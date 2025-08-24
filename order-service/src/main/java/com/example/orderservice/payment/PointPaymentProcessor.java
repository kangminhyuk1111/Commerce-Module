package com.example.orderservice.payment;

import com.example.orderservice.client.PointClient;
import com.example.orderservice.dto.request.DeductPointsRequest;
import com.example.orderservice.dto.request.PaymentRequest;
import com.example.orderservice.dto.response.DeductPointsResponse;
import com.example.orderservice.dto.response.PaymentResponse;
import com.example.orderservice.exception.ApplicationException;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PointPaymentProcessor implements PaymentProcessor{

  private static final Logger log = LoggerFactory.getLogger(PointPaymentProcessor.class);
  private static final String TRANSACTION_ID_START = "POINT_";
  public static final String USER_DELIMITER = "_USER_";

  private final PointClient pointClient;

  public PointPaymentProcessor(final PointClient pointClient) {
    this.pointClient = pointClient;
  }

  @Override
  public PaymentResponse processPayment(final PaymentRequest request) {
    log.info("포인트 결제 시작 - orderId: {}, userId: {}, amount: {}",
        request.orderId(), request.userId(), request.amount());

    try {
      DeductPointsRequest deductRequest = new DeductPointsRequest(request.userId(), request.amount());

      DeductPointsResponse response = pointClient.deductPoints(deductRequest);

      String transactionId = generateTransactionId(request);

      log.info("포인트 결제 성공 - transactionId: {}, 남은포인트: {}",
          transactionId, response.remainingPoints());

      return PaymentResponse.success(transactionId);

    } catch (ApplicationException e) {
      log.warn("포인트 부족 - userId: {}, 요청금액: {}, 에러: {}",
          request.userId(), request.amount(), e.getMessage());
      return PaymentResponse.failure("포인트가 부족합니다: " + e.getMessage());

    } catch (Exception e) {
      log.error("포인트 결제 시스템 오류 - userId: {}, amount: {}",
          request.userId(), request.amount(), e);
      return PaymentResponse.failure("포인트 결제 중 시스템 오류가 발생했습니다");
    }
  }

  private String generateTransactionId(PaymentRequest request) {
    return TRANSACTION_ID_START + UUID.randomUUID() + USER_DELIMITER + request.userId();
  }
}
