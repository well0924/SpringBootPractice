package com.example.springpractice.config.batch.MemberListExcel;

import com.example.springpractice.domain.dto.MemberExcelDto;
import lombok.extern.log4j.Log4j2;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.util.List;

@Log4j2
@Component
public class MemberProcessor implements ItemProcessor<List<MemberExcelDto>,List<MemberExcelDto>> {
    @Override
    public List<MemberExcelDto> process(List<MemberExcelDto> item) throws Exception {
        log.info(item);
        return item;
    }
}
