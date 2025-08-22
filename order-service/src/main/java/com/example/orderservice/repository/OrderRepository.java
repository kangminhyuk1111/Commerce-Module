package com.example.orderservice.repository;

import com.example.orderservice.entity.Order;
import java.util.List;
import java.util.Optional;

public interface OrderRepository {
  List<Order> findAll();
  Optional<Order> findById(Long id);
  Order save(Order order);
  List<Order> findOrdersByUserId(Long userId);
}
