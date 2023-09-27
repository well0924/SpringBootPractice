package com.example.springpractice.config.security.handler;

import com.example.springpractice.config.security.auth.CustomUserDetails;
import com.example.springpractice.domain.Member;
import com.example.springpractice.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Set;

@Component
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    @Autowired
    private MemberService userService;

    private RequestCache requestCache = new HttpSessionRequestCache();
    private RedirectStrategy redirectStratgy = new DefaultRedirectStrategy();
    private static final String DEFAULT_URL= "/member-success";
    private static final String ADMIN_URL="/admin-success";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        CustomUserDetails userDetails =  (CustomUserDetails) authentication.getPrincipal();

        Member member = userDetails.getMember();
        //실패 횟수가 0보다 높으면 실패 횟수를 리셋
        if (member.getFailCount() > 0) {
            userService.resetFailedAttempts(member.getMemberEmail());
        }

        HttpSession session = request.getSession(false);

        if(session != null) {
            //세션이 null이 아닌경우에는 세션을 제거.
            session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        }

        //권한에 따라서 특정 페이지로 이동을 하기.
        try {
            //권한별 페이지 이동
            redirectStrategy(request, response, authentication);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //로그인을 했을시 role에 의해서 특정url로 이동하는 메서드
    private void redirectStrategy(HttpServletRequest request,HttpServletResponse response,Authentication authentication)throws Exception {

        SavedRequest savedRequest = requestCache.getRequest(request, response);

        if(savedRequest != null) {
            redirectStratgy.sendRedirect(request, response, DEFAULT_URL);
        }else {
            Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
            //권한이 ADMIN이면 어드민 페이지로 이동
            if(roles.contains("ROLE_ADMIN")) {
                redirectStratgy.sendRedirect(request, response,ADMIN_URL);
                //권한이 USER이면 메인 페이지로 이동
            }else if(roles.contains("ROLE_USER")) {
                redirectStratgy.sendRedirect(request, response,DEFAULT_URL);
            }
        }

    }
}
