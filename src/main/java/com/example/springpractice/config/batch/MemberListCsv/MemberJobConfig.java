package com.example.springpractice.config.batch.MemberListCsv;

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
public class MemberJobConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final MemberListCsv memberListCsv;

    @Bean
    public Job itemWriterJob()throws Exception{
        return this.jobBuilderFactory
                .get("itemWriterJob")
                .preventRestart()
                .incrementer(new RunIdIncrementer())//job 실행할 때마다 파라미터 id를 자동생성
                .start(this.csvItemWriterStep())//csv파일 step시작
                .build();
    }

    @Bean
    public Step csvItemWriterStep() throws Exception {
        return  this.stepBuilderFactory
                .get("csvItemWriterStep")
                .<Member,Member>chunk(10)
                .reader(memberListCsv.itemReader())
                .writer(memberListCsv.csvFileItemWriter())
                .build();
    }

}
