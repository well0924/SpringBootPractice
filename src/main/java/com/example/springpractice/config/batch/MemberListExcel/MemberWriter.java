package com.example.springpractice.config.batch.MemberListExcel;

import com.example.springpractice.domain.dto.MemberExcelDto;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.util.List;

@Component
public class MemberWriter implements ItemWriter<List<MemberExcelDto>> {
    private static final String FILE_PATH = "src/main/resources/memberList.xlsx";

    @Override
    public void write(List<? extends List<MemberExcelDto>> items) throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Members");

        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("번호");
        headerRow.createCell(1).setCellValue("아이디");
        headerRow.createCell(2).setCellValue("이름");
        headerRow.createCell(3).setCellValue("이메일");
        headerRow.createCell(4).setCellValue("전화번호");

        int rowNum = 1;
        for (List<MemberExcelDto> memberList : items) {
            for (MemberExcelDto memberDTO : memberList) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(memberDTO.getId());
                row.createCell(1).setCellValue(memberDTO.getMemberId());
                row.createCell(2).setCellValue(memberDTO.getMemberName());
                row.createCell(3).setCellValue(memberDTO.getMemberEmail());
                row.createCell(4).setCellValue(memberDTO.getMemberPhone());
            }
        }

        try (FileOutputStream fileOut = new FileOutputStream(FILE_PATH)) {
            workbook.write(fileOut);
        }

        workbook.close();
    }
}
