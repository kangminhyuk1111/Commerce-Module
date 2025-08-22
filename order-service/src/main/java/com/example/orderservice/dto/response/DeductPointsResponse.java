package com.example.orderservice.dto.response;

public record DeductPointsResponse(Long userId, Integer remainingPoints, Integer deductedAmount, String transactionId) {

}
