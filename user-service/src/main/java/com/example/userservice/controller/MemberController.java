package com.example.userservice.controller;

import com.example.userservice.dto.request.LoginRequest;
import com.example.userservice.dto.response.LoginResponse;
import com.example.userservice.dto.request.CreateMemberRequest;
import com.example.userservice.dto.response.MemberResponse;
import com.example.userservice.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members")
public class MemberController {

  private final MemberService memberService;

  public MemberController(final MemberService memberService) {
    this.memberService = memberService;
  }

  @PostMapping("/register")
  @ResponseStatus(HttpStatus.CREATED)
  public void register(@RequestBody CreateMemberRequest request) {
    memberService.createMember(request);
  }

  @PostMapping("/login")
  public LoginResponse login(@RequestBody final LoginRequest request) {
    return memberService.login(request);
  }

  @GetMapping("/profile/{userId}")
  public MemberResponse profile(@PathVariable Long userId) {
    return memberService.findById(userId);
  }
}
