package com.example.orderservice.service;

import com.example.orderservice.dto.request.OrderItemRequest;
import com.example.orderservice.entity.Order;
import com.example.orderservice.entity.OrderItem;
import com.example.orderservice.entity.OrderStatus;
import com.example.orderservice.exception.ApplicationException;
import com.example.orderservice.fake.FakeOrderItemRepository;
import com.example.orderservice.fake.FakeProductClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class OrderItemServiceTest {

  private OrderItemService orderItemService;
  private FakeOrderItemRepository fakeRepository;
  private FakeProductClient fakeProductClient;

  @BeforeEach
  void setUp() {
    fakeRepository = new FakeOrderItemRepository();
    fakeProductClient = new FakeProductClient();
    orderItemService = new OrderItemService(fakeRepository, fakeProductClient);

    // 테스트 상품 데이터 세팅
    fakeProductClient.addProduct(1L, "연필", 500, 10);
    fakeProductClient.addProduct(2L, "지우개", 300, 20);
    fakeProductClient.addProduct(3L, "공책", 1000, 5);
  }

  @Nested
  class SaveOrderItemsTest {

    @Test
    void 단일_상품으로_OrderItem을_저장할_수_있다() {
      // Arrange
      Order savedOrder = createOrder(1L);
      List<OrderItemRequest> items = List.of(orderItem(1L, 2));

      // Act
      orderItemService.saveOrderItems(items, savedOrder);

      // Assert
      List<OrderItem> savedItems = fakeRepository.findAll();

      assertThat(savedItems)
          .hasSize(1)
          .first()
          .satisfies(item -> {
            assertThat(item.getProductId()).isEqualTo(1L);
            assertThat(item.getProductName()).isEqualTo("연필");
            assertThat(item.getProductPrice()).isEqualTo(500);
            assertThat(item.getOrderQuantity()).isEqualTo(2);
          });
    }

    @Test
    void 여러_상품으로_OrderItem을_저장할_수_있다() {
      // Arrange
      Order savedOrder = createOrder(1L);
      List<OrderItemRequest> items = List.of(
          orderItem(1L, 2),
          orderItem(2L, 3)
      );

      // Act
      orderItemService.saveOrderItems(items, savedOrder);

      // Assert
      List<OrderItem> savedItems = fakeRepository.findAll();

      assertThat(savedItems)
          .hasSize(2)
          .extracting(OrderItem::getProductName)
          .containsExactlyInAnyOrder("연필", "지우개");

      assertThat(savedItems)
          .extracting(OrderItem::getOrderQuantity)
          .containsExactly(2, 3);
    }

    @Test
    void 존재하지_않는_상품으로_저장_시_예외가_발생한다() {
      // Arrange
      Order savedOrder = createOrder(1L);
      List<OrderItemRequest> items = List.of(orderItem(999L, 1));

      // Act & Assert
      assertThatThrownBy(() -> orderItemService.saveOrderItems(items, savedOrder))
          .isInstanceOf(RuntimeException.class)
          .hasMessageContaining("상품을 찾을 수 없습니다")
          .hasMessageEndingWith("999");
    }
  }

  @Nested
  class CalculateTotalPriceTest {

    @Test
    void 단일_상품의_총_가격을_계산할_수_있다() {
      // Arrange
      List<OrderItemRequest> items = List.of(orderItem(1L, 3));

      // Act
      int totalPrice = orderItemService.calculateTotalPrice(items);

      // Assert
      assertThat(totalPrice)
          .isEqualTo(1500)
          .isGreaterThan(1000)
          .isLessThan(2000);
    }

    @Test
    void 여러_상품의_총_가격을_계산할_수_있다() {
      // Arrange
      List<OrderItemRequest> items = List.of(
          orderItem(1L, 2),  // 1000원
          orderItem(2L, 4),  // 1200원
          orderItem(3L, 1)   // 1000원
      );

      // Act
      int totalPrice = orderItemService.calculateTotalPrice(items);

      // Assert
      assertThat(totalPrice)
          .isEqualTo(3200)
          .isBetween(3000, 4000);
    }

    @Test
    void 재고가_부족하면_예외가_발생한다() {
      // Arrange
      List<OrderItemRequest> items = List.of(orderItem(3L, 10)); // 재고 5개 < 주문 10개

      // Act & Assert
      assertThatExceptionOfType(ApplicationException.class)
          .isThrownBy(() -> orderItemService.calculateTotalPrice(items))
          .withMessage("재고 부족: 공책");
    }

    @Test
    void 일부_상품만_재고_부족인_경우_예외가_발생한다() {
      // Arrange
      List<OrderItemRequest> items = List.of(
          orderItem(1L, 2),   // 재고 충분
          orderItem(3L, 10)   // 재고 부족
      );

      // Act & Assert
      assertThatExceptionOfType(ApplicationException.class)
          .isThrownBy(() -> orderItemService.calculateTotalPrice(items))
          .withMessageContaining("재고 부족")
          .withMessageContaining("공책");
    }

    @Test
    void 재고와_주문_수량이_같으면_정상_처리된다() {
      // Arrange
      List<OrderItemRequest> items = List.of(orderItem(3L, 5)); // 재고 5개 = 주문 5개

      // Act
      int totalPrice = orderItemService.calculateTotalPrice(items);

      // Assert
      assertThat(totalPrice)
          .isNotZero()
          .isPositive()
          .isEqualTo(5000);
    }

    @Test
    void 수량이_0인_경우_가격이_0으로_계산된다() {
      // Arrange
      List<OrderItemRequest> items = List.of(orderItem(1L, 0));

      // Act
      int totalPrice = orderItemService.calculateTotalPrice(items);

      // Assert
      assertThat(totalPrice)
          .isZero()
          .isNotPositive()
          .isNotNegative();
    }

    @Test
    void 존재하지_않는_상품으로_계산_시_예외가_발생한다() {
      // Arrange
      List<OrderItemRequest> items = List.of(orderItem(999L, 1));

      // Act & Assert
      assertThatCode(() -> orderItemService.calculateTotalPrice(items))
          .isInstanceOf(RuntimeException.class)
          .hasMessageStartingWith("상품을 찾을 수 없습니다");
    }

    @Test
    void 복합_상황에서_정상_계산된다() {
      // Arrange
      List<OrderItemRequest> items = List.of(
          orderItem(1L, 5),   // 2500원
          orderItem(2L, 10),  // 3000원
          orderItem(3L, 3)    // 3000원
      );

      // Act
      int totalPrice = orderItemService.calculateTotalPrice(items);

      // Assert
      assertThat(totalPrice)
          .isEqualTo(8500)
          .isGreaterThanOrEqualTo(8000)
          .isLessThanOrEqualTo(9000)
          .satisfies(price -> {
            assertThat(price % 100).isZero(); // 100원 단위
            assertThat(price).isGreaterThan(5000);
          });
    }

    @Test
    void 빈_주문_목록은_0원으로_계산된다() {
      // Arrange
      List<OrderItemRequest> items = List.of();

      // Act
      int totalPrice = orderItemService.calculateTotalPrice(items);

      // Assert
      assertThat(totalPrice).isZero();
    }

    @Test
    void 모든_상품의_가격이_양수인지_확인한다() {
      // Arrange
      List<OrderItemRequest> items = List.of(
          orderItem(1L, 1),
          orderItem(2L, 1),
          orderItem(3L, 1)
      );

      // Act
      int totalPrice = orderItemService.calculateTotalPrice(items);

      // Assert
      assertThat(totalPrice)
          .isPositive()
          .isGreaterThan(0)
          .satisfies(price -> assertThat(price).isEqualTo(1800)); // 500 + 300 + 1000
    }
  }

  private Order createOrder(Long memberId) {
    return new Order(memberId, 1000, OrderStatus.PENDING, LocalDateTime.now());
  }

  private OrderItemRequest orderItem(Long productId, Integer quantity) {
    return new OrderItemRequest(productId, quantity);
  }
}