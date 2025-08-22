package com.example.pointservice.service;

import com.example.pointservice.domain.Point;
import com.example.pointservice.dto.request.AddPointRequest;
import com.example.pointservice.dto.request.RefundPointRequest;
import com.example.pointservice.dto.response.PointResponse;
import com.example.pointservice.dto.request.UsePointRequest;
import com.example.pointservice.exception.ErrorCode;
import com.example.pointservice.exception.PointException;
import com.example.pointservice.repository.PointRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PointService {

  private static final Logger log = LoggerFactory.getLogger(PointService.class);
  private static final int INITIAL_POINTS = 0;

  private final PointRepository pointRepository;

  public PointService(final PointRepository pointRepository) {
    this.pointRepository = pointRepository;
  }

  public PointResponse findPointByUserId(final Long userId) {
    final Point point = pointRepository.findByUserId(userId)
        .orElseThrow(() -> new PointException(ErrorCode.ACCOUNT_ALREADY_EXISTS));

    return PointResponse.of(point);
  }

  @Transactional
  public PointResponse addPoint(final AddPointRequest request) {
    final Point point = findOrCreatePointAccount(request.userId());

    point.addPoint(request.amount());

    log.info("포인트 추가 완료 - userId: {}, 추가금액: {}, 잔여포인트: {}",
        request.userId(), request.amount(), point.getBalance());

    return PointResponse.of(point);
  }

  @Transactional
  public PointResponse usePoint(final UsePointRequest request) {
    final Point point = findOrCreatePointAccount(request.userId());

    point.usePoint(request.amount());

    log.info("포인트 차감 완료 - userId: {}, 차감금액: {}, 잔여포인트: {}",
        request.userId(), request.amount(), point.getBalance());

    return PointResponse.of(point);
  }

  @Transactional
  public PointResponse refundPoint(final RefundPointRequest request) {
    final Point point = pointRepository.findByUserIdWithLock(request.userId())
        .orElseThrow(() -> new PointException(ErrorCode.ACCOUNT_NOT_FOUND));

    point.refundPoint(request.amount());

    log.info("포인트 환불 완료 - userId: {}, 환불금액: {}, 잔여포인트: {}",
        request.userId(), request.amount(), point.getBalance());

    return PointResponse.of(point);
  }

  @Transactional
  public PointResponse createPointAccount(final Long userId) {
    if (pointRepository.findByUserId(userId).isPresent()) {
      throw new PointException(ErrorCode.ACCOUNT_ALREADY_EXISTS);
    }

    final Point newAccount = createNewPointAccount(userId);
    return PointResponse.of(newAccount);
  }

  public boolean canUsePoint(final Long userId, final Integer amount) {
    return pointRepository.findByUserId(userId)
        .map(point -> point.getBalance() >= amount)
        .orElse(false);
  }

  private Point findOrCreatePointAccount(Long userId) {
    return pointRepository.findByUserIdWithLock(userId)
        .orElseGet(() -> createNewPointAccount(userId));
  }

  private Point createNewPointAccount(Long userId) {
    log.info("포인트 계좌가 없어 새로 생성합니다. userId: {}", userId);

    try {
      Point newAccount = Point.openAccount(userId);
      Point savedPoint = pointRepository.save(newAccount);

      log.info("포인트 계좌 생성 완료 - userId: {}, 초기포인트: {}",
          userId, INITIAL_POINTS);

      return savedPoint;

    } catch (Exception e) {
      log.error("포인트 계좌 생성 실패: userId={}", userId, e);
      throw new PointException(ErrorCode.INTERNAL_SERVER_ERROR, e);
    }
  }
}