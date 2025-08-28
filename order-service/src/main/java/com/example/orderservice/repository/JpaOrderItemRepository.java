package com.example.orderservice.repository;

import com.example.orderservice.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaOrderItemRepository extends OrderItemRepository ,JpaRepository<OrderItem, Long> {

}
