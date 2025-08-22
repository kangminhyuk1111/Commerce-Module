package com.example.orderservice.dto.request;

import com.example.orderservice.payment.PaymentMethod;

public record PaymentRequest(Long orderId, Long userId, Integer amount, PaymentMethod paymentMethod) {

}
