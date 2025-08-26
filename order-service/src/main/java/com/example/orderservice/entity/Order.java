package com.example.orderservice.entity;

import com.example.orderservice.exception.ApplicationException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
public class Order {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Long memberId;

  @Column(nullable = false)
  private Integer totalPrice;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private OrderStatus status;

  @Column(nullable = false)
  private LocalDateTime orderDate;

  public Order() {
  }

  public Order(final Long memberId, final Integer totalPrice, final OrderStatus status, final LocalDateTime orderDate) {
    this.memberId = memberId;
    this.totalPrice = totalPrice;
    this.status = status;
    this.orderDate = orderDate;
  }

  public void complete() {
    if (this.status != OrderStatus.PAID) {
      throw new ApplicationException("결제 완료 주문만 완료 처리할 수 있습니다.");
    }
    this.status = OrderStatus.COMPLETED;
  }

  public void markAsPaid() {
    if (this.status != OrderStatus.PENDING) {
      throw new ApplicationException("대기 중인 주문만 취소 처리할 수 있습니다.");
    }
    this.status = OrderStatus.PAID;
  }

  public void cancel() {
    if (this.status != OrderStatus.PENDING) {
      throw new ApplicationException("대기 중인 주문만 취소 처리할 수 있습니다.");
    }
    this.status = OrderStatus.CANCELLED;
  }

  public Long getId() {
    return id;
  }

  public Long getMemberId() {
    return memberId;
  }

  public Integer getTotalPrice() {
    return totalPrice;
  }

  public OrderStatus getStatus() {
    return status;
  }

  public LocalDateTime getOrderDate() {
    return orderDate;
  }
}