package com.example.springpractice;

import com.example.springpractice.config.constant.Role;
import com.example.springpractice.config.constant.UserState;
import com.example.springpractice.config.security.BcryptPasswordEncoderConfig;
import com.example.springpractice.domain.Member;
import com.example.springpractice.repository.MemberRepository;
import org.hibernate.internal.build.AllowPrintStacktrace;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SpringBootTest
class SpringPracticeApplicationTests {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private BcryptPasswordEncoderConfig config;
    @Test
    void contextLoads() {
    }
}
