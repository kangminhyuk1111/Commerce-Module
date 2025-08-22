package com.example.orderservice.service;

import com.example.orderservice.dto.request.CreateOrderRequest;
import com.example.orderservice.dto.request.OrderItemRequest;
import com.example.orderservice.dto.response.OrderResponse;
import com.example.orderservice.entity.Order;
import com.example.orderservice.entity.OrderItem;
import com.example.orderservice.entity.OrderStatus;
import com.example.orderservice.exception.ApplicationException;
import com.example.orderservice.fake.FakeOrderItemRepository;
import com.example.orderservice.fake.FakeOrderRepository;
import com.example.orderservice.fake.FakeProductClient;
import com.example.orderservice.payment.PaymentProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.mock;

class OrderServiceTest {

  private OrderService orderService;
  private FakeOrderRepository fakeOrderRepository;
  private OrderItemService orderItemService;
  private FakeOrderItemRepository fakeOrderItemRepository;
  private FakeProductClient fakeProductClient;
  private PaymentProcessor paymentProcessor;

  @BeforeEach
  void setUp() {
    fakeOrderRepository = new FakeOrderRepository();
    fakeOrderItemRepository = new FakeOrderItemRepository();
    fakeProductClient = new FakeProductClient();
    paymentProcessor = mock(paymentProcessor);

    orderItemService = new OrderItemService(fakeOrderItemRepository, fakeProductClient);
    orderService = new OrderService(fakeOrderRepository, orderItemService, paymentProcessor);

    // 테스트 상품 데이터 세팅
    fakeProductClient.addProduct(1L, "연필", 500, 10);
    fakeProductClient.addProduct(2L, "지우개", 300, 20);
    fakeProductClient.addProduct(3L, "공책", 1000, 5);
  }

  @Nested
  class CreateOrderTest {

    @Test
    void 단일_상품으로_주문을_생성할_수_있다() {
      // Arrange
      CreateOrderRequest request = new CreateOrderRequest(1L, List.of(
          new OrderItemRequest(1L, 2) // 연필 2개
      ));

      // Act
      OrderResponse response = orderService.createOrder(request);

      // Assert
      assertThat(response)
          .satisfies(r -> {
            assertThat(r.id()).isEqualTo(1L);
            assertThat(r.memberId()).isEqualTo(1L);
            assertThat(r.totalPrice()).isEqualTo(1000); // 500 * 2
            assertThat(r.status()).isEqualTo(OrderStatus.PENDING);
          });

      // 저장된 Order 확인
      List<Order> savedOrders = fakeOrderRepository.findAll();
      assertThat(savedOrders).hasSize(1);

      // 저장된 OrderItem 확인
      List<OrderItem> savedItems = fakeOrderItemRepository.findAll();
      assertThat(savedItems)
          .hasSize(1)
          .extracting(OrderItem::getProductName)
          .containsExactly("연필");
    }

    @Test
    void 여러_상품으로_주문을_생성할_수_있다() {
      // Arrange
      CreateOrderRequest request = new CreateOrderRequest(1L, List.of(
          new OrderItemRequest(1L, 2), // 연필 2개 = 1000원
          new OrderItemRequest(2L, 3)  // 지우개 3개 = 900원
      ));

      // Act
      OrderResponse response = orderService.createOrder(request);

      // Assert
      assertThat(response.totalPrice()).isEqualTo(1900);

      List<OrderItem> savedItems = fakeOrderItemRepository.findAll();
      assertThat(savedItems)
          .hasSize(2)
          .extracting(OrderItem::getProductName)
          .containsExactlyInAnyOrder("연필", "지우개");
    }

    @Test
    void 재고가_부족하면_주문_생성이_실패한다() {
      // Arrange
      CreateOrderRequest request = new CreateOrderRequest(1L, List.of(
          new OrderItemRequest(3L, 10) // 공책 10개 요청 (재고 5개)
      ));

      // Act & Assert
      assertThatExceptionOfType(ApplicationException.class)
          .isThrownBy(() -> orderService.createOrder(request))
          .withMessageContaining("재고 부족")
          .withMessageContaining("공책");
    }

    @Test
    void 존재하지_않는_상품으로_주문_생성_시_실패한다() {
      // Arrange
      CreateOrderRequest request = new CreateOrderRequest(1L, List.of(
          new OrderItemRequest(999L, 1) // 존재하지 않는 상품
      ));

      // Act & Assert
      assertThatThrownBy(() -> orderService.createOrder(request))
          .isInstanceOf(RuntimeException.class)
          .hasMessageContaining("상품을 찾을 수 없습니다");
    }

