package com.example.orderservice.service;

import com.example.orderservice.client.ProductClient;
import com.example.orderservice.dto.request.OrderItemRequest;
import com.example.orderservice.dto.response.ProductResponse;
import com.example.orderservice.entity.Order;
import com.example.orderservice.entity.OrderItem;
import com.example.orderservice.exception.ApplicationException;
import com.example.orderservice.repository.OrderItemRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class OrderItemService {

  private final OrderItemRepository orderItemsRepository;
  private final ProductClient productClient;

  public OrderItemService(final OrderItemRepository orderItemsRepository,
      final ProductClient productClient) {
    this.orderItemsRepository = orderItemsRepository;
    this.productClient = productClient;
  }

  /**
   * 주문 아이템 저장 및 스냅샷
   */
  public void saveOrderItems(final List<OrderItemRequest> items, final Order savedOrder) {
    items.stream()
        .map(item -> {
          ProductResponse product = productClient.findProductById(item.productId());

          if (product.stock() < item.quantity()) {
            throw new ApplicationException("재고 부족: " + product.name());
          }

          return new OrderItem(
              savedOrder.getId(),
              product.id(),
              product.name(),
              product.price(),
              item.quantity()
          );
        })
        .forEach(orderItemsRepository::save);
  }

  /**
   * 총 금액 계산 (상품 조회 + 재고 검증)
   */
  public int calculateTotalPrice(final List<OrderItemRequest> items) {
    return items.stream()
        .mapToInt(item -> {
          ProductResponse product = productClient.findProductById(item.productId());

          if (product.stock() < item.quantity()) {
            throw new ApplicationException("재고 부족: " + product.name());
          }

          return product.price() * item.quantity();
        })
        .sum();
  }
}
