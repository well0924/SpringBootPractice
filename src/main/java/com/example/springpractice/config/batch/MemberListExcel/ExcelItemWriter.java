package com.example.springpractice.config.batch.MemberListExcel;

import com.example.springpractice.domain.dto.MemberExcelDto;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.batch.item.ItemWriter;

import java.io.FileOutputStream;
import java.util.List;

public class ExcelItemWriter implements ItemWriter<List<MemberExcelDto>> {
    private static final String path = "output-members.xlsx";

    @Override
    public void write(List<? extends List<MemberExcelDto>> items) throws Exception {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            int sheetNum = 1;

            for (List<MemberExcelDto> memberDtos : items) {
                // Create a sheet
                Row headerRow = workbook.createSheet("Members_" + sheetNum++).createRow(0);

                // Write header
                headerRow.createCell(0).setCellValue("id");
                headerRow.createCell(1).setCellValue("MemberId");
                headerRow.createCell(2).setCellValue("MemberName");
                headerRow.createCell(3).setCellValue("MemberEmail");
                headerRow.createCell(4).setCellValue("MemberPhone");
                headerRow.createCell(5).setCellValue("Role");
                headerRow.createCell(6).setCellValue("userState");

                // Write member data
                int rowNum = 1;

                for (MemberExcelDto memberDto : memberDtos) {
                    Row row = workbook.getSheetAt(sheetNum - 2).createRow(rowNum++);
                    row.createCell(0).setCellValue(memberDto.getId());
                    row.createCell(1).setCellValue(memberDto.getMemberId());
                    row.createCell(2).setCellValue(memberDto.getMemberName());
                    row.createCell(3).setCellValue(memberDto.getMemberEmail());
                    row.createCell(4).setCellValue(memberDto.getMemberPhone());
                    row.createCell(5).setCellValue(memberDto.getRole().getRole());
                    row.createCell(6).setCellValue(memberDto.getUserState().name());
                }
            }

            // Save workbook to file
            try (FileOutputStream fileOut = new FileOutputStream(path)) {
                workbook.write(fileOut);
            }
        }
    }
}
