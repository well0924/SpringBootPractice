package com.example.springpractice.domain.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberRequest {
    @NotBlank(message = "회원 아이디를 입력해 주세요.")
    private String memberId;
    @NotBlank(message = "회원 이름을 입력해 주세요.")
    private String memberName;
    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,16}", message = "비밀번호는 8~16자 영문 대 소문자, 숫자, 특수문자를 사용하세요.")
    private String password;
    @Email(message = "이메일을 입력해주세요.")
    private String memberEmail;
    @NotBlank(message = "전화번호를 입력해 주세요.")
    private String memberPhone;

}
