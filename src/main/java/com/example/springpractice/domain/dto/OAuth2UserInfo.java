package com.example.springpractice.domain.dto;

public interface OAuth2UserInfo {
    String getProviderId();//공급자 id
    String getProvider();//공급자
    String getEmail();//사용자 이메일
    String getName();//사용자 이름
}
