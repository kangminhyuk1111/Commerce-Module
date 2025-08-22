package com.example.orderservice.repository;

import com.example.orderservice.entity.Order;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaOrderRepository extends OrderRepository, JpaRepository<Order, Long> {
  @Query("SELECT o FROM Order o WHERE o.memberId = :userId ORDER BY o.orderDate DESC")
  List<Order> findOrdersByUserId(@Param("userId") Long userId);
}
