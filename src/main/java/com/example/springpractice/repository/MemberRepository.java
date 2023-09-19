package com.example.springpractice.repository;

import com.example.springpractice.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.OptionalInt;

public interface MemberRepository extends JpaRepository<Member,Long> {

    //로그인 실패 횟수증가
    @Query(value = "update Member m set m.failCount =?2 where m.memberEmail = ?1")
    @Modifying
    void updateFailCount(String memberEmail,int failCount);
    //로그인
    Optional<Member>findByMemberEmail(String memberEmail);
    //아이디 중복처리
    boolean existsByMemberId(String memberName);
    //아이디 찾기
    Optional<Member>findByMemberName(String memberName);
}
