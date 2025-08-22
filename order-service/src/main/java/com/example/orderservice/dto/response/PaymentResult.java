package com.example.orderservice.dto.response;

import com.example.orderservice.payment.PaymentStatus;

public record PaymentResult(PaymentStatus status, String transactionId, String failureReason) {
  public static PaymentResult success(String transactionId) {
    return new PaymentResult(PaymentStatus.SUCCESS, transactionId, null);
  }

  public static PaymentResult failure(String reason) {
    return new PaymentResult(PaymentStatus.SUCCESS, null, reason);
  }
}
