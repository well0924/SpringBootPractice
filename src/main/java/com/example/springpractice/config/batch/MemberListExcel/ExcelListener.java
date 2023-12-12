package com.example.springpractice.config.batch.MemberListExcel;

import lombok.extern.log4j.Log4j2;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.IOException;

@Log4j2
@Component
public class ExcelListener implements StepExecutionListener {

    public XSSFWorkbook workbook;
    public XSSFSheet sheet;

    private String fileName = "test.xlsx";
    private String outPath = "src/main/resources/" + fileName;

    @Override
    public void beforeStep(StepExecution stepExecution) {
        log.info("### before step!! ###");

        workbook = new XSSFWorkbook();
        sheet = workbook.createSheet();
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        log.info("### after step!! ###");

        try{
            FileOutputStream out = new FileOutputStream(outPath);
            workbook.write(out);
            workbook.close();
            out.close();
            return ExitStatus.COMPLETED;
        }catch (IOException e){
            log.error("@@@ IOException "+e.getMessage());
            return ExitStatus.FAILED;
        }
    }
}
