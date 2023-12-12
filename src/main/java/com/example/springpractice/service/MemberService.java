package com.example.springpractice.service;

import com.example.springpractice.config.constant.Role;
import com.example.springpractice.config.constant.UserState;
import com.example.springpractice.domain.Member;
import com.example.springpractice.domain.dto.MemberRequest;
import com.example.springpractice.domain.dto.MemberResponse;
import com.example.springpractice.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    public static final int MAX_FAILED_ATTEMPTS = 3;

    private static final long LOCK_TIME_DURATION = 24 * 60 * 60 * 1000; // 24 hours

    private final MemberRepository repo;

    //실패 횟수증가
    @Transactional
    public void increaseFailedAttempts(Member member) {
        int newFailAttempts = member.getFailCount() + 1;
        repo.updateFailCount(member.getMemberEmail(),newFailAttempts);
    }

    //실패 횟수 초기화
    @Transactional
    public void resetFailedAttempts(String email) {
        repo.updateFailCount(email,0);
    }

    //계정 잠금
    @Transactional
    public void lock(Member member) {
        member.setUserState(UserState.USER_LOCK);
        member.setAccountNonLocked(false);
        member.setLockTime(new Date());
        member.setEnabled(false);
        repo.save(member);
    }

    //계정이 잠겼을 경우 기간 설정
    @Transactional
    public boolean unlockWhenTimeExpired(Member member) {
        long lockTimeInMillis = member.getLockTime().getTime();
        long currentTimeInMillis = System.currentTimeMillis();

        if (lockTimeInMillis + LOCK_TIME_DURATION < currentTimeInMillis) {
            member.setAccountNonLocked(true);
            member.setEnabled(true);
            member.setUserState(UserState.NONHUMAN);
            member.setLockTime(null);
            member.setFailCount(0);
            repo.save(member);
            return true;
        }
        return false;
    }

    //회원 목록
    @Transactional(readOnly = true)
    public List<MemberResponse>memberResponseList(){
        List<Member>memberList = memberRepository.findAll();
        return memberList.stream().map(MemberResponse::new).collect(Collectors.toList());
    }

    //회원 조회
    @Transactional(readOnly = true)
    public MemberResponse memberResponse(Long id){
        Optional<Member>member = repo.findById(id);

        if(member.isEmpty()){
            throw new RuntimeException("회원이 없습니다.");
        }

        return new MemberResponse(member.get());
    }

    @Transactional(readOnly = true)
    public Member getEmail(String memberEmail){
        Optional<Member>member=repo.findByMemberEmail(memberEmail);
        return member.get();
    }

    //회원 가입기능
    @Transactional
    public Member memberCreate(MemberRequest memberRequest){
        Member member = Member
                .builder()
                .memberId(memberRequest.getMemberId())
                .memberName(memberRequest.getMemberName())
                .password(passwordEncoder.encode(memberRequest.getPassword()))
                .memberEmail(memberRequest.getMemberEmail())
                .memberPhone(memberRequest.getMemberPhone())
                .failCount(0)
                .role(Role.ROLE_USER)
                .userState(UserState.NONHUMAN)
                .enabled(true)
                .accountNonLocked(true)
                .lockTime(null)
                .build();

        return memberRepository.save(member);
    }

    //회원 수정
    @Transactional
    public Member updateMember(Long id,MemberRequest memberRequest){
        Optional<Member>member = repo.findById(id);
        Member memberUpdate = member.get();
        memberUpdate.memberUpdate(memberRequest);
        return repo.save(memberUpdate);
    }

    //회원 삭제
    @Transactional
    public void memberDelete(Long id){
        Optional<Member>member = repo.findById(id);
        if(!member.isPresent()){
            throw new RuntimeException("회원이 없습니다.");
        }
        repo.delete(member.get());
    }

    //회원 아이디 중복처리
    @Transactional
    public boolean MemberIdDuplicated(String memberId){
        return repo.existsByMemberId(memberId);
    }

    //회원 아이디 찾기
    @Transactional
    public String memberIdSearch(String memberName){
        Optional<Member>member = repo.findByMemberName(memberName);

        if(member.isEmpty()){
            throw new RuntimeException("회원의 아이디가 없습니다.");
        }

        return member.get().getMemberId();
    }

    //회원 비밀번호 재설정
    @Transactional
    public void passwordReset(MemberRequest memberRequest){
        Member member = Member.builder()
                .password(passwordEncoder.encode(memberRequest.getPassword()))
                .build();
        repo.save(member);
    }

    //휴먼회원 해제
    @Transactional
    public void activeNonHuman(Member member){
        member.setUserState(UserState.NONHUMAN);
        repo.save(member);
    }
}
