package com.example.orderservice.payment;

import com.example.orderservice.dto.request.PaymentRequest;
import com.example.orderservice.dto.response.PaymentResult;

public interface PaymentProcessor {

  PaymentResult processPayment(PaymentRequest request);
}
