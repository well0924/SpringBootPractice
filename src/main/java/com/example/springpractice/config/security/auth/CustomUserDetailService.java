package com.example.springpractice.config.security.auth;

import com.example.springpractice.domain.Member;
import com.example.springpractice.repository.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Log4j2
@Service
@AllArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //로그인시 회원의 이메일로 회원의 정보를 인증하기.
        Optional<Member>member = Optional.ofNullable(memberRepository
                .findByMemberEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("회원이 없습니다.")));

        log.info("loginResult:"+member.get());

        return new CustomUserDetails(member.get());
    }
}
