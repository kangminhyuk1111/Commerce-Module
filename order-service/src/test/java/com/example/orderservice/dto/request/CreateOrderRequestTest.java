package com.example.orderservice.dto.request;

import com.example.orderservice.entity.Order;
import com.example.orderservice.entity.OrderStatus;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class CreateOrderRequestTest {

  @Test
  void 필수_필드로_CreateOrderRequest를_생성할_수_있다() {
    // Arrange
    Long memberId = 1L;
    List<OrderItemRequest> items = List.of(
        new OrderItemRequest(1L, 3),
        new OrderItemRequest(2L, 2)
    );

    // Act
    CreateOrderRequest request = new CreateOrderRequest(memberId, items);

    // Assert
    assertThat(request.memberId()).isEqualTo(memberId);
    assertThat(request.items()).hasSize(2);
    assertThat(request.items().get(0).productId()).isEqualTo(1L);
    assertThat(request.items().get(0).quantity()).isEqualTo(3);
    assertThat(request.items().get(1).productId()).isEqualTo(2L);
    assertThat(request.items().get(1).quantity()).isEqualTo(2);
  }

  @Test
  void 빈_아이템_리스트로_CreateOrderRequest를_생성할_수_있다() {
    // Arrange
    Long memberId = 1L;
    List<OrderItemRequest> items = List.of();

    // Act
    CreateOrderRequest request = new CreateOrderRequest(memberId, items);

    // Assert
    assertThat(request.memberId()).isEqualTo(memberId);
    assertThat(request.items()).isEmpty();
  }

  @Test
  void 단일_아이템으로_CreateOrderRequest를_생성할_수_있다() {
    // Arrange
    Long memberId = 1L;
    List<OrderItemRequest> items = List.of(
        new OrderItemRequest(1L, 5)
    );

    // Act
    CreateOrderRequest request = new CreateOrderRequest(memberId, items);

    // Assert
    assertThat(request.memberId()).isEqualTo(memberId);
    assertThat(request.items()).hasSize(1);
    assertThat(request.items().get(0).productId()).isEqualTo(1L);
    assertThat(request.items().get(0).quantity()).isEqualTo(5);
  }

  @Test
  void OrderItemRequest를_정상적으로_생성할_수_있다() {
    // Arrange
    Long productId = 1L;
    Integer quantity = 5;

    // Act
    OrderItemRequest orderItemRequest = new OrderItemRequest(productId, quantity);

    // Assert
    assertThat(orderItemRequest.productId()).isEqualTo(productId);
    assertThat(orderItemRequest.quantity()).isEqualTo(quantity);
  }

  @Test
  void 수량이_0인_OrderItemRequest를_생성할_수_있다() {
    // Arrange
    Long productId = 1L;
    Integer quantity = 0;

    // Act
    OrderItemRequest orderItemRequest = new OrderItemRequest(productId, quantity);

    // Assert
    assertThat(orderItemRequest.productId()).isEqualTo(productId);
    assertThat(orderItemRequest.quantity()).isEqualTo(0);
  }

  @Test
  void 많은_수량의_OrderItemRequest를_생성할_수_있다() {
    // Arrange
    Long productId = 1L;
    Integer quantity = 1000;

    // Act
    OrderItemRequest orderItemRequest = new OrderItemRequest(productId, quantity);

    // Assert
    assertThat(orderItemRequest.productId()).isEqualTo(productId);
    assertThat(orderItemRequest.quantity()).isEqualTo(1000);
  }

  @Test
  void 정상적인_값으로_Order_도메인_객체를_생성할_수_있다() {
    // Arrange
    Long memberId = 1L;
    List<OrderItemRequest> items = List.of(
        new OrderItemRequest(1L, 3),
        new OrderItemRequest(2L, 2)
    );
    CreateOrderRequest request = new CreateOrderRequest(memberId, items);
    Integer totalPrice = 30000;

    // Act
    Order order = request.toDomain(totalPrice);

    // Assert
    assertThat(order.getMemberId()).isEqualTo(memberId);
    assertThat(order.getTotalPrice()).isEqualTo(totalPrice);
    assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING);
    assertThat(order.getOrderDate()).isNotNull();
    assertThat(order.getOrderDate()).isBefore(LocalDateTime.now().plusSeconds(1));
  }

  @Test
  void 총_가격이_0일_때_Order_도메인_객체를_생성할_수_있다() {
    // Arrange
    Long memberId = 1L;
    List<OrderItemRequest> items = List.of(
        new OrderItemRequest(1L, 0)
    );
    CreateOrderRequest request = new CreateOrderRequest(memberId, items);
    Integer totalPrice = 0;

    // Act
    Order order = request.toDomain(totalPrice);

    // Assert
    assertThat(order.getMemberId()).isEqualTo(memberId);
    assertThat(order.getTotalPrice()).isEqualTo(0);
    assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING);
    assertThat(order.getOrderDate()).isNotNull();
  }

  @Test
  void 높은_금액으로_Order_도메인_객체를_생성할_수_있다() {
    // Arrange
    Long memberId = 1L;
    List<OrderItemRequest> items = List.of(
        new OrderItemRequest(1L, 100),
        new OrderItemRequest(2L, 50)
    );
    CreateOrderRequest request = new CreateOrderRequest(memberId, items);
    Integer totalPrice = 1000000;

    // Act
    Order order = request.toDomain(totalPrice);

    // Assert
    assertThat(order.getMemberId()).isEqualTo(memberId);
    assertThat(order.getTotalPrice()).isEqualTo(1000000);
    assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING);
    assertThat(order.getOrderDate()).isNotNull();
  }

  @Test
  void 여러_회원의_Order_도메인_객체를_생성할_수_있다() {
    // Arrange
    Long memberId1 = 1L;
    Long memberId2 = 2L;
    List<OrderItemRequest> items = List.of(
        new OrderItemRequest(1L, 2)
    );
    CreateOrderRequest request1 = new CreateOrderRequest(memberId1, items);
    CreateOrderRequest request2 = new CreateOrderRequest(memberId2, items);
    Integer totalPrice = 20000;

    // Act
    Order order1 = request1.toDomain(totalPrice);
    Order order2 = request2.toDomain(totalPrice);

    // Assert
    assertThat(order1.getMemberId()).isEqualTo(memberId1);
    assertThat(order2.getMemberId()).isEqualTo(memberId2);
    assertThat(order1.getTotalPrice()).isEqualTo(totalPrice);
    assertThat(order2.getTotalPrice()).isEqualTo(totalPrice);
    assertThat(order1.getStatus()).isEqualTo(OrderStatus.PENDING);
    assertThat(order2.getStatus()).isEqualTo(OrderStatus.PENDING);
  }
}