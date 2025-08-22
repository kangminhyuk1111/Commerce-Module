package com.example.pointservice.integration;

import com.example.pointservice.domain.Point;
import com.example.pointservice.dto.request.AddPointRequest;
import com.example.pointservice.dto.request.RefundPointRequest;
import com.example.pointservice.dto.request.UsePointRequest;
import com.example.pointservice.dto.response.PointResponse;
import com.example.pointservice.repository.PointRepository;
import com.example.pointservice.service.PointService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class PointServiceIntegrationTest {

  @Autowired
  private PointService pointService;

  @Autowired
  private PointRepository pointRepository;

  @Test
  @DisplayName("계좌_개설_성공")
  void 계좌_개설_성공() {
    // Arrange
    Long userId = 1L;

    // Act
    PointResponse point = pointService.createPointAccount(userId);

    // Assert
    assertThat(point.userId()).isEqualTo(userId);
    assertThat(point.balance()).isZero();
    assertThat(pointRepository.findByUserId(point.userId())).isPresent();
  }

  @Test
  @DisplayName("포인트_추가_성공")
  void 포인트_추가_성공() {
    // Arrange
    PointResponse point = pointService.createPointAccount(1L);

    // Act
    final AddPointRequest addPointRequest = new AddPointRequest(point.userId(), 100);
    pointService.addPoint(addPointRequest);

    // Assert
    Point updated = pointRepository.findByUserId(point.userId()).get();
    assertThat(updated.getBalance()).isEqualTo(100);
  }

  @Test
  @DisplayName("포인트_사용_성공")
  void 포인트_사용_성공() {
    // Arrange
    PointResponse point = pointService.createPointAccount(1L);
    final AddPointRequest addPointRequest = new AddPointRequest(point.userId(), 200);
    pointService.addPoint(addPointRequest);

    // Act
    final UsePointRequest usePointRequest = new UsePointRequest(point.userId(), 100);
    pointService.usePoint(usePointRequest);

    // Assert
    Point updated = pointRepository.findByUserId(point.userId()).get();
    assertThat(updated.getBalance()).isEqualTo(100);
  }

  @Test
  @DisplayName("포인트_사용_잔액부족")
  void 포인트_사용_잔액부족() {
    // Arrange
    PointResponse point = pointService.createPointAccount(1L);
    final AddPointRequest addPointRequest = new AddPointRequest(point.userId(), 50);
    pointService.addPoint(addPointRequest);

    // Act & Assert
    assertThatThrownBy(() -> pointService.usePoint(new UsePointRequest(point.userId(), 100)))
        .isInstanceOf(RuntimeException.class);
  }

  @Test
  @DisplayName("포인트_환불_성공")
  void 포인트_환불_성공() {
    // Arrange
    PointResponse point = pointService.createPointAccount(1L);
    final AddPointRequest addPointRequest = new AddPointRequest(point.userId(), 100);
    pointService.addPoint(addPointRequest);

    // Act
    final RefundPointRequest refundPointRequest = new RefundPointRequest(point.userId(), 50);
    pointService.refundPoint(refundPointRequest);

    // Assert
    Point updated = pointRepository.findByUserId(point.userId()).get();
    assertThat(updated.getBalance()).isEqualTo(150);
  }
}
