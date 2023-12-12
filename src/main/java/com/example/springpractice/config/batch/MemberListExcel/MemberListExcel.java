package com.example.springpractice.config.batch.MemberListExcel;

import com.example.springpractice.domain.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import javax.persistence.EntityManagerFactory;
import java.util.Arrays;
import java.util.Iterator;

@Log4j2
@Configuration
@RequiredArgsConstructor
public class MemberListExcel {

    private final JobLauncher jobLauncher;
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;
    private final ExcelListener excelListener;
    private final static int chunkSize = 10;
    private int rowIdx = 0;

    @Bean(name = "excelJob")
    public Job excelJob(Step excelStep) throws Exception {
        return jobBuilderFactory
                .get("excelJob")
                .incrementer(new RunIdIncrementer())
                .flow(excelStep)
                .end()
                .build();
    }

    @Bean
    @JobScope
    public Step excelStep() throws Exception {
        return stepBuilderFactory
                .get("excelStep")
                .<Member,Member>chunk(chunkSize)
                .reader(excelReader())
                .writer(excelWriter())
                .listener(excelListener)
                .build();
    }

    //엑셀 파일 읽기.
    private ItemReader<? extends Member>excelReader() throws Exception {
        JpaPagingItemReader reader = new JpaPagingItemReaderBuilder<Member>()
                .pageSize(chunkSize)
                .entityManagerFactory(entityManagerFactory)
                .queryString("select m.id,m.memberId,m.memberName,m.memberPhone,m.memberEmail,m.role,m.userState,m.accountNonLocked from Member m")
                .name("getTargetReader")
                .build();
        reader.afterPropertiesSet();
        return reader;
    }

    private ItemWriter<Member>excelWriter(){
        return items ->{
            Row row = null;
            Cell cell = null;
            Iterator e = items.iterator();
            while(e.hasNext()){
                row = excelListener.sheet.createRow(rowIdx++);
                Object[] t = (Object[]) e.next();

                for(int i = 0; i< Arrays.stream(t).count(); i++){
                    cell = row.createCell(i);
                    cell.setCellValue(t[i].toString());
                }
            }
        };
    }

    @Scheduled(cron = "0/1 * * * * *")
    public void runExportMembersJob() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis()) // 고유한 Job Parameter 추가
                .toJobParameters();

        JobExecution jobExecution = jobLauncher.run(excelJob(null), jobParameters);

        // Job 실행 결과 확인
        BatchStatus batchStatus = jobExecution.getStatus();
        System.out.println("Job Exit Status: " + batchStatus);
    }
}
