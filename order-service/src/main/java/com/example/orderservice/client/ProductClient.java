package com.example.orderservice.client;

import com.example.orderservice.dto.response.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-client", url = "${product-service.api-url}")
public interface ProductClient {

  @GetMapping("/{id}")
  ProductResponse findProductById(@PathVariable("id") Long productId);
}