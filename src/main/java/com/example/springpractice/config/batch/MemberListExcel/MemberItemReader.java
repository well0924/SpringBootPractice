package com.example.springpractice.config.batch.MemberListExcel;

import com.example.springpractice.domain.Member;
import com.example.springpractice.domain.dto.MemberExcelDto;
import com.example.springpractice.repository.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@Component
@AllArgsConstructor
public class MemberItemReader implements ItemReader<List<MemberExcelDto>> {

    private final MemberRepository memberRepository;

    @Override
    public List<MemberExcelDto> read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        List<Member>list = memberRepository.findAll();
        List<MemberExcelDto> dto = new ArrayList<>();

        for(Member member : list){
            MemberExcelDto result = new MemberExcelDto();
            result.setId(member.getId());
            result.setMemberId(member.getMemberId());
            result.setMemberName(member.getMemberName());
            result.setMemberEmail(member.getMemberEmail());
            result.setMemberPhone(member.getMemberPhone());
            dto.add(result);
        }

        log.info("목록::"+dto);

        return dto;
    }
}
