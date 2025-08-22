package com.example.userservice.auth;

import com.example.userservice.dto.response.LoginResponse;

public interface JwtProvider {

  LoginResponse generateToken(Long userId);
  Long getUserIdFromToken(String token);
  boolean validateToken(String token);
  boolean isTokenExpired(String token);
}
