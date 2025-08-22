package com.example.pointservice.integration;

import com.example.pointservice.domain.Point;
import com.example.pointservice.dto.request.AddPointRequest;
import com.example.pointservice.dto.request.UsePointRequest;
import com.example.pointservice.repository.PointRepository;
import com.example.pointservice.service.PointService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class PointServiceConcurrencyTest {

  @Autowired
  private PointService pointService;

  @Autowired
  private PointRepository pointRepository;

  @Test
  @DisplayName("포인트_동시_사용_테스트")
  void 포인트_동시_사용_테스트() throws InterruptedException {
    // Arrange
    Long userId = 1L;
    pointService.createPointAccount(userId);
    pointService.addPoint(new AddPointRequest(userId, 1000)); // 초기 1000포인트

    int threadCount = 10;
    int useAmount = 100;
    ExecutorService executor = Executors.newFixedThreadPool(threadCount);
    CountDownLatch latch = new CountDownLatch(threadCount);

    // Act
    for (int i = 0; i < threadCount; i++) {
      executor.submit(() -> {
        try {
          pointService.usePoint(new UsePointRequest(userId, useAmount));
        } catch (Exception e) {
        } finally {
          latch.countDown();
        }
      });
    }

    latch.await();

    // Assert
    Point point = pointRepository.findByUserId(userId).get();
    assertThat(point.getBalance()).isEqualTo(0);
  }
}
