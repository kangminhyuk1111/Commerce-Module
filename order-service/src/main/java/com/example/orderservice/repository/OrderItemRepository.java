package com.example.orderservice.repository;

import com.example.orderservice.entity.OrderItem;

public interface OrderItemRepository {

  OrderItem save(OrderItem orderItems);
}
