# 스프링 시큐리티 및 배치 연습

스프링 시큐리티와 스프링 배치를 연습하는 프로젝트입니다. 

## 학습 목표

* [x] 회원 crud.
* [x] 시큐리티 로그인 및 로그아웃.
* [x] 회원이 로그인을 했을 경우 로그인을 3회 실패했을시 계정을 잠금.
* [x] 로그인을 성공했을시 로그인 실패 횟수 리셋.
* [x] 로그인을 했을시 권한에 맞게 페이지로 이동을 하기.
* [x] 로그인 실패시 에러 메시지 나오게 하기.
* [x] 자동 로그인기능 만들기.(remember-me 기능).
* [x] 소셜 로그인을 통해서 로그인을 하기.(카카오).
* [x] 회원 가입을 했을시 특정 시간이 지나면 회원을 휴먼 회원으로 변경.
* [x] 회원 목록을 csv 파일로 저장.
* [x] 휴먼회원이 되었을시 해당 회원에게 이메일을 발송하기.
* [x] 휴먼회원을 해제하는 기능을 만들기.
* [x] 회원 목록을 엑셀파일로 만들기.
* [x] 스프링 배치 예외처리

## 시나리오

~~1.회원가입 후 로그인을 실행~~ v

~~2.로그인 방식은 일반적인 로그인과 소셜 로그인(카카오를 활용)~~ v

~~3.로그인에 3번 실패시 회원의 계정을 24시간 잠금 ->계정 상태 USER_LOCK~~ v

~~4.회원의 활동이 일년간 활동이 없는 경우에는 회원 계정 상태를 휴먼상태로 변경.~~ v

~~5.추가로 휴먼상태로 변경 되었을 때 동시에 휴먼회원이 되었다는 이메일을 전송하기.~~ v

~~6.회원엔티티에 저장된 회원을 csv파일로 저장하기. (스케줄러를 사용해서 매일 새벽4에 작동하게끔 하기)v~~

~~7.회원의 정보를 엑셀 파일로 만든 뒤 저장하기.v~~


## 스프링 시큐리티 흐름

<img src="https://github.com/well0924/SpringBootPractice/assets/89343159/5f5d78a6-706e-47c2-ae08-4b027ea33bf1" width="400" height="400">

## OAuth2 흐름

<img src="https://github.com/well0924/SpringBootPractice/assets/89343159/484865da-2d6a-45a3-be45-38db6ab6b00f" width="400" height="400">

## 스프링 배치 흐름

<img src="https://github.com/well0924/SpringBootPractice/assets/89343159/e8f22db6-6494-4d3e-a868-33ac454152bb" width="400" height="400">

## 구현 화면

1. 로그인 (회원) 

<img src="https://github.com/well0924/jpapractice/assets/89343159/ba6ee860-a2b9-4f76-8814-94d2a113bb2c">

2. 로그인 (관리자)

<img src="https://github.com/well0924/jpapractice/assets/89343159/53acb864-ea07-4621-98e6-2f323887cd29">


3. 비밀번호를 3회 실패한 경우 계정 잠금

<img src="https://github.com/well0924/jpapractice/assets/89343159/e1e66d73-8483-4cc2-9e51-cb628ef6cfb2">

4. remember-me 기능

<img src="https://github.com/well0924/jpapractice/assets/89343159/6abae093-bc83-4ddc-bf47-a1ff802b6970">

5. socialLogin 기능 (kakao)

<img src="https://github.com/well0924/jpapractice/assets/89343159/a7a5ac73-f719-42ab-959e-0a82caa38d44">

5. 회원 가입일로부터 특정기간동안 활동이 없는경우에는 회원을 휴먼회원으로 변경하기

작성코드 
```
@Log4j2
@Configuration
@RequiredArgsConstructor
public class InactiveUserJobConfig {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final MemberRepository memberRepository;

    @Bean
    public Job inactiveUserJob() throws Exception {
        return jobBuilderFactory.get("inactiveUserJob3")
                .preventRestart()//job의 재실행을 막기.
                .start(inactiveJobStep())//job을 시작하기.(휴먼회원 변경)
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    public Step inactiveJobStep()throws Exception {
        return stepBuilderFactory.get("inactiveUserStep")
                .<Member,Member>chunk(10)
                .reader(inactiveUserReader())
                .processor(inactiveUserProcessor())
                .writer(inactiveUserWriter())
                .build();
    }

    @Bean
    //@StepScope
    public ItemWriter<Member> inactiveUserWriter() {
        return ((List<? extends Member> members) -> memberRepository.saveAll(members));
    }

    @Bean
    //@StepScope
    public ItemProcessor<Member,Member> inactiveUserProcessor() {
        return Member::setUserStateHuman;
    }

    @Bean
    //@StepScope //(1)
    public QueueItemReader<Member> inactiveUserReader() {
        //(2)
        List<Member> oldUsers =
                memberRepository.findByUpdatedTimeBeforeAndUserStateEquals(
                        LocalDateTime.now().minusYears(1),
                        UserState.NONHUMAN);
        log.info(oldUsers.size());
        return new QueueItemReader<>(oldUsers); //(3)
    }
}
```

6. 회원 목록을 csv파일로 만들기.

작성 코드
```
@Log4j2
@Configuration
@RequiredArgsConstructor
public class MemberJobConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final MemberRepository memberRepository;
    private String[] names;

    @Bean
    public Job itemWriterJob()throws Exception{
        return this.jobBuilderFactory
                .get("itemWriterJob")
                .preventRestart()
                .incrementer(new RunIdIncrementer())
                .start(this.csvItemWriterStep())
                .build();
    }
    @Bean
    public Step csvItemWriterStep() throws Exception {
        return  this.stepBuilderFactory.get("csvItemWriterStep")
                .<Member,Member>chunk(10)
                .reader(itemReader())
                .writer(csvFileItemWriter())
                .build();
    }
    private ItemReader<Member>itemReader(){
        return new ListItemReader<>(getItems());
    }
    private List<Member>getItems(){
        return memberRepository.findAll();
    }
    public void setNames(String[]names){
        Assert.notNull(names,"Names must be non-null");
        this.names = Arrays.asList(names).toArray(new String[names.length]);
    }
    private ItemWriter<? super Member>csvFileItemWriter() throws Exception {

        BeanWrapperFieldExtractor<Member>memberBeanWrapperFieldExtractor = new BeanWrapperFieldExtractor<>();

        memberBeanWrapperFieldExtractor.setNames(new String[]{"memberId","memberName","memberPhone","memberEmail","Role","UserState"});

        DelimitedLineAggregator<Member>delimitedLineAggregator = new DelimitedLineAggregator<>();
        delimitedLineAggregator.setDelimiter(",");
        delimitedLineAggregator.setFieldExtractor(memberBeanWrapperFieldExtractor);

        FlatFileItemWriter<Member>flatFileItemWriter = new FlatFileItemWriterBuilder<Member>()
                .name("csvFileItemWriter")
                .encoding("UTF-8")
                .resource(new FileSystemResource("src/main/resources/test-data.csv"))
                .lineAggregator(delimitedLineAggregator)
                .headerCallback(writer -> writer.write("memberId,memberName,memberPhone,memberEmail,Role,UserState"))
                .append(true)
                .build();
        flatFileItemWriter.afterPropertiesSet();

        return flatFileItemWriter;
    }
}
```

결과물 

[test-data.csv](src%2Fmain%2Fresources%2Ftest-data.csv)

7. 회원 정보를 엑셀파일로 저장하기.

