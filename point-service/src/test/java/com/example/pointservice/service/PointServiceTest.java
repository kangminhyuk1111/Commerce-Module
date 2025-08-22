package com.example.pointservice.service;

import static org.assertj.core.api.Assertions.*;

import com.example.pointservice.dto.request.AddPointRequest;
import com.example.pointservice.dto.request.UsePointRequest;
import com.example.pointservice.dto.response.PointResponse;
import com.example.pointservice.exception.ErrorCode;
import com.example.pointservice.exception.PointException;
import com.example.pointservice.repository.FakePointRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class PointServiceTest {

  private PointService pointService;
  private FakePointRepository fakePointRepository;

  @BeforeEach
  void setUp() {
    fakePointRepository = new FakePointRepository();
    pointService = new PointService(fakePointRepository);
  }

  @Nested
  @DisplayName("포인트 계좌를 생성")
  class CreatePointAccount {

    @Test
    @DisplayName("포인트 계좌를 생성한다")
    void 포인트_계좌를_생성한다() {
      // Arrange
      Long userId = 1L;

      // Act
      PointResponse response = pointService.createPointAccount(userId);

      // Assert
      assertThat(response.userId()).isEqualTo(userId);
      assertThat(response.balance()).isEqualTo(0L);
    }

    @Test
    @DisplayName("이미 존재하는 계좌 생성 시 예외가 발생한다")
    void 이미_존재하는_계좌_생성시_예외가_발생한다() {
      // Arrange
      Long userId = 1L;
      pointService.createPointAccount(userId);

      // Act & Assert
      assertThatThrownBy(() -> pointService.createPointAccount(userId))
          .isInstanceOf(PointException.class)
          .hasMessage(ErrorCode.ACCOUNT_ALREADY_EXISTS.getMessage());
    }
  }

  @Nested
  @DisplayName("포인트 계좌를 조회")
  class FindPointAccount {

    @Test
    @DisplayName("존재하는 계좌를 조회한다")
    void 존재하는_계좌를_조회한다() {
      // Arrange
      Long userId = 1L;
      pointService.createPointAccount(userId);

      // Act
      PointResponse response = pointService.findPointByUserId(userId);

      // Assert
      assertThat(response.userId()).isEqualTo(userId);
      assertThat(response.balance()).isEqualTo(0L);
    }

    @Test
    @DisplayName("존재하지 않는 계좌 조회 시 예외가 발생한다")
    void 존재하지_않는_계좌_조회시_예외가_발생한다() {
      // Arrange
      Long userId = 999L;

      // Act & Assert
      assertThatThrownBy(() -> pointService.findPointByUserId(userId))
          .isInstanceOf(PointException.class)
          .hasMessage(ErrorCode.ACCOUNT_ALREADY_EXISTS.getMessage());
    }
  }

  @Nested
  @DisplayName("포인트를 추가")
  class AddPoint {

    @Test
    @DisplayName("계좌가 없으면 새로 생성하고 포인트를 추가한다")
    void 계좌가_없으면_새로_생성하고_포인트를_추가한다() {
      // Arrange
      Long userId = 1L;
      Integer amount = 5000;
      AddPointRequest request = new AddPointRequest(userId, amount);

      // Act
      PointResponse response = pointService.addPoint(request);

      // Assert
      assertThat(response.userId()).isEqualTo(userId);
      assertThat(response.balance()).isEqualTo(5000L);

      // 계좌가 실제로 생성되었는지 확인
      PointResponse foundAccount = pointService.findPointByUserId(userId);
      assertThat(foundAccount.balance()).isEqualTo(5000L);
    }

    @Test
    @DisplayName("기존 계좌에 포인트를 추가한다")
    void 기존_계좌에_포인트를_추가한다() {
      // Arrange
      Long userId = 1L;
      pointService.createPointAccount(userId);
      pointService.addPoint(new AddPointRequest(userId, 3000)); // 기존 잔액 3000

      Integer additionalAmount = 2000;
      AddPointRequest request = new AddPointRequest(userId, additionalAmount);

      // Act
      PointResponse response = pointService.addPoint(request);

      // Assert
      assertThat(response.balance()).isEqualTo(5000); // 3000 + 2000
    }
  }

  @Nested
  @DisplayName("포인트를 차감")
  class UsePoint {

    @Test
    @DisplayName("계좌가 없으면 새로 생성하고 잔액 부족으로 실패한다")
    void 계좌가_없으면_새로_생성하고_잔액_부족으로_실패한다() {
      // Arrange
      Long userId = 1L;
      Integer amount = 1000;
      UsePointRequest request = new UsePointRequest(userId, amount);

      // Act & Assert
      assertThatThrownBy(() -> pointService.usePoint(request))
          .isInstanceOf(PointException.class)
          .hasMessage(ErrorCode.INSUFFICIENT_BALANCE.getMessage());

      // 계좌는 생성되어야 함
      PointResponse createdAccount = pointService.findPointByUserId(userId);
      assertThat(createdAccount.balance()).isEqualTo(0);
    }

    @Test
    @DisplayName("충분한 잔액이 있으면 포인트를 차감한다")
    void 충분한_잔액이_있으면_포인트를_차감한다() {
      // Arrange
      Long userId = 1L;
      pointService.addPoint(new AddPointRequest(userId, 5000)); // 잔액 5000

      Integer deductAmount = 2000;
      UsePointRequest request = new UsePointRequest(userId, deductAmount);

      // Act
      PointResponse response = pointService.usePoint(request);

      // Assert
      assertThat(response.balance()).isEqualTo(3000); // 5000 - 2000
    }

    @Test
    @DisplayName("잔액이 부족하면 예외가 발생한다")
    void 잔액이_부족하면_예외가_발생한다() {
      // Arrange
      Long userId = 1L;
      pointService.addPoint(new AddPointRequest(userId, 3000)); // 잔액 3000

      Integer deductAmount = 5000;
      UsePointRequest request = new UsePointRequest(userId, deductAmount);

      // Act & Assert
      assertThatThrownBy(() -> pointService.usePoint(request))
          .isInstanceOf(PointException.class)
          .hasMessage(ErrorCode.INSUFFICIENT_BALANCE.getMessage());

      // 잔액은 변경되지 않아야 함
      PointResponse account = pointService.findPointByUserId(userId);
      assertThat(account.balance()).isEqualTo(3000);
    }
  }

  @Nested
  @DisplayName("포인트 사용 가능 여부를 확인")
  class CanUsePoint {

    @Test
    @DisplayName("계좌가 없으면 false를 반환한다")
    void 계좌가_없으면_false를_반환한다() {
      // Arrange
      Long userId = 1L;
      Integer amount = 1000;

      // Act
      boolean canUse = pointService.canUsePoint(userId, amount);

      // Assert
      assertThat(canUse).isFalse();
    }

    @Test
    @DisplayName("잔액이 충분하면 true를 반환한다")
    void 잔액이_충분하면_true를_반환한다() {
      // Arrange
      Long userId = 1L;
      pointService.addPoint(new AddPointRequest(userId, 5000));
      Integer amount = 3000;

      // Act
      boolean canUse = pointService.canUsePoint(userId, amount);

      // Assert
      assertThat(canUse).isTrue();
    }

    @Test
    @DisplayName("잔액이 부족하면 false를 반환한다")
    void 잔액이_부족하면_false를_반환한다() {
      // Arrange
      Long userId = 1L;
      pointService.addPoint(new AddPointRequest(userId, 3000));
      Integer amount = 5000;

      // Act
      boolean canUse = pointService.canUsePoint(userId, amount);

      // Assert
      assertThat(canUse).isFalse();
    }

    @Test
    @DisplayName("잔액과 사용금액이 같으면 true를 반환한다")
    void 잔액과_사용금액이_같으면_true를_반환한다() {
      // Arrange
      Long userId = 1L;
      Integer amount = 5000;
      pointService.addPoint(new AddPointRequest(userId, amount));

      // Act
      boolean canUse = pointService.canUsePoint(userId, amount);

      // Assert
      assertThat(canUse).isTrue();
    }
  }

  @Nested
  @DisplayName("포인트 시나리오 테스트")
  class PointScenario {

    @Test
    @DisplayName("포인트 추가 후 차감하는 전체 플로우")
    void 포인트_추가_후_차감하는_전체_플로우() {
      // Arrange
      Long userId = 1L;

      // Act & Assert
      // 1. 포인트 추가 (계좌 자동 생성)
      PointResponse addResponse = pointService.addPoint(new AddPointRequest(userId, 10000));
      assertThat(addResponse.balance()).isEqualTo(10000);

      // 2. 포인트 차감
      PointResponse useResponse = pointService.usePoint(new UsePointRequest(userId, 3000));
      assertThat(useResponse.balance()).isEqualTo(7000);

      // 3. 계좌 조회로 최종 확인
      PointResponse finalResponse = pointService.findPointByUserId(userId);
      assertThat(finalResponse.balance()).isEqualTo(7000);
    }

    @Test
    @DisplayName("포인트 차감 시도 후 추가하는 플로우")
    void 포인트_차감_시도_후_추가하는_플로우() {
      // Arrange
      Long userId = 1L;

      // Act & Assert
      // 1. 포인트 차감 시도 (계좌 생성 + 잔액 부족 실패)
      assertThatThrownBy(() -> pointService.usePoint(new UsePointRequest(userId, 1000)))
          .isInstanceOf(PointException.class);

      // 2. 계좌는 생성되었음을 확인
      PointResponse createdAccount = pointService.findPointByUserId(userId);
      assertThat(createdAccount.balance()).isEqualTo(0);

      // 3. 포인트 추가
      PointResponse addResponse = pointService.addPoint(new AddPointRequest(userId, 5000));
      assertThat(addResponse.balance()).isEqualTo(5000);

      // 4. 이제 차감 가능
      PointResponse useResponse = pointService.usePoint(new UsePointRequest(userId, 2000));
      assertThat(useResponse.balance()).isEqualTo(3000);
    }

    @Test
    @DisplayName("여러 번 포인트 추가 및 차감")
    void 여러번_포인트_추가_및_차감() {
      // Arrange
      Long userId = 1L;

      // Act & Assert
      // 1차 추가
      pointService.addPoint(new AddPointRequest(userId, 3000));
      assertThat(pointService.findPointByUserId(userId).balance()).isEqualTo(3000);

      // 1차 차감
      pointService.usePoint(new UsePointRequest(userId, 1000));
      assertThat(pointService.findPointByUserId(userId).balance()).isEqualTo(2000);

      // 2차 추가
      pointService.addPoint(new AddPointRequest(userId, 5000));
      assertThat(pointService.findPointByUserId(userId).balance()).isEqualTo(7000);

      // 2차 차감
      pointService.usePoint(new UsePointRequest(userId, 3000));
      assertThat(pointService.findPointByUserId(userId).balance()).isEqualTo(4000);

      // 사용 가능 여부 확인
      assertThat(pointService.canUsePoint(userId, 4000)).isTrue();
      assertThat(pointService.canUsePoint(userId, 4001)).isFalse();
    }
  }
}