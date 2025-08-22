package com.example.productservice.dto.request;

import com.example.productservice.entity.Product;

public record UpdateProductRequest(String name, Integer price, Integer stock) {
  public Product toDomain() {
    return new Product(name, price, stock);
  }
}
