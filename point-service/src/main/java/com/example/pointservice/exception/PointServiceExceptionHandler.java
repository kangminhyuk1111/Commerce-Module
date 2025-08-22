package com.example.pointservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class PointServiceExceptionHandler {

  @ExceptionHandler(PointException.class)
  public ResponseEntity<ErrorResponse> handlePointException(PointException e) {
    final ErrorResponse response = ErrorResponse.of(400, e.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<ErrorResponse> handleRuntimeException(PointException e) {
    ErrorResponse errorResponse = ErrorResponse.of(500, "서버 내부 오류가 발생했습니다.");
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
  }
}
