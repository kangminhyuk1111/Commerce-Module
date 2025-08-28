package com.example.orderservice.service;

import com.example.orderservice.dto.request.CreateOrderRequest;
import com.example.orderservice.dto.request.OrderItemRequest;
import com.example.orderservice.dto.request.PaymentRequest;
import com.example.orderservice.dto.response.OrderResponse;
import com.example.orderservice.dto.response.PaymentResponse;
import com.example.orderservice.entity.Order;
import com.example.orderservice.exception.ApplicationException;
import com.example.orderservice.payment.PaymentMethod;
import com.example.orderservice.payment.PaymentProcessor;
import com.example.orderservice.payment.PaymentStatus;
import com.example.orderservice.repository.OrderRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

  private final OrderRepository orderRepository;
  private final OrderItemService orderItemService;
  private final PaymentProcessor processor;

  public OrderService(final OrderRepository orderRepository,
      final OrderItemService orderItemService,
      final PaymentProcessor paymentProcessor) {
    this.orderRepository = orderRepository;
    this.orderItemService = orderItemService;
    this.processor = paymentProcessor;
  }

  public List<OrderResponse> findAllOrders() {
    return orderRepository.findAll().stream().map(OrderResponse::of).toList();
  }

  public OrderResponse findOrderById(final Long id) {
    final Order order = findByOrderId(id);

    return OrderResponse.of(order);
  }

  @Transactional
  public OrderResponse createOrder(final CreateOrderRequest request) {
    final List<OrderItemRequest> items = request.items();

    if (items == null || items.isEmpty()) {
      throw new ApplicationException("주문 상품이 비어있습니다.");
    }

    final Integer totalPrice = orderItemService.calculateTotalPrice(items);

    final Order order = request.toDomain(totalPrice);

    final Order savedOrder = orderRepository.save(order);

    orderItemService.saveOrderItems(items, savedOrder);

    return OrderResponse.of(savedOrder);
  }

  @Transactional
  public OrderResponse cancelOrder(final Long orderId) {
    final Order order = findByOrderId(orderId);

    order.cancel();

    return OrderResponse.of(order);
  }

  @Transactional
  public OrderResponse processOrderPayment(final Long orderId) {
    final Order order = findByOrderId(orderId);

    final PaymentRequest paymentRequest = new PaymentRequest(
        order.getId(), order.getMemberId(), order.getTotalPrice(), PaymentMethod.POINT
    );

    final PaymentResponse result = processor.processPayment(paymentRequest);

    if (result.status() != PaymentStatus.SUCCESS) {
      throw new ApplicationException("결제 실패: " + result.failureReason());
    }

    order.markAsPaid();

    return OrderResponse.of(order);
  }

  private Order findByOrderId(final Long orderId) {
    return orderRepository.findById(orderId)
        .orElseThrow(() -> new ApplicationException("주문 정보를 찾을 수 없습니다."));
  }

  public List<OrderResponse> findMyOrders(final Long userId) {
    List<Order> orders = orderRepository.findOrdersByUserId(userId);

    return orders.stream()
        .map(OrderResponse::of)
        .toList();
  }
}
