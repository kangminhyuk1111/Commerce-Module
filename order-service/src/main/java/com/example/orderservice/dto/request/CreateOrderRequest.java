package com.example.orderservice.dto.request;

import com.example.orderservice.entity.Order;
import com.example.orderservice.entity.OrderStatus;
import java.time.LocalDateTime;
import java.util.List;

public record CreateOrderRequest(Long memberId, List<OrderItemRequest> items) {
  public Order toDomain(Integer totalPrice) {
    return new Order(memberId, totalPrice, OrderStatus.PENDING, LocalDateTime.now());
  }
}

