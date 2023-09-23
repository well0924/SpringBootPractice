package com.example.springpractice.config.security.auth;

import com.example.springpractice.domain.Member;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.ArrayList;
import java.util.Collection;

@Data
public class CustomUserDetails implements UserDetails {

    private Member member;

    public CustomUserDetails(Member member){
        this.member = member;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collectors = new ArrayList<>();
        collectors.add(()-> member.getRole().getRole());
        return collectors;
    }

    //로그인을 할 경우에 필요한 패스워드
    @Override
    public String getPassword() {
        return member.getPassword();
    }

    //로그인에 필요한 아이디
    @Override
    public String getUsername() {
        return member.getMemberId();
    }

    //계정이 만료가 되지 않았는지 확인
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    //계정이 잠겼는지 확인
    @Override
    public boolean isAccountNonLocked() {
        return member.isAccountNonLocked();
    }

    //
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    //계정이 사용가능한지 ->값이 false인 경우에는 해당 계정을 사용할 수 없음.
    @Override
    public boolean isEnabled() {
        return member.isEnabled();
    }
}
