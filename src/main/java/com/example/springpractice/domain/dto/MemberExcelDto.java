package com.example.springpractice.domain.dto;

import com.example.springpractice.config.constant.Role;
import com.example.springpractice.config.constant.UserState;
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
    private UserState userState;
    private Role role;

}
