package com.example.userservice.dto.response;

public record LoginResponse(
    String accessToken,
    Long expiresAt
) {

  public static LoginResponse of(String accessToken, Long expiresAt) {
    return new LoginResponse(accessToken, expiresAt);
  }
}