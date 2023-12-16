package com.example.springpractice.config.batch.MemberListExcel;

import com.example.springpractice.domain.Member;
import com.example.springpractice.domain.dto.MemberExcelDto;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Log4j2
@Configuration
@AllArgsConstructor
public class ExcelConfig {

    private final JobLauncher jobLauncher;
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public ExcelItemReader reader() {
        return new ExcelItemReader();
    }

    @Bean
    public ItemWriter<List<MemberExcelDto>> writer() {
        return new ExcelItemWriter();
    }

    @Bean
    public Step step(ItemReader<List<Member>> reader, ItemWriter<List<MemberExcelDto>> writer) {
        return stepBuilderFactory.get("step")
                .<List<Member>, List<MemberExcelDto>>chunk(10)
                .reader(reader)
                .writer(writer)
                .build();
    }

    @Bean
    public Job job(Step step) {
        return jobBuilderFactory.get("job")
                .flow(step)
                .end()
                .build();
    }

    public void launchJob() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();

        jobLauncher.run(job(step(reader(),writer())), jobParameters);
    }
}
