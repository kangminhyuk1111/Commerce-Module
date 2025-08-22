package com.example.orderservice.exception;

public class ProductNotFoundException extends ApplicationException {

  public ProductNotFoundException(Long productId) {
    super("주문하려는 상품을 찾을 수 없습니다. 상품 번호 : " + productId);
  }
}
