package com.example.springpractice.config.security.handler;

import com.example.springpractice.domain.Member;
import com.example.springpractice.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomFailHandler extends SimpleUrlAuthenticationFailureHandler {

    @Autowired
    private MemberService memberService;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException ,LockedException{
        String email = request.getParameter("memberEmail");
        String errorMessage ="";
        //이메일로 회원의 정보를 가져오기.
        Member member = memberService.getEmail(email);
        //회원의 정보가 있는 경우 회원의 로그인 실패 횟수를 증가
        if (member != null) {
            if (member.isEnabled() && member.isAccountNonLocked()) {
                if (member.getFailCount() < memberService.MAX_FAILED_ATTEMPTS - 1) {
                    memberService.increaseFailedAttempts(member);
                } else {
                    //로그인 실패 횟수를 넘는경우 계정을 잠금.
                    memberService.lock(member);
                    exception = new LockedException("당신의 계정은 3회 실패로 계정이 잠겼습니다. 계정해제는 24시간후에 잠금이 풀립니다.");
                }
            //계정이 잠겨있지 않은 경우     
            } else if (member.isAccountNonLocked()) {
                //잠금기간이 해제된 경우
                if (memberService.unlockWhenTimeExpired(member)) {
                    exception = new LockedException("당신의 계정이 해제되었습니다. 다시 한번 로그인을 시도해 주세요.");
                }
            }
        }
        //로그인을 실패했을시 에러 메시지를 출력하는 기능.
        if (exception instanceof BadCredentialsException) {
            exception = new BadCredentialsException("잘못 입력했습니다.");
        } else if (exception instanceof InternalAuthenticationServiceException) {
            exception = new InternalAuthenticationServiceException("문제");
        } else if (exception instanceof UsernameNotFoundException) {
            exception = new UsernameNotFoundException("회원이 없습니다.");
        } else if (exception instanceof AuthenticationCredentialsNotFoundException) {
            exception = new AuthenticationCredentialsNotFoundException("인증에 문제가 있습니다.");
        } else {
            errorMessage = "관리자에게 문의해 주세요.";
        }
        super.setDefaultFailureUrl("/login?error");
        super.onAuthenticationFailure(request, response, exception);
    }
}
