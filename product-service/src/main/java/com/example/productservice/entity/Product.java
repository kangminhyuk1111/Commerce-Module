package com.example.productservice.entity;

import com.example.productservice.exception.ProductException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Product {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "price", nullable = false)
  private Integer price;

  @Column(name = "stock", nullable = false)
  private Integer stock;

  public Product() {
  }

  public Product(final String name, final Integer price, final Integer stock) {
    validateName(name);
    validatePrice(price);
    validateStock(stock);

    this.name = name.trim();
    this.price = price;
    this.stock = stock;
  }

  public Product(final Long id, final String name, final Integer price, final Integer stock) {
    validateName(name);
    validatePrice(price);
    validateStock(stock);

    this.id = id;
    this.name = name.trim();
    this.price = price;
    this.stock = stock;
  }

  public void update(final String name, final Integer price, final Integer stock) {
    validateName(name);
    validatePrice(price);
    validateStock(stock);

    this.name = name.trim();
    this.price = price;
    this.stock = stock;
  }

  public void reduceStock(final Integer quantity) {
    if (quantity == null) {
      throw new ProductException("차감할 수량은 null일 수 없습니다.");
    }
    if (quantity <= 0) {
      throw new ProductException("차감할 수량은 0보다 커야 합니다.");
    }
    if (this.stock < quantity) {
      throw new ProductException(
          String.format("재고가 부족합니다. 현재 재고: %d, 요청 수량: %d", this.stock, quantity)
      );
    }

    this.stock = this.stock - quantity;
  }

  public void restoreStock(final Integer quantity) {
    if (quantity == null) {
      throw new ProductException("복구할 수량은 null일 수 없습니다.");
    }
    if (quantity <= 0) {
      throw new ProductException("복구할 수량은 0보다 커야 합니다.");
    }

    this.stock = this.stock + quantity;
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public Integer getPrice() {
    return price;
  }

  public Integer getStock() {
    return stock;
  }

  // 검증 메서드들
  private void validateName(final String name) {
    if (name == null) {
      throw new ProductException("상품명은 null일 수 없습니다.");
    }
    if (name.trim().isEmpty()) {
      throw new ProductException("상품명은 빈 문자열일 수 없습니다.");
    }
  }

  private void validatePrice(final Integer price) {
    if (price == null) {
      throw new ProductException("가격은 null일 수 없습니다.");
    }
    if (price < 0) {
      throw new ProductException("가격은 0보다 작을 수 없습니다.");
    }
  }

  private void validateStock(final Integer stock) {
    if (stock == null) {
      throw new ProductException("재고는 null일 수 없습니다.");
    }
    if (stock < 0) {
      throw new ProductException("재고는 0보다 작을 수 없습니다.");
    }
  }
}