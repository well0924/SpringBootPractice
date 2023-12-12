package com.example.springpractice.config.batch.MemberListCsv;

import com.example.springpractice.domain.Member;
import com.example.springpractice.repository.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

import java.util.List;


@Log4j2
@Component
@AllArgsConstructor
public class MemberListCsv {

    private final MemberRepository memberRepository;

    @Bean
    public ItemReader<Member> itemReader(){
        return new ListItemReader<>(getItems());
    }

    public List<Member> getItems(){
        return memberRepository.findAll();
    }

    @Bean
    public ItemWriter<? super Member> csvFileItemWriter() throws Exception {

        BeanWrapperFieldExtractor<Member> memberBeanWrapperFieldExtractor = new BeanWrapperFieldExtractor<>();

        memberBeanWrapperFieldExtractor.setNames(new String[]{"memberId","memberName","memberPhone","memberEmail","Role","UserState"});

        DelimitedLineAggregator<Member> delimitedLineAggregator = new DelimitedLineAggregator<>();
        delimitedLineAggregator.setDelimiter(",");
        delimitedLineAggregator.setFieldExtractor(memberBeanWrapperFieldExtractor);

        //csv 나 xml 파일을 작성할 때 사용하는 Writer
        FlatFileItemWriter<Member> flatFileItemWriter = new FlatFileItemWriterBuilder<Member>()
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
