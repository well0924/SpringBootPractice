package com.example.springpractice.domain;

import com.example.springpractice.config.base.BaseTime;
import com.example.springpractice.config.constant.Role;
import com.example.springpractice.config.constant.UserState;
import com.example.springpractice.domain.dto.MemberRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@NoArgsConstructor
public class Member extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String memberId;
    private String password;
    private String memberName;
    private String memberPhone;
    private String memberEmail;
    //회원 상태 (휴먼계정,계정 잠금)
    @Enumerated(EnumType.STRING)
    private UserState userState;
    //회원 등급
    @Enumerated(EnumType.STRING)
    private Role role;

    private boolean enabled;

    @Setter
    @Column
    private boolean accountNonLocked;

    @Setter
    @Column(columnDefinition = "Integer default 0")
    private int failCount;

    @Setter
    private Date lockTime;

    private String provider; //어떤 OAuth인지(google, naver 등)

    private String provideId; // 해당 OAuth 의 key(id)

    @Builder
    public Member(Long id, String memberId, String password,String memberName, String memberEmail, String memberPhone,Role role,UserState userState,boolean enabled,boolean accountNonLocked,int failCount,
                  Date lockTime,String provider,String provideId) {
        this.id = id;
        this.memberId = memberId;
        this.password = password;
        this.memberName = memberName;
        this.memberEmail = memberEmail;
        this.memberPhone = memberPhone;
        this.getCreatedTime();
        this.getUpdatedTime();
        this.role = role;
        this.userState = userState;
        this.enabled = enabled;
        this.accountNonLocked = accountNonLocked;
        this.failCount = failCount;
        this.lockTime = lockTime;
        this.provider = provider;
        this.provideId = provideId;
    }

    //회원 휴먼 상태 및 계정 잠금 변환
    public Member setUserState(){
        userState = UserState.NONHUMAN;
        return this;
    }

    public void memberUpdate(MemberRequest member){
        this.memberId = member.getMemberId();
        this.memberName = member.getMemberName();
        this.memberEmail = member.getMemberEmail();
        this.memberPhone = member.getMemberPhone();
    }
}
