package com.example.orderservice.controller;

import com.example.orderservice.dto.request.CreateOrderRequest;
import com.example.orderservice.dto.response.OrderResponse;
import com.example.orderservice.service.OrderService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
✅ 필수 구현
- POST /api/orders - 주문 생성 (기본 기능만)
- GET /api/orders - 내 주문 목록 조회
- GET /api/orders/{id} - 주문 상세 조회
*/
@RestController
@RequestMapping("/api/orders")
public class OrderController {

  private final OrderService orderService;

  public OrderController(final OrderService orderService) {
    this.orderService = orderService;
  }
  
  // 내 주문 목록 조회
  @GetMapping("/{userId}/my-orders")
  public List<OrderResponse> findMyOrders(@PathVariable Long userId) {
    return orderService.findMyOrders(userId);
  }

  // 주문 ID로 주문 상세 조회
  @GetMapping("/{id}")
  public OrderResponse findOrderById(@PathVariable final Long id) {
    return orderService.findOrderById(id);
  }

  // 주문 생성
  @PostMapping
  public OrderResponse createOrder(@RequestBody final CreateOrderRequest request) {
    return orderService.createOrder(request);
  }

  // 주문 취소
  @PutMapping("/{id}/cancel")
  public OrderResponse cancelOrder(@PathVariable final Long id) {
    return orderService.cancelOrder(id);
  }

  @PutMapping("/{id}/payment")
  public OrderResponse paymentOrder(@PathVariable final Long id) {
    return orderService.processOrderPayment(id);
  }
}
