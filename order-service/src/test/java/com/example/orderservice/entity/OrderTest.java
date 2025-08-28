package com.example.orderservice.entity;

import com.example.orderservice.exception.ApplicationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

class OrderTest {

  @Nested
  class ObjectCreationTest {

    @Test
    void 기본_생성자로_Order_객체를_생성할_수_있다() {
      // Arrange

      // Act
      Order order = new Order();

      // Assert
      assertThat(order).isNotNull();
      assertThat(order.getId()).isNull();
      assertThat(order.getMemberId()).isNull();
      assertThat(order.getTotalPrice()).isNull();
      assertThat(order.getStatus()).isNull();
      assertThat(order.getOrderDate()).isNull();
    }

    @Test
    void 필수_필드로_Order_객체를_생성할_수_있다() {
      // Arrange
      Long memberId = 1L;
      Integer totalPrice = 20000;
      OrderStatus status = OrderStatus.PENDING;
      LocalDateTime orderDate = LocalDateTime.now();

      // Act
      Order order = new Order(memberId, totalPrice, status, orderDate);

      // Assert
      assertThat(order.getMemberId()).isEqualTo(memberId);
      assertThat(order.getTotalPrice()).isEqualTo(totalPrice);
      assertThat(order.getStatus()).isEqualTo(status);
      assertThat(order.getOrderDate()).isEqualTo(orderDate);
    }
  }

  @Nested
  class OrderStatusChangeTest {

    private Order pendingOrder;
    private Order paidOrder;
    private Order completedOrder;
    private Order cancelledOrder;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
      now = LocalDateTime.now();
      pendingOrder = new Order(1L, 20000, OrderStatus.PENDING, now);
      paidOrder = new Order(1L, 20000, OrderStatus.PAID, now);
      completedOrder = new Order(1L, 20000, OrderStatus.COMPLETED, now);
      cancelledOrder = new Order(1L, 20000, OrderStatus.CANCELLED, now);
    }

    @Nested
    class PaidTest {

      @Test
      void PENDING_상태의_주문은_CANCELLED로_변경된다() {
        // Arrange
        // pendingOrder는 BeforeEach에서 설정됨

        // Act
        pendingOrder.cancel();

        // Assert
        assertThat(pendingOrder.getStatus()).isEqualTo(OrderStatus.CANCELLED);
      }

      @Test
      void PAID_상태의_주문은_paid_처리할_수_없다() {
        // Arrange
        // paidOrder는 BeforeEach에서 설정됨

        // Act & Assert
        assertThatThrownBy(() -> paidOrder.markAsPaid())
            .isInstanceOf(ApplicationException.class)
            .hasMessage("대기 중인 주문만 취소 처리할 수 있습니다.");
      }

      @Test
      void COMPLETED_상태의_주문은_paid_처리할_수_없다() {
        // Arrange
        // completedOrder는 BeforeEach에서 설정됨

        // Act & Assert
        assertThatThrownBy(() -> completedOrder.markAsPaid())
            .isInstanceOf(ApplicationException.class)
            .hasMessage("대기 중인 주문만 취소 처리할 수 있습니다.");
      }

      @Test
      void CANCELLED_상태의_주문은_paid_처리할_수_없다() {
        // Arrange
        // cancelledOrder는 BeforeEach에서 설정됨

        // Act & Assert
        assertThatThrownBy(() -> cancelledOrder.markAsPaid())
            .isInstanceOf(ApplicationException.class)
            .hasMessage("대기 중인 주문만 취소 처리할 수 있습니다.");
      }
    }

    @Nested
    class CompleteTest {

      @Test
      void PAID_상태의_주문은_완료_처리할_수_있다() {
        // Arrange
        // paidOrder는 BeforeEach에서 설정됨

        // Act
        paidOrder.complete();

        // Assert
        assertThat(paidOrder.getStatus()).isEqualTo(OrderStatus.COMPLETED);
      }

      @Test
      void PENDING_상태의_주문은_완료_처리할_수_없다() {
        // Arrange
        // pendingOrder는 BeforeEach에서 설정됨

        // Act & Assert
        assertThatThrownBy(() -> pendingOrder.complete())
            .isInstanceOf(ApplicationException.class)
            .hasMessage("결제 완료 주문만 완료 처리할 수 있습니다.");
      }

      @Test
      void COMPLETED_상태의_주문은_완료_처리할_수_없다() {
        // Arrange
        // completedOrder는 BeforeEach에서 설정됨

        // Act & Assert
        assertThatThrownBy(() -> completedOrder.complete())
            .isInstanceOf(ApplicationException.class)
            .hasMessage("결제 완료 주문만 완료 처리할 수 있습니다.");
      }

