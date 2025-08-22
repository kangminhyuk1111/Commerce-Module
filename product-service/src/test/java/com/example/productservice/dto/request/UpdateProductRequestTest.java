package com.example.productservice.dto.request;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.productservice.entity.Product;
import org.junit.jupiter.api.Test;

class UpdateProductRequestTest {

  @Test
  void toDomain_테스트() {
    String name = "icecream";
    Integer price = 1000;
    Integer stock = 100;
    final UpdateProductRequest request = new UpdateProductRequest(name, price, stock);

    final Product product = request.toDomain();

    assertThat(request.name()).isEqualTo(product.getName());
    assertThat(request.price()).isEqualTo(product.getPrice());
    assertThat(request.stock()).isEqualTo(product.getStock());
  }
}