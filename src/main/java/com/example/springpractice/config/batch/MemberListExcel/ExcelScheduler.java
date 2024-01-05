package com.example.springpractice.config.batch.MemberListExcel;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Log4j2
@Component
@RequiredArgsConstructor
public class ExcelScheduler {

    private final ExcelConfig excelConfig;

    private final JobLauncher jobLauncher;

    public void memberExcelScheduler(){
        // 넘기는 파라미터를 매번 다르게 해서 별개의 JobInstance로 인식하게 함
        Map<String, JobParameter> scheduleMap = new HashMap<>();
        scheduleMap.put("time", new JobParameter(System.currentTimeMillis()));
        JobParameters jobParameters = new JobParameters(scheduleMap);

        try {
            JobExecution jobExecution = jobLauncher.run(excelConfig.importMemberJob(), jobParameters);

            log.info("Job Execution: " + jobExecution.getStatus());
            log.info("Job getJobConfigurationName: " + jobExecution.getJobConfigurationName());
            log.info("Job getJobId: " + jobExecution.getJobId());
            log.info("Job getExitStatus: " + jobExecution.getExitStatus());
            log.info("Job getJobInstance: " + jobExecution.getJobInstance());
            log.info("Job getStepExecutions: " + jobExecution.getStepExecutions());
            log.info("Job getLastUpdated: " + jobExecution.getLastUpdated());
            log.info("Job getFailureExceptions: " + jobExecution.getFailureExceptions());

        } catch (JobExecutionAlreadyRunningException
                 | JobRestartException
                 | JobInstanceAlreadyCompleteException
                 | JobParametersInvalidException e) {
            e.getMessage();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