      @Test
      void CANCELLED_상태의_주문은_완료_처리할_수_없다() {
        // Arrange
        // cancelledOrder는 BeforeEach에서 설정됨

        // Act & Assert
        assertThatThrownBy(() -> cancelledOrder.complete())
            .isInstanceOf(ApplicationException.class)
            .hasMessage("결제 완료 주문만 완료 처리할 수 있습니다.");
      }
    }

    @Nested
    class CancelTest {

      @Test
      void PENDING_상태의_주문은_취소_처리할_수_있다() {
        // Arrange
        // pendingOrder는 BeforeEach에서 설정됨

        // Act
        pendingOrder.cancel();

        // Assert
        assertThat(pendingOrder.getStatus()).isEqualTo(OrderStatus.CANCELLED);
      }

      @Test
      void PAID_상태의_주문은_취소_처리할_수_없다() {
        // Arrange
        // paidOrder는 BeforeEach에서 설정됨

        // Act & Assert
        assertThatThrownBy(() -> paidOrder.cancel())
            .isInstanceOf(ApplicationException.class)
            .hasMessage("대기 중인 주문만 취소 처리할 수 있습니다.");
      }

      @Test
      void COMPLETED_상태의_주문은_취소_처리할_수_없다() {
        // Arrange
        // completedOrder는 BeforeEach에서 설정됨

        // Act & Assert
        assertThatThrownBy(() -> completedOrder.cancel())
            .isInstanceOf(ApplicationException.class)
            .hasMessage("대기 중인 주문만 취소 처리할 수 있습니다.");
      }

      @Test
      void CANCELLED_상태의_주문은_취소_처리할_수_없다() {
        // Arrange
        // cancelledOrder는 BeforeEach에서 설정됨

        // Act & Assert
        assertThatThrownBy(() -> cancelledOrder.cancel())
            .isInstanceOf(ApplicationException.class)
            .hasMessage("대기 중인 주문만 취소 처리할 수 있습니다.");
      }
    }
  }

  @Nested
  class GetterMethodTest {

    @Test
    void 모든_필드의_값을_정확히_반환한다() {
      // Arrange
      Long memberId = 1L;
      Integer totalPrice = 50000;
      OrderStatus status = OrderStatus.PENDING;
      LocalDateTime orderDate = LocalDateTime.now();
      Order order = new Order(memberId, totalPrice, status, orderDate);

      // Act & Assert
      assertThat(order.getMemberId()).isEqualTo(memberId);
      assertThat(order.getTotalPrice()).isEqualTo(totalPrice);
      assertThat(order.getStatus()).isEqualTo(status);
      assertThat(order.getOrderDate()).isEqualTo(orderDate);
    }
  }

  @Nested
  class StatusChangeScenarioTest {

    @Test
    void 주문_생성_취소_처리_시나리오() {
      // Arrange
      Long memberId = 1L;
      Integer totalPrice = 30000;
      OrderStatus status = OrderStatus.PENDING;
      LocalDateTime orderDate = LocalDateTime.now();
      Order order = new Order(memberId, totalPrice, status, orderDate);

      // Act
      order.cancel();

      // Assert
      assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
    }

    @Test
    void 주문_결제_완료_처리_시나리오() {
      // Arrange
      Long memberId = 1L;
      Integer totalPrice = 30000;
      OrderStatus status = OrderStatus.PAID;
      LocalDateTime orderDate = LocalDateTime.now();
      Order order = new Order(memberId, totalPrice, status, orderDate);

      // Act
      order.complete();

      // Assert
      assertThat(order.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @Test
    void 완료된_주문은_더_이상_상태_변경이_불가능하다() {
      // Arrange
      Long memberId = 1L;
      Integer totalPrice = 30000;
      OrderStatus status = OrderStatus.PAID;
      LocalDateTime orderDate = LocalDateTime.now();
      Order order = new Order(memberId, totalPrice, status, orderDate);
      order.complete();

      // Act & Assert
      assertThatThrownBy(() -> order.cancel())
          .isInstanceOf(ApplicationException.class);

      assertThatThrownBy(() -> order.complete())
          .isInstanceOf(ApplicationException.class);
    }

    @Test
    void 취소된_주문은_더_이상_상태_변경이_불가능하다() {
      // Arrange
      Long memberId = 1L;
      Integer totalPrice = 30000;
      OrderStatus status = OrderStatus.PENDING;
      LocalDateTime orderDate = LocalDateTime.now();
      Order order = new Order(memberId, totalPrice, status, orderDate);
      order.cancel();

      // Act & Assert
      assertThatThrownBy(() -> order.complete())
          .isInstanceOf(ApplicationException.class);

      assertThatThrownBy(() -> order.cancel())
          .isInstanceOf(ApplicationException.class);
    }
  }
}