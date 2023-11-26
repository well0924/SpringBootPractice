package com.example.springpractice.config.batch;

import com.example.springpractice.config.constant.UserState;
import com.example.springpractice.domain.Member;
import com.example.springpractice.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.List;

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
