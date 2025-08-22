package com.example.productservice.controller;

import com.example.productservice.dto.request.CreateProductRequest;
import com.example.productservice.dto.request.RestoreProductRequest;
import com.example.productservice.dto.request.UpdateProductRequest;
import com.example.productservice.dto.response.ProductResponse;
import com.example.productservice.service.ProductService;
import com.example.productservice.dto.request.ReduceProductRequest;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/*
✅ 필수 구현
- GET /api/products - 상품 목록 조회 (기본 페이징)
- GET /api/products/{id} - 상품 상세 조회
- POST /api/products - 상품 등록 (관리자용)
*/
@RestController
@RequestMapping("/api/products")
public class ProductController {

  private final ProductService productService;

  public ProductController(final ProductService productService) {
    this.productService = productService;
  }

  @GetMapping
  public List<ProductResponse> findAllProducts() {
    return productService.findAll();
  }

  @GetMapping("/{id}")
  public ProductResponse findProductById(@PathVariable final Long id) {
    return productService.findById(id);
  }

  @PostMapping
  public ProductResponse addNewProduct(@RequestBody final CreateProductRequest request) {
    return productService.save(request);
  }

  @PutMapping("/{id}")
  public ProductResponse updateProduct(@PathVariable final Long id, @RequestBody UpdateProductRequest request) {
    return productService.update(id, request);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public void deleteProduct(@PathVariable final Long id) {
    productService.delete(id);
  }

  @PutMapping("/reduce")
  @ResponseStatus(HttpStatus.OK)
  public void reduceProductStock(@RequestBody ReduceProductRequest request) {
    productService.reduceStock(request);
  }

  @PutMapping("/restore")
  @ResponseStatus(HttpStatus.OK)
  public void restoreProductStock(@RequestBody RestoreProductRequest request) {
    productService.restoreProduct(request);
  }
}
