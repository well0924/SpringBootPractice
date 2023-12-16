package com.example.springpractice.config.batch.IsHuman;

import com.example.springpractice.config.batch.SendMail.HumanEmailSender;
import com.example.springpractice.domain.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Log4j2
@Configuration
@RequiredArgsConstructor
public class InactiveUserJobConfig {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final HumanActive humanActive;
    private final HumanEmailSender humanEmailSender;

    //시나리오는 반년간 활동이 없는 회원을 조회
    //flow 를 사용하기.
    @Bean
    public Job inactiveUserJob() throws Exception {
        return jobBuilderFactory
                .get("inactiveUserJob")
                .preventRestart()
                .incrementer(new RunIdIncrementer())
                .start(inactiveJobStep())
                .build();

    }

    @Bean
    public Step inactiveJobStep(){
        return stepBuilderFactory
                .get("inactiveUserStep")
                .<Member,Member>chunk(10)
                .reader(humanActive.inactiveUserReader())//reader
                .processor(humanActive.inactiveUserProcessor())//processor
                .writer(humanActive.inactiveUserWriter())//writer
                .build();
    }

    @Bean
    public Step emailSendJopStep(){
        return stepBuilderFactory
                .get("emailSendJopStep")
                .<Member,Member>chunk(10)
                .reader(humanEmailSender.humanUserReader())
                .writer(humanEmailSender.inactiveUserEmailSender())
                .build();
    }
}
