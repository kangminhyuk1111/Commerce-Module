package com.example.pointservice.domain;

import com.example.pointservice.exception.ErrorCode;
import com.example.pointservice.exception.PointException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class PointTest {

  @Test
  @DisplayName("계좌_개설_성공")
  void 계좌_개설_성공() {
    // Arrange
    Long userId = 1L;

    // Act
    Point point = Point.openAccount(userId);

    // Assert
    assertThat(point).isNotNull();
    assertThat(point.getUserId()).isEqualTo(userId);
    assertThat(point.getBalance()).isZero();
  }

  @Test
  @DisplayName("계좌_개설_userId_null")
  void 계좌_개설_userId_null() {
    // Arrange
    Long userId = null;

    // Act & Assert
    assertThatThrownBy(() -> Point.openAccount(userId))
        .isInstanceOf(PointException.class)
        .hasMessage(ErrorCode.USER_ID_NOT_NULL.getMessage());
  }

  @Test
  @DisplayName("포인트_추가_성공")
  void 포인트_추가_성공() {
    // Arrange
    Point point = Point.openAccount(1L);

    // Act
    point.addPoint(100);

    // Assert
    assertThat(point.getBalance()).isEqualTo(100);
  }

  @Test
  @DisplayName("포인트_여러번_추가")
  void 포인트_여러번_추가() {
    // Arrange
    Point point = Point.openAccount(1L);

    // Act
    point.addPoint(100);
    point.addPoint(200);

    // Assert
    assertThat(point.getBalance()).isEqualTo(300);
  }

  @Test
  @DisplayName("포인트_사용_성공")
  void 포인트_사용_성공() {
    // Arrange
    Point point = Point.openAccount(1L);
    point.addPoint(200);

    // Act
    point.usePoint(100);

    // Assert
    assertThat(point.getBalance()).isEqualTo(100);
  }

  @Test
  @DisplayName("포인트_사용_잔액부족")
  void 포인트_사용_잔액부족() {
    // Arrange
    Point point = Point.openAccount(1L);
    point.addPoint(50);

    // Act & Assert
    assertThatThrownBy(() -> point.usePoint(100))
        .isInstanceOf(PointException.class)
        .hasMessage(ErrorCode.INSUFFICIENT_BALANCE.getMessage());
  }

  @Test
  @DisplayName("포인트_환불_성공")
  void 포인트_환불_성공() {
    // Arrange
    Point point = Point.openAccount(1L);
    point.addPoint(100);

    // Act
    point.refundPoint(50);

    // Assert
    assertThat(point.getBalance()).isEqualTo(150);
  }

  @Test
  @DisplayName("포인트_여러번_환불")
  void 포인트_여러번_환불() {
    // Arrange
    Point point = Point.openAccount(1L);
    point.addPoint(100);

    // Act
    point.refundPoint(50);
    point.refundPoint(30);

    // Assert
    assertThat(point.getBalance()).isEqualTo(180);
  }
}
