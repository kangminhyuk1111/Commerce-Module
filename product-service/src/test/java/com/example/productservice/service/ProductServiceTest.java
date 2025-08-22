package com.example.productservice.service;

import com.example.productservice.dto.request.CreateProductRequest;
import com.example.productservice.dto.request.UpdateProductRequest;
import com.example.productservice.dto.response.ProductResponse;
import com.example.productservice.exception.ProductNotFoundException;
import com.example.productservice.repository.FakeProductRepository;
import com.example.productservice.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductServiceTest {

  private ProductService productService;

  @BeforeEach
  void setUp() {
    final ProductRepository productRepository = new FakeProductRepository();
    productService = new ProductService(productRepository);
  }

  @Test
  void 상품_생성_후_조회_테스트() {
    CreateProductRequest request = new CreateProductRequest("노트북", 1000000, 10);

    ProductResponse savedProduct = productService.save(request);
    ProductResponse foundProduct = productService.findById(savedProduct.id());

    assertThat(foundProduct.id()).isEqualTo(savedProduct.id());
    assertThat(foundProduct.name()).isEqualTo("노트북");
    assertThat(foundProduct.price()).isEqualTo(1000000);
    assertThat(foundProduct.stock()).isEqualTo(10);
  }

  @Test
  void 존재하지_않는_상품은_조회하면_예외가_발생한다() {
    Long notExistId = 999L;

    assertThatThrownBy(() -> productService.findById(notExistId))
        .isInstanceOf(ProductNotFoundException.class);
  }

  @Test
  void 여러_상품_생성_후_전체_조회_테스트() {
    productService.save(new CreateProductRequest("노트북", 1000000, 10));
    productService.save(new CreateProductRequest("마우스", 30000, 50));

    List<ProductResponse> products = productService.findAll();

    assertThat(products).hasSize(2);
    assertThat(products.stream().map(ProductResponse::name))
        .containsExactlyInAnyOrder("노트북", "마우스");
  }

  @Test
  void 상품_수정_테스트() {
    ProductResponse savedProduct = productService.save(
        new CreateProductRequest("기존상품", 500000, 5)
    );
    UpdateProductRequest updateRequest = new UpdateProductRequest("수정된상품", 800000, 15);

    ProductResponse updatedProduct = productService.update(savedProduct.id(), updateRequest);

    assertThat(updatedProduct.id()).isEqualTo(savedProduct.id());
    assertThat(updatedProduct.name()).isEqualTo("수정된상품");
    assertThat(updatedProduct.price()).isEqualTo(800000);
    assertThat(updatedProduct.stock()).isEqualTo(15);
  }

  @Test
  void 존재하지_않는_ID로_상품을_수정하면_예외를_발생시킨다() {
    long nonExistentId = 999L;
    UpdateProductRequest updateRequest = new UpdateProductRequest("수정된상품", 800000, 15);

    assertThatThrownBy(() -> productService.update(nonExistentId, updateRequest))
        .isInstanceOf(RuntimeException.class);
  }

  @Test
  void 상품_삭제_후_조회_실패_테스트() {
    ProductResponse savedProduct = productService.save(
        new CreateProductRequest("삭제될상품", 100000, 1)
    );

    productService.delete(savedProduct.id());

    assertThatThrownBy(() -> productService.findById(savedProduct.id()))
        .isInstanceOf(RuntimeException.class);
  }
}