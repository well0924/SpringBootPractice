package com.example.springpractice.controller;

import com.example.springpractice.domain.Member;
import com.example.springpractice.domain.dto.MemberRequest;
import com.example.springpractice.domain.dto.MemberResponse;
import com.example.springpractice.service.MemberService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class LoginApiController {

    private final MemberService memberService;

    @GetMapping("/list")
    public ResponseEntity<List<MemberResponse>>memberList(){
        List<MemberResponse>list = memberService.memberResponseList();
        return new ResponseEntity<>(list,HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MemberResponse>memberDetail(@PathVariable Long id){
        MemberResponse memberResponse = memberService.memberResponse(id);
        return new ResponseEntity<>(memberResponse,HttpStatus.OK);
    }
    
    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Member>memberCreate(@RequestBody MemberRequest memberRequest){
        Member member = memberService.memberCreate(memberRequest);
        return new ResponseEntity<>(member,HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Member>memberUpdate(@PathVariable Long id,@RequestBody MemberRequest memberRequest){
        Member member = memberService.updateMember(id,memberRequest);
        return new ResponseEntity<>(member,HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Objects>memberDelete(@PathVariable Long id){
        memberService.memberDelete(id);
        return new ResponseEntity("삭제 완료",HttpStatus.OK);
    }
}
