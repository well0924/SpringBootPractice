package com.example.springpractice.config.security.handler;

import com.example.springpractice.config.security.auth.CustomUserDetails;
import com.example.springpractice.domain.Member;
import com.example.springpractice.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    @Autowired
    private MemberService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        CustomUserDetails userDetails =  (CustomUserDetails) authentication.getPrincipal();

        Member member = userDetails.getMember();
        //실패 횟수가 0보다 높으면 실패 횟수를 리셋
        if (member.getFailCount() > 0) {
            userService.resetFailedAttempts(member.getMemberEmail());
        }
        //권한에 따라서 특정 페이지로 이동을 하기.

        super.onAuthenticationSuccess(request, response, authentication);
    }
}
