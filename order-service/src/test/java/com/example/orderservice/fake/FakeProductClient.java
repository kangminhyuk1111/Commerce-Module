package com.example.orderservice.fake;

import com.example.orderservice.client.ProductClient;
import com.example.orderservice.dto.response.ProductResponse;

import java.util.HashMap;
import java.util.Map;

public class FakeProductClient implements ProductClient {

  private final Map<Long, ProductResponse> products = new HashMap<>();

  @Override
  public ProductResponse findProductById(Long productId) {
    ProductResponse product = products.get(productId);
    if (product == null) {
      throw new RuntimeException("상품을 찾을 수 없습니다: " + productId);
    }
    return product;
  }

  public void addProduct(ProductResponse product) {
    products.put(product.id(), product);
  }

  public void addProduct(Long id, String name, Integer price, Integer stock) {
    ProductResponse product = new ProductResponse(id, name, price, stock);
    products.put(id, product);
  }

  public void clear() {
    products.clear();
  }

  public int size() {
    return products.size();
  }

  public boolean exists(Long productId) {
    return products.containsKey(productId);
  }

  public void removeProduct(Long productId) {
    products.remove(productId);
  }
}