package com.example.pointservice.domain;

import com.example.pointservice.exception.ErrorCode;
import com.example.pointservice.exception.PointException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "points")
public class Point {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private Long userId;

  @Column(nullable = false)
  private Integer balance;

  @UpdateTimestamp
  @Column(nullable = false)
  private LocalDateTime lastUpdated;

  public Point() {
  }

  private Point(final Long userId, final Integer balance) {
    this.userId = userId;
    this.balance = balance;
  }

  public Point(final Long id, final Long userId, final Integer balance,
      final LocalDateTime lastUpdated) {
    this.id = id;
    this.userId = userId;
    this.balance = balance;
    this.lastUpdated = lastUpdated;
  }

  public static Point openAccount(Long userId) {
    if (userId == null) {
      throw new PointException(ErrorCode.USER_ID_NOT_NULL);
    }

    return new Point(userId, 0);
  }

  // 포인트 추가
  public void addPoint(final Integer balance) {
    this.balance = this.balance + balance;
  }

  // 포인트 사용
  public void usePoint(final Integer balance) {
    if (this.balance < balance) {
      throw new PointException(ErrorCode.INSUFFICIENT_BALANCE);
    }

    this.balance = this.balance - balance;
  }

  // 포인트 환불
  public void refundPoint(final Integer balance) {
    this.balance = this.balance + balance;
  }

  public Long getId() {
    return id;
  }

  public Long getUserId() {
    return userId;
  }

  public Integer getBalance() {
    return balance;
  }

  public LocalDateTime getLastUpdated() {
    return lastUpdated;
  }
}
