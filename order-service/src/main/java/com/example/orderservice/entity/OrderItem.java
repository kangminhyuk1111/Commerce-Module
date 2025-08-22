package com.example.orderservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "order_items")
public class OrderItem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Long orderId;

  @Column(nullable = false)
  private Long productId;

  @Column(nullable = false)
  private String productName;

  @Column(nullable = false)
  private Integer productPrice;

  @Column(nullable = false)
  private Integer orderQuantity;

  @Column(nullable = false)
  private Integer totalPrice;

  public OrderItem() {
  }

  public OrderItem(Long orderId, Long productId, String productName, Integer productPrice, Integer orderQuantity) {
    this.orderId = orderId;
    this.productId = productId;
    this.productName = productName;
    this.productPrice = productPrice;
    this.orderQuantity = orderQuantity;
    this.totalPrice = productPrice * orderQuantity;
  }

  public Long getId() {
    return id;
  }

  public Long getOrderId() {
    return orderId;
  }

  public Long getProductId() {
    return productId;
  }

  public String getProductName() {
    return productName;
  }

  public Integer getProductPrice() {
    return productPrice;
  }

  public Integer getOrderQuantity() {
    return orderQuantity;
  }

  public Integer getTotalPrice() {
    return totalPrice;
  }
}