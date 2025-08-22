package com.example.userservice.service;

import com.example.userservice.auth.JwtProvider;
import com.example.userservice.dto.request.LoginRequest;
import com.example.userservice.domain.Member;
import com.example.userservice.dto.request.CreateMemberRequest;
import com.example.userservice.dto.response.LoginResponse;
import com.example.userservice.dto.response.MemberResponse;
import com.example.userservice.global.ErrorCode;
import com.example.userservice.global.MemberException;
import com.example.userservice.repository.MemberRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberService {

  private final MemberRepository memberRepository;
  private final JwtProvider jwtProvider;

  public MemberService(final MemberRepository memberRepository, final JwtProvider jwtProvider) {
    this.memberRepository = memberRepository;
    this.jwtProvider = jwtProvider;
  }

  @Transactional
  public LoginResponse login(final LoginRequest request) {
    final Member member = memberRepository.findByEmail(request.email())
        .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

    if (!member.getPassword().equals(request.password())) {
      throw new MemberException(ErrorCode.LOGIN_FAIL);
    }

    return jwtProvider.generateToken(member.getId());
  }

  @Transactional
  public MemberResponse createMember(final CreateMemberRequest request) {
    Optional<Member> existingMember = memberRepository.findByEmailWithLock(request.email());

    if (existingMember.isPresent()) {
      throw new MemberException(ErrorCode.EMAIL_ALREADY_EXIST);
    }

    final Member saved = memberRepository.save(request.toDomain());

    return MemberResponse.of(saved);
  }

  public MemberResponse findById(final Long id) {
    final Member member = memberRepository.findById(id)
        .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

    return MemberResponse.of(member);
  }

  public List<MemberResponse> findAll() {
    return memberRepository.findAll().stream().map(MemberResponse::of).toList();
  }
}