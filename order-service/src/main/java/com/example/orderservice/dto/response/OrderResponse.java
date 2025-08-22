package com.example.orderservice.dto.response;

import com.example.orderservice.entity.Order;
import com.example.orderservice.entity.OrderStatus;
import java.time.LocalDateTime;

public record OrderResponse(
    Long id,
    Long memberId,
    Integer totalPrice,
    OrderStatus status,
    LocalDateTime orderDate
) {
  public static OrderResponse of(Order order) {
    return new OrderResponse(
        order.getId(),
        order.getMemberId(),
        order.getTotalPrice(),
        order.getStatus(),
        order.getOrderDate()
    );
  }
}
