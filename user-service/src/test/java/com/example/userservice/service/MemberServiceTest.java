package com.example.userservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.userservice.auth.JJwtProvider;
import com.example.userservice.auth.JwtProvider;
import com.example.userservice.dto.request.CreateMemberRequest;
import com.example.userservice.dto.response.MemberResponse;
import com.example.userservice.global.ErrorCode;
import com.example.userservice.global.MemberException;
import com.example.userservice.repository.FakeMemberRepository;
import com.example.userservice.repository.MemberRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class MemberServiceTest {

  private MemberService memberService;
  private JwtProvider jwtProvider;

  @BeforeEach
  void setUp() {
    MemberRepository memberRepository = new FakeMemberRepository();
    jwtProvider = new JJwtProvider(
        "testSecretKeyForTestingPurposeOnly32Chars",
        3600000L
    );
    memberService = new MemberService(memberRepository, jwtProvider);
  }

  @Nested
  class 멤버_생성 {

    @Test
    void 성공() {
      // Arrange
      final CreateMemberRequest request = new CreateMemberRequest("name", "email@email.com", "password@");

      // Act
      MemberResponse response = memberService.createMember(request);

      // Assert
      assertThat(response)
          .extracting(
              MemberResponse::name,
              MemberResponse::email
          )
          .containsExactly(
              "name",
              "email@email.com"
          );
    }

    @Test
    void 실패_이메일_중복() {
      // Arrange
      final CreateMemberRequest request = new CreateMemberRequest("name", "email@email.com", "password");
      memberService.createMember(request);

      // Act & Assert
      assertThatThrownBy(() -> memberService.createMember(request))
          .isInstanceOf(MemberException.class)
          .hasMessage(ErrorCode.EMAIL_ALREADY_EXIST.getMessage());
    }
  }

  @Nested
  class 멤버_조회 {

    @Test
    void 멤버_생성_후_조회시_성공() {
      // Arrange
      String name = "kim";
      String email = "kim@gmail.com";
      String password = "password@";
      final CreateMemberRequest request = new CreateMemberRequest(name, email, password);
      final MemberResponse member = memberService.createMember(request);

      // Act
      MemberResponse findById = memberService.findById(member.id());

      // Assert
      assertThat(findById)
          .extracting(
              MemberResponse::name,
              MemberResponse::email
          ).containsExactly(
              name,
              email
          );
    }

    @Test
    void 전체_멤버_조회() {
      // Arrange
      CreateMemberRequest request1 = new CreateMemberRequest("kim1", "kim1@gmail.com", "password@");
      CreateMemberRequest request2 = new CreateMemberRequest("kim2", "kim2@gmail.com", "password@");
      memberService.createMember(request1);
      memberService.createMember(request2);

      // Act
      List<MemberResponse> members = memberService.findAll();

      // Assert
      assertThat(members.size()).isEqualTo(2);
    }
  }
}
