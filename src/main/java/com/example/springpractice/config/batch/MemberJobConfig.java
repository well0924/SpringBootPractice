package com.example.springpractice.config.batch;

import com.example.springpractice.domain.Member;
import com.example.springpractice.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.List;

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
