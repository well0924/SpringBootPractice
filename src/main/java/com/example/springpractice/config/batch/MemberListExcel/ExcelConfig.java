package com.example.springpractice.config.batch.MemberListExcel;

import com.example.springpractice.domain.dto.MemberExcelDto;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@EnableBatchProcessing
public class ExcelConfig {

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    private final MemberItemReader memberItemReader;

    private final MemberProcessor memberItemProcessor;

    private final MemberWriter memberItemWriter;

    @Bean
    public Job importMemberJob() throws Exception {
        return  jobBuilderFactory.get("importMemberJob")
                .incrementer(new RunIdIncrementer())
                .preventRestart()
                .flow(step())
                .end()
                .build();
    }

    @Bean
    public Step step() throws Exception {
        return stepBuilderFactory.get("step")
                .<List<MemberExcelDto>, List<MemberExcelDto>>chunk(10000)
                .reader((ItemReader<? extends List<MemberExcelDto>>) memberItemReader.read())
                .processor(memberItemProcessor)
                .writer(memberItemWriter)
                .build();
    }


}
