package com.example.orderservice.fake;

import com.example.orderservice.entity.Order;
import com.example.orderservice.entity.OrderStatus;

import com.example.orderservice.repository.OrderRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class FakeOrderRepository implements OrderRepository {

  private final Map<Long, Order> storage = new HashMap<>();
  private Long nextId = 1L;

  @Override
  public List<Order> findAll() {
    return new ArrayList<>(storage.values());
  }

  @Override
  public Optional<Order> findById(Long id) {
    return Optional.ofNullable(storage.get(id));
  }

  @Override
  public Order save(Order order) {
    if (order.getId() == null) {
      Order newOrder = createOrderWithId(nextId++, order);
      storage.put(newOrder.getId(), newOrder);
      return newOrder;
    } else {
      storage.put(order.getId(), order);
      return order;
    }
  }

  @Override
  public List<Order> findOrdersByUserId(final Long userId) {
    return storage.values().stream()
        .filter(order -> order.getMemberId().equals(userId))
        .collect(Collectors.toList());
  }

  private Order createOrderWithId(Long id, Order order) {
    try {
      Order newOrder = new Order(
          order.getMemberId(),
          order.getTotalPrice(),
          order.getStatus(),
          order.getOrderDate()
      );

      // 리플렉션을 사용해서 ID 설정
      setIdUsingReflection(newOrder, id);
      return newOrder;

    } catch (Exception e) {
      throw new RuntimeException("Order ID 설정 실패", e);
    }
  }

  private void setIdUsingReflection(Order order, Long id) {
    try {
      java.lang.reflect.Field idField = Order.class.getDeclaredField("id");
      idField.setAccessible(true);
      idField.set(order, id);
    } catch (Exception e) {
      throw new RuntimeException("리플렉션으로 ID 설정 실패", e);
    }
  }

  // 테스트 헬퍼 메서드들
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

  // 테스트용 데이터 추가 메서드
  public Order saveWithId(Long id, Long memberId, Integer totalPrice, OrderStatus status) {
    Order order = new Order(memberId, totalPrice, status, LocalDateTime.now());
    setIdUsingReflection(order, id);
    storage.put(id, order);
    return order;
  }
}