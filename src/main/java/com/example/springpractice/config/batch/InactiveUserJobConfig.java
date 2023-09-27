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
    public Job inactiveUserJob() {
        return jobBuilderFactory.get("inactiveUserJob3")
                .preventRestart()
                .start(inactiveJobStep(null))
                .build();
    }

    @Bean
    public Step inactiveJobStep(@Value("#{jobParameters[requestDate]}") final String requestDate) {
        log.info("requestDate: {}", requestDate);
        return stepBuilderFactory.get("inactiveUserStep")
                .<Member,Member>chunk(10)
                .faultTolerant()
                .retryLimit(3)
                .retry(Exception.class)
                .reader(inactiveUserReader())
                .processor(inactiveUserProcessor())
                .writer(inactiveUserWriter())
                .build();
    }

    @Bean
    @StepScope //(1)
    public QueueItemReader<Member> inactiveUserReader() {
        //(2)
        List<Member> oldUsers =
                memberRepository.findByUpdatedTimeBeforeAndUserStateEquals(
                        LocalDateTime.now().minusYears(1),
                        UserState.HUMAN);

        return new QueueItemReader<>(oldUsers); //(3)
    }

    public ItemProcessor<Member,Member> inactiveUserProcessor() {
        return new org.springframework.batch.item.ItemProcessor<Member,Member>() {
            @Override
            public Member process(Member member) throws Exception {
                return member.setUserState();
            }
        };
    }

    public ItemWriter<Member> inactiveUserWriter() {
        return ((List<? extends Member> members) -> memberRepository.saveAll(members));
    }
}
