package com.example.springpractice;

import com.example.springpractice.config.batch.MemberListCsv.JobScheduler;
import com.example.springpractice.config.batch.MemberListExcel.ExcelConfig;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableBatchProcessing
@EnableJpaAuditing
@SpringBootApplication
public class SpringPracticeApplication {

    public static void main(String[] args) throws Exception {

        ConfigurableApplicationContext context = SpringApplication.run(SpringPracticeApplication.class, args);

        JobScheduler config = context.getBean(JobScheduler.class);
        config.memberListCsvScheduler();
    }

}
