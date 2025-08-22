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
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderItemService {

  private final OrderItemRepository orderItemsRepository;
  private final ProductClient productClient;

  public OrderItemService(final OrderItemRepository orderItemsRepository, final ProductClient productClient) {
    this.orderItemsRepository = orderItemsRepository;
    this.productClient = productClient;
  }

  @Transactional
  public void saveOrderItems(final List<OrderItemRequest> items, final Order savedOrder) {
    for (OrderItemRequest item : items) {
      final ProductResponse product = productClient.findProductById(item.productId());

      final OrderItem orderItem = new OrderItem(
          savedOrder.getId(),
          product.id(),
          product.name(),
          product.price(),
          item.quantity()
      );

      orderItemsRepository.save(orderItem);
    }
  }

  public int calculateTotalPrice(final List<OrderItemRequest> items) {
    int totalPrice = 0;

    for (OrderItemRequest item : items) {
      final ProductResponse product = productClient.findProductById(item.productId());

      if (product.stock() < item.quantity()) {
        throw new ApplicationException("재고 부족: " + product.name());
      }

      totalPrice += product.price() * item.quantity();
    }

    return totalPrice;
  }
}
