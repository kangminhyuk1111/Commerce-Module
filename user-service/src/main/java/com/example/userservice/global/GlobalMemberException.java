package com.example.userservice.global;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalMemberException {

  @ExceptionHandler(MemberException.class)
  public ResponseEntity<ErrorResponse> handleMemberException(MemberException e) {
    final ErrorResponse errorResponse = ErrorResponse.of(e.getErrorCode().getHttpStatus().value(), e.getMessage());
    return ResponseEntity.status(e.getErrorCode().getHttpStatus().value()).body(errorResponse);
  }
}
