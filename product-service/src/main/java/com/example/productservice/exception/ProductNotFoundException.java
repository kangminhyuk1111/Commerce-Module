package com.example.productservice.exception;

public class ProductNotFoundException extends ProductException {

  public ProductNotFoundException(Long id) {
    super("상품을 찾을 수 없습니다. ID: " + id);
  }

  public ProductNotFoundException(String message) {
    super(message);
  }
}