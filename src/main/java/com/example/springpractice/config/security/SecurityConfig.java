package com.example.springpractice.config.security;

import com.example.springpractice.config.security.auth.CustomUserDetailService;
import com.example.springpractice.config.security.handler.CustomFailHandler;
import com.example.springpractice.config.security.handler.CustomSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.firewall.DefaultHttpFirewall;
import org.springframework.security.web.firewall.HttpFirewall;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Order(SecurityProperties.DEFAULT_FILTER_ORDER)
public class SecurityConfig {

    private final CustomUserDetailService customUserDetailService;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfiguration) throws Exception {
        return authConfiguration.getAuthenticationManager();
    }

    @Autowired
    private CustomFailHandler loginFailureHandler;

    @Autowired
    private CustomSuccessHandler loginSuccessHandler;

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web
                .httpFirewall(defaultFireWell())
                .ignoring()
                .antMatchers("/images/**", "/js/**","/font/**", "/webfonts/**","/istatic/**",
                        "/main/**", "/webjars/**", "/dist/**", "/plugins/**", "/css/**","/favicon.ico","/h2-console/**");
    }

    @Bean
    public AuthenticationProvider authProvider(){
        //디비에 저장된 사용자 정보를 비교하는데 사용.
        //그 밖에도 AuthenticationProvider는 여러가지 타입으로 있다.
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        //프로바이더에서 UserDeailService를 실행
        provider.setUserDetailsService(customUserDetailService);
        provider.setPasswordEncoder(bCryptPasswordEncoder());
        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                //csrf 토큰 비활성화
                .csrf().disable()
                .authorizeRequests()
                //추후에 접근 경로 설정 필요.
                .antMatchers("/**").permitAll()
                .anyRequest().authenticated();

        http
                .formLogin()
                .loginPage("/login")
                .usernameParameter("memberEmail")
                .passwordParameter("password")
                .loginProcessingUrl("/login/action")
                .defaultSuccessUrl("/login")
                .successHandler(loginSuccessHandler)//로그인에 성공시 핸들러(계정잠금횟수리셋 + 권한에 따른 페이지 이동)
                .failureHandler(loginFailureHandler)//로그인에 실패시 핸들러(계정잠금 + 로그인 실패시 에러 메시지)
                .permitAll();

        http
                .logout()
                .logoutUrl("/logout")//로그아웃을 하는데 필요한 url
                .logoutSuccessUrl("/login")
                .deleteCookies("JSESSIONID")//로그아웃을 했을시 저장된 쿠키를 삭제
                .invalidateHttpSession(true);//로그아웃시 저장된 세션을 제거

        return http.build();
    }

    @Bean
    public HttpFirewall defaultFireWell(){
        return new DefaultHttpFirewall();
    }
}
