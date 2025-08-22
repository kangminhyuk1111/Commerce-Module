package com.example.userservice.auth;

import com.example.userservice.dto.response.LoginResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class JJwtProviderTest {

  @Autowired
  private JwtProvider jwtProvider;

  @Test
  void JWT_토큰_생성() {
    // given
    Long userId = 1L;

    // when
    LoginResponse response = jwtProvider.generateToken(userId);

    // then
    assertThat(response.accessToken()).isNotNull();
    assertThat(response.expiresAt()).isGreaterThan(System.currentTimeMillis());
  }

  @Test
  void JWT_토큰에서_사용자ID_추출() {
    // given
    Long userId = 123L;
    LoginResponse response = jwtProvider.generateToken(userId);

    // when
    Long extractedUserId = jwtProvider.getUserIdFromToken(response.accessToken());

    // then
    assertThat(extractedUserId).isEqualTo(userId);
  }

  @Test
  void JWT_토큰_유효성_검증() {
    // given
    Long userId = 1L;
    LoginResponse response = jwtProvider.generateToken(userId);

    // when & then
    assertThat(jwtProvider.validateToken(response.accessToken())).isTrue();
    assertThat(jwtProvider.validateToken("invalid.token.here")).isFalse();
  }

  @Test
  void JWT_토큰_만료_확인() {
    // given
    Long userId = 1L;
    LoginResponse response = jwtProvider.generateToken(userId);

    // when & then
    assertThat(jwtProvider.isTokenExpired(response.accessToken())).isFalse();
  }
}