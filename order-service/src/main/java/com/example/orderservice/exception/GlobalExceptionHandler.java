package com.example.orderservice.exception;

import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(FeignException.NotFound.class)
  public ResponseEntity<ErrorResponse> handleProductNotFound(FeignException.NotFound e) {
    ErrorResponse errorResponse = ErrorResponse.of(404, "상품을 찾을 수 없습니다.");
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }

  @ExceptionHandler(FeignException.Conflict.class)
  public ResponseEntity<ErrorResponse> handleStockShortage(FeignException.Conflict e) {
    ErrorResponse errorResponse = ErrorResponse.of(409, "재고가 부족합니다.");
    return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
  }

  @ExceptionHandler(ProductNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleProductNotFoundException(ProductNotFoundException e) {
    ErrorResponse errorResponse = ErrorResponse.of(404, e.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }

  @ExceptionHandler(ApplicationException.class)
  public ResponseEntity<ErrorResponse> handleOrderException(ApplicationException e) {
    ErrorResponse errorResponse = ErrorResponse.of(400, e.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException e) {
    ErrorResponse errorResponse = ErrorResponse.of(500, "서버 내부 오류가 발생했습니다.");
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
  }
}
