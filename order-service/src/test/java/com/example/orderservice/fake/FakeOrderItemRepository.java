package com.example.orderservice.fake;

import com.example.orderservice.entity.OrderItem;

import com.example.orderservice.repository.OrderItemRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FakeOrderItemRepository implements OrderItemRepository {

  private final Map<Long, OrderItem> storage = new HashMap<>();
  private Long nextId = 1L;

  @Override
  public OrderItem save(OrderItem orderItem) {
    if (orderItem.getId() == null) {
      // 새로운 엔티티 - ID 생성
      OrderItem newOrderItem = createOrderItemWithId(nextId++, orderItem);
      storage.put(newOrderItem.getId(), newOrderItem);
      return newOrderItem;
    } else {
      // 기존 엔티티 - 업데이트
      storage.put(orderItem.getId(), orderItem);
      return orderItem;
    }
  }

  // ID를 가진 새로운 OrderItem 생성
  private OrderItem createOrderItemWithId(Long id, OrderItem orderItem) {
    try {
      OrderItem newOrderItem = new OrderItem(
          orderItem.getOrderId(),
          orderItem.getProductId(),
          orderItem.getProductName(),
          orderItem.getProductPrice(),
          orderItem.getOrderQuantity()
      );

      // 리플렉션으로 ID 설정
      setIdUsingReflection(newOrderItem, id);
      return newOrderItem;

    } catch (Exception e) {
      throw new RuntimeException("OrderItem ID 설정 실패", e);
    }
  }

  private void setIdUsingReflection(OrderItem orderItem, Long id) {
    try {
      java.lang.reflect.Field idField = OrderItem.class.getDeclaredField("id");
      idField.setAccessible(true);
      idField.set(orderItem, id);
    } catch (Exception e) {
      throw new RuntimeException("리플렉션으로 ID 설정 실패", e);
    }
  }

  // 테스트 헬퍼 메서드들
  public List<OrderItem> findAll() {
    return new ArrayList<>(storage.values());
  }

  public List<OrderItem> findByOrderId(Long orderId) {
    return storage.values().stream()
        .filter(item -> item.getOrderId().equals(orderId))
        .toList();
  }

  public void clear() {
    storage.clear();
    nextId = 1L;
  }

  public int size() {
    return storage.size();
  }

  public boolean exists(Long id) {
    return storage.containsKey(id);
  }
}