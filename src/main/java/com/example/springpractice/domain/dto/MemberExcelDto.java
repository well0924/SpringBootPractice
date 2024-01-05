package com.example.springpractice.domain.dto;

import com.example.springpractice.config.constant.Role;
import com.example.springpractice.config.constant.UserState;
import com.example.springpractice.domain.Member;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberExcelDto {
    private Long id;
    private String memberId;
    private String memberName;
    private String memberPhone;
    private String memberEmail;

    @Builder
    public MemberExcelDto(Member member){
        this.id = member.getId();
        this.memberId = member.getMemberId();
        this.memberName = member.getMemberName();
        this.memberEmail = member.getMemberEmail();
        this.memberPhone = member.getMemberPhone();
    }
}
