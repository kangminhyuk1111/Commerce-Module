package com.example.orderservice.payment;

import com.example.orderservice.dto.request.PaymentRequest;
import com.example.orderservice.dto.response.PaymentResponse;

public interface PaymentProcessor {

  PaymentResponse processPayment(PaymentRequest request);
}
