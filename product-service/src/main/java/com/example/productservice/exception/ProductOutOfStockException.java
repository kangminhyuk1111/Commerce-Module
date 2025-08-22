package com.example.productservice.exception;

public class ProductOutOfStockException extends ProductException {

  public ProductOutOfStockException() {
    super("재고가 부족합니다.");
  }
}
