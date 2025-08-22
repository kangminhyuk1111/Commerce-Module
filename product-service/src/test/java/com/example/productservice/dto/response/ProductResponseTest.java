package com.example.productservice.dto.response;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.productservice.entity.Product;
import org.junit.jupiter.api.Test;

class ProductResponseTest {

  @Test
  void of_테스트() {
    Long id = 1L;
    String name = "icecream";
    Integer price = 1000;
    Integer stock = 100;
    final Product product = new Product(id, name, price, stock);

    final ProductResponse response = ProductResponse.of(product);

    assertThat(response.id()).isEqualTo(product.getId());
    assertThat(response.name()).isEqualTo(product.getName());
  }
}