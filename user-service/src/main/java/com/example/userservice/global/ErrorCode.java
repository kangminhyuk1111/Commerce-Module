package com.example.userservice.global;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

  // 회원 정보 검증 에러
  NAME_REQUIRED(HttpStatus.BAD_REQUEST, "NAME_REQUIRED", "이름은 필수 입니다."),
  EMAIL_REQUIRED(HttpStatus.BAD_REQUEST, "EMAIL_REQUIRED", "이메일은 필수 입니다."),
  EMAIL_VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "EMAIL_VALIDATION_FAILED", "올바른 이메일 형식이 아닙니다."),
  MEMBER_NAME_TOO_SHORT(HttpStatus.BAD_REQUEST, "MEMBER_NAME_TOO_SHORT", "이름이 너무 짧습니다."),
  PASSWORD_REQUIRED(HttpStatus.BAD_REQUEST, "PASSWORD_REQUIRED", "비밀번호는 필수 입니다."),
  PASSWORD_TOO_SHORT(HttpStatus.BAD_REQUEST, "PASSWORD_TOO_SHORT", "비밀번호가 너무 짧습니다."),

  // 회원 중복/존재 에러
  EMAIL_ALREADY_EXIST(HttpStatus.CONFLICT, "EMAIL_ALREADY_EXIST", "이미 사용중인 이메일입니다."),
  MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER_NOT_FOUND", "존재하지 않는 이메일입니다."),

  // 인증 관련 에러
  LOGIN_FAIL(HttpStatus.UNAUTHORIZED, "LOGIN_FAIL", "비밀번호가 일치하지 않습니다."),
  TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "TOKEN_INVALID", "유효하지 않은 토큰입니다."),
  TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "TOKEN_EXPIRED", "만료된 토큰입니다."),
  ACCESS_DENIED(HttpStatus.FORBIDDEN, "ACCESS_DENIED", "접근 권한이 없습니다."),

  // 일반 에러
  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "서버 내부 오류가 발생했습니다."),
  INVALID_REQUEST(HttpStatus.BAD_REQUEST, "INVALID_REQUEST", "잘못된 요청입니다."),
  METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "METHOD_NOT_ALLOWED", "허용되지 않은 HTTP 메서드입니다.");

  private final HttpStatus httpStatus;
  private final String code;
  private final String message;

  ErrorCode(HttpStatus httpStatus, String code, String message) {
    this.httpStatus = httpStatus;
    this.code = code;
    this.message = message;
  }

  public HttpStatus getHttpStatus() {
    return httpStatus;
  }

  public String getCode() {
    return code;
  }

  public String getMessage() {
    return message;
  }
}