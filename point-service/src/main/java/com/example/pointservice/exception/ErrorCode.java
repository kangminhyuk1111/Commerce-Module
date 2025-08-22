package com.example.pointservice.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

  // 계좌 관련 에러
  ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "ACCOUNT_NOT_FOUND", "계좌가 존재하지 않습니다."),
  ACCOUNT_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "ACCOUNT_ALREADY_EXISTS", "이미 포인트 계좌가 존재합니다."),
  ACCOUNT_CREATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "ACCOUNT_CREATION_FAILED", "포인트 계좌 생성에 실패했습니다."),

  // 포인트 관련 에러
  INSUFFICIENT_BALANCE(HttpStatus.BAD_REQUEST, "INSUFFICIENT_BALANCE", "포인트가 부족합니다."),
  INVALID_AMOUNT(HttpStatus.BAD_REQUEST, "INVALID_AMOUNT", "금액이 유효하지 않습니다."),
  NEGATIVE_AMOUNT(HttpStatus.BAD_REQUEST, "NEGATIVE_AMOUNT", "추가할 포인트는 0보다 커야 합니다."),
  ZERO_OR_NEGATIVE_USE_AMOUNT(HttpStatus.BAD_REQUEST, "ZERO_OR_NEGATIVE_USE_AMOUNT", "사용할 포인트는 0보다 커야 합니다."),
  ZERO_OR_NEGATIVE_REFUND_AMOUNT(HttpStatus.BAD_REQUEST, "ZERO_OR_NEGATIVE_REFUND_AMOUNT", "환불할 포인트는 0보다 커야 합니다."),

  // 일반 에러
  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "서버 내부 오류가 발생했습니다."),
  INVALID_REQUEST(HttpStatus.BAD_REQUEST, "INVALID_REQUEST", "잘못된 요청입니다."),
  USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "사용자를 찾을 수 없습니다."),
  USER_ID_NOT_NULL(HttpStatus.BAD_REQUEST, "USER_ID_NOT_NULL", "멤버 ID는 NULL일 수 없습니다.");

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