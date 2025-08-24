package com.example.orderservice.dto.response;

import com.example.orderservice.payment.PaymentStatus;

public record PaymentResponse(PaymentStatus status, String transactionId, String failureReason) {
  public static PaymentResponse success(String transactionId) {
    return new PaymentResponse(PaymentStatus.SUCCESS, transactionId, null);
  }

  public static PaymentResponse failure(String reason) {
    return new PaymentResponse(PaymentStatus.SUCCESS, null, reason);
  }
}
