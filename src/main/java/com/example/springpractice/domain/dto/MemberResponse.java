package com.example.springpractice.domain.dto;

import com.example.springpractice.config.base.BaseTime;
import com.example.springpractice.config.constant.Role;
import com.example.springpractice.config.constant.UserState;
import com.example.springpractice.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@AllArgsConstructor
public class MemberResponse extends BaseTime {
    private Long id;
    private String memberId;
    private String memberName;
    private String memberPhone;
    private String memberEmail;
    //회원 상태 (휴먼계정,계정 잠금)
    private UserState userState;
    //회원 등급
    private Role role;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
    private boolean accountNonLocked;
    private int failCount;
    private Date lockTime;

    public MemberResponse(Member member){
        this.id = member.getId();
        this.memberId = member.getMemberId();
        this.memberName = member.getMemberName();
        this.memberEmail = member.getMemberEmail();
        this.memberPhone = member.getMemberPhone();
        this.role = member.getRole();
        this.userState = member.getUserState();
        this.createdTime = member.getCreatedTime();
        this.updatedTime = member.getUpdatedTime();
        this.accountNonLocked = member.isAccountNonLocked();
        this.failCount = member.getFailCount();
        this.lockTime = member.getLockTime();
    }
}
