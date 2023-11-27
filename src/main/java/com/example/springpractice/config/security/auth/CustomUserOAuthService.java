package com.example.springpractice.config.security.auth;

import com.example.springpractice.config.constant.Role;
import com.example.springpractice.config.constant.UserState;
import com.example.springpractice.domain.Member;
import com.example.springpractice.domain.dto.KakaoUserInfo;
import com.example.springpractice.domain.dto.OAuth2UserInfo;
import com.example.springpractice.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserOAuthService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bcryptPasswordEncoder;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        System.out.println("getClientRegistration: "+userRequest.getClientRegistration());
        System.out.println("getAccessToken: "+userRequest.getAccessToken().getTokenValue());
        System.out.println("getAttributes: "+ super.loadUser(userRequest).getAttributes());

        OAuth2User oAuth2User = super.loadUser(userRequest);
        OAuth2UserInfo oAuth2UserInfo =null;

        if(userRequest.getClientRegistration().getRegistrationId().equals("kakao")) {
            oAuth2UserInfo = new KakaoUserInfo(oAuth2User.getAttributes());
        }else{
            System.out.println("지원하지 않은 로그인 서비스 입니다.");
        }

        String provider = oAuth2UserInfo.getProvider(); //google , naver, facebook etc
        String providerId = oAuth2UserInfo.getProviderId();
        String memberId = provider + "_" + providerId;//중복이 발생하지 않도록 provider와 providerId를 조합
        String memberName = oAuth2UserInfo.getName();
        String email = oAuth2UserInfo.getEmail();
        Role role = Role.ROLE_USER;

        Optional<Member> member = memberRepository.findByMemberEmail(email);
        Member member1 = null;
        //처음 OAuth를 사용하는 경우
        if(member.isEmpty()){
            LocalDateTime createdTime = LocalDateTime.now();
            member1 = Member
                    .builder()
                    .memberId(memberId)
                    .password(bcryptPasswordEncoder.encode("1234"))
                    .memberEmail(email)
                    .memberName(memberName)
                    .provider(provider)
                    .enabled(true)
                    .accountNonLocked(true)
                    .userState(UserState.NONHUMAN)
                    .provideId(providerId)
                    .role(role)
                    .createdTime(createdTime)
                    .build();
            memberRepository.save(member1);
        }

        return new CustomUserDetails(member.get(),oAuth2User.getAttributes());
    }
}
