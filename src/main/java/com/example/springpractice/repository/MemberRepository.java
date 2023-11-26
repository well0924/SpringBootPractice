package com.example.springpractice.repository;

import com.example.springpractice.config.constant.UserState;
import com.example.springpractice.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member,Long> {

    //로그인 실패 횟수증가
    @Query(value = "update Member m set m.failCount =?2 where m.memberEmail = ?1")
    @Modifying
    void updateFailCount(String memberEmail,int failCount);
    //로그인(일반 로그인->이메일)
    Optional<Member>findByMemberEmail(String memberEmail);
    //아이디 중복처리
    boolean existsByMemberId(String memberName);
    //아이디 찾기
    Optional<Member>findByMemberName(String memberName);
    //휴먼회원 전환
    List<Member> findByUpdatedTimeBeforeAndUserStateEquals(LocalDateTime localDateTime, UserState userState);

}