    @Test
    void 복합_상품_주문을_생성할_수_있다() {
      // Arrange
      CreateOrderRequest request = new CreateOrderRequest(2L, List.of(
          new OrderItemRequest(1L, 5),  // 연필 5개 = 2500원
          new OrderItemRequest(2L, 10), // 지우개 10개 = 3000원
          new OrderItemRequest(3L, 2)   // 공책 2개 = 2000원
      ));

      // Act
      OrderResponse response = orderService.createOrder(request);

      // Assert
      assertThat(response)
          .satisfies(r -> {
            assertThat(r.memberId()).isEqualTo(2L);
            assertThat(r.totalPrice()).isEqualTo(7500);
            assertThat(r.status()).isEqualTo(OrderStatus.PENDING);
          });

      List<OrderItem> savedItems = fakeOrderItemRepository.findAll();
      assertThat(savedItems)
          .hasSize(3)
          .extracting(OrderItem::getOrderQuantity)
          .containsExactly(5, 10, 2);
    }
  }

  @Nested
  class FindOrderTest {

    @Test
    void 주문_ID로_주문을_조회할_수_있다() {
      // Arrange
      CreateOrderRequest request = new CreateOrderRequest(1L, List.of(
          new OrderItemRequest(1L, 2)
      ));
      OrderResponse createdOrder = orderService.createOrder(request);

      // Act
      OrderResponse foundOrder = orderService.findOrderById(createdOrder.id());

      // Assert
      assertThat(foundOrder)
          .satisfies(order -> {
            assertThat(order.id()).isEqualTo(createdOrder.id());
            assertThat(order.memberId()).isEqualTo(1L);
            assertThat(order.totalPrice()).isEqualTo(1000);
          });
    }

    @Test
    void 존재하지_않는_주문_ID로_조회_시_예외가_발생한다() {
      // Arrange
      Long nonExistentOrderId = 999L;

      // Act & Assert
      assertThatExceptionOfType(ApplicationException.class)
          .isThrownBy(() -> orderService.findOrderById(nonExistentOrderId))
          .withMessageContaining("주문 정보를 찾을 수 없습니다.");
    }

    @Test
    void 모든_주문을_조회할_수_있다() {
      // Arrange
      CreateOrderRequest request1 = new CreateOrderRequest(1L, List.of(
          new OrderItemRequest(1L, 1)
      ));
      CreateOrderRequest request2 = new CreateOrderRequest(2L, List.of(
          new OrderItemRequest(2L, 2)
      ));

      orderService.createOrder(request1);
      orderService.createOrder(request2);

      // Act
      List<OrderResponse> allOrders = orderService.findAllOrders();

      // Assert
      assertThat(allOrders)
          .hasSize(2)
          .extracting(OrderResponse::memberId)
          .containsExactly(1L, 2L);
    }
  }

  @Nested
  class CancelOrderTest {

    @Test
    void PENDING_상태의_주문을_취소할_수_있다() {
      // Arrange
      CreateOrderRequest request = new CreateOrderRequest(1L, List.of(
          new OrderItemRequest(1L, 2)
      ));
      OrderResponse createdOrder = orderService.createOrder(request);

      // Act
      OrderResponse cancelledOrder = orderService.cancelOrder(createdOrder.id());

      // Assert
      assertThat(cancelledOrder.status()).isEqualTo(OrderStatus.CANCELLED);
    }

    @Test
    void 존재하지_않는_주문_취소_시_예외가_발생한다() {
      // Arrange
      Long nonExistentOrderId = 999L;

      // Act & Assert
      assertThatExceptionOfType(ApplicationException.class)
          .isThrownBy(() -> orderService.cancelOrder(nonExistentOrderId))
          .withMessageContaining("주문 정보를 찾을 수 없습니다.");
    }
  }

  @Nested
  class EmptyOrderTest {

    @Test
    void 빈_주문_목록으로_주문_생성_시_0원이_된다() {
      // Arrange
      CreateOrderRequest request = new CreateOrderRequest(1L, List.of());

      // Act
      OrderResponse response = orderService.createOrder(request);

      // Assert
      assertThat(response.totalPrice()).isZero();
      assertThat(fakeOrderItemRepository.findAll()).isEmpty();
    }
  }

  @Nested
  class MultipleOrdersTest {

    @Test
    void 같은_회원이_여러_주문을_생성할_수_있다() {
      // Arrange
      CreateOrderRequest request1 = new CreateOrderRequest(1L, List.of(
          new OrderItemRequest(1L, 1)
      ));
      CreateOrderRequest request2 = new CreateOrderRequest(1L, List.of(
          new OrderItemRequest(2L, 2)
      ));

      // Act
      OrderResponse order1 = orderService.createOrder(request1);
      OrderResponse order2 = orderService.createOrder(request2);

      // Assert
      assertThat(order1.id()).isNotEqualTo(order2.id());

      List<Order> savedOrders = fakeOrderRepository.findAll();
      assertThat(savedOrders)
          .hasSize(2)
          .allSatisfy(order -> assertThat(order.getMemberId()).isEqualTo(1L));
    }
  }
}