package com.example.productservice.entity;

import com.example.productservice.exception.ProductException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductTest {

  @Nested
  @DisplayName("상품 생성 할 때,")
  class ProductCreation {

    @Test
    @DisplayName("정상적인 상품이 생성된다.")
    void 상품_생성_테스트() {
      String name = "노트북";
      Integer price = 1000000;
      Integer stock = 10;

      Product product = new Product(name, price, stock);

      assertThat(product.getName()).isEqualTo(name);
      assertThat(product.getPrice()).isEqualTo(price);
      assertThat(product.getStock()).isEqualTo(stock);
      assertThat(product.getId()).isNull(); // 생성시 ID는 null이어야 함
    }

    @Test
    @DisplayName("ID를 포함한 상품이 생성된다.")
    void ID를_포함한_상품_생성_테스트() {
      Long id = 1L;
      String name = "마우스";
      Integer price = 30000;
      Integer stock = 100;

      Product product = new Product(id, name, price, stock);

      assertThat(product.getId()).isEqualTo(id);
      assertThat(product.getName()).isEqualTo(name);
      assertThat(product.getPrice()).isEqualTo(price);
      assertThat(product.getStock()).isEqualTo(stock);
    }

    @Test
    @DisplayName("기본 생성자로 상품이 생성된다.")
    void 기본_생성자_상품_생성_테스트() {
      Product product = new Product();

      assertThat(product.getId()).isNull();
      assertThat(product.getName()).isNull();
      assertThat(product.getPrice()).isNull();
      assertThat(product.getStock()).isNull();
    }

    @Test
    @DisplayName("최소값으로 상품이 생성된다.")
    void 최소값으로_상품_생성() {
      String name = "a";
      Integer price = 0;
      Integer stock = 0;

      Product product = new Product(name, price, stock);

      assertThat(product.getName()).isEqualTo(name);
      assertThat(product.getPrice()).isEqualTo(price);
      assertThat(product.getStock()).isEqualTo(stock);
    }

    @Test
    @DisplayName("공백이 포함된 상품명으로 생성된다.")
    void 공백이_포함된_상품명으로_생성() {
      String name = "  유효한 상품명  ";
      Integer price = 50000;
      Integer stock = 5;

      Product product = new Product(name, price, stock);

      assertThat(product.getName()).isEqualTo(name.trim());
    }
  }

  @Nested
  @DisplayName("상품 생성 시 검증 실패로,")
  class ProductCreationValidationFailure {

    @Test
    @DisplayName("null 상품명으로 생성 시 예외가 발생한다.")
    void null_상품명_예외() {
      String name = null;
      Integer price = 10000;
      Integer stock = 5;

      assertThatThrownBy(() -> new Product(name, price, stock))
          .isInstanceOf(ProductException.class);
    }

    @ParameterizedTest
    @EmptySource
    @ValueSource(strings = {"   ", "\t", "\n", "  \t  \n  "})
    @DisplayName("빈 상품명으로 생성 시 예외가 발생한다.")
    void 빈_상품명_예외(String emptyName) {
      Integer price = 10000;
      Integer stock = 5;

      assertThatThrownBy(() -> new Product(emptyName, price, stock))
          .isInstanceOf(ProductException.class);
    }

    @Test
    @DisplayName("null 가격으로 생성 시 예외가 발생한다.")
    void null_가격_예외() {
      String name = "상품";
      Integer price = null;
      Integer stock = 5;

      assertThatThrownBy(() -> new Product(name, price, stock))
          .isInstanceOf(ProductException.class);
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, -100, -1000000})
    @DisplayName("음수 가격으로 생성 시 예외가 발생한다.")
    void 음수_가격_예외(Integer negativePrice) {
      String name = "상품";
      Integer stock = 5;

      assertThatThrownBy(() -> new Product(name, negativePrice, stock))
          .isInstanceOf(ProductException.class);
    }

    @Test
    @DisplayName("null 재고로 생성 시 예외가 발생한다.")
    void null_재고_예외() {
      String name = "상품";
      Integer price = 10000;
      Integer stock = null;

      assertThatThrownBy(() -> new Product(name, price, stock))
          .isInstanceOf(ProductException.class);
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, -50, -999})
    @DisplayName("음수 재고로 생성 시 예외가 발생한다.")
    void 음수_재고_예외(Integer negativeStock) {
      String name = "상품";
      Integer price = 10000;

      assertThatThrownBy(() -> new Product(name, price, negativeStock))
          .isInstanceOf(ProductException.class);
    }

    @Test
    @DisplayName("ID 포함 생성자에서도 검증이 적용된다.")
    void ID포함_생성자_검증() {
      Long id = 1L;
      String name = null;
      Integer price = 10000;
      Integer stock = 5;

      assertThatThrownBy(() -> new Product(id, name, price, stock))
          .isInstanceOf(ProductException.class);
    }
  }

  @Nested
  @DisplayName("상품 정보 수정 시 검증 실패로,")
  class ProductUpdateValidationFailure {

    @Test
    @DisplayName("null 상품명으로 수정 시 예외가 발생한다.")
    void null_상품명_수정_예외() {
      Product product = new Product("기존상품", 10000, 5);

      assertThatThrownBy(() -> product.update(null, 20000, 10))
          .isInstanceOf(ProductException.class);
    }

    @Test
    @DisplayName("빈 상품명으로 수정 시 예외가 발생한다.")
    void 빈_상품명_수정_예외() {
      Product product = new Product("기존상품", 10000, 5);

      assertThatThrownBy(() -> product.update("   ", 20000, 10))
          .isInstanceOf(ProductException.class);
    }

    @Test
    @DisplayName("null 가격으로 수정 시 예외가 발생한다.")
    void null_가격_수정_예외() {
      Product product = new Product("기존상품", 10000, 5);

      assertThatThrownBy(() -> product.update("수정상품", null, 10))
          .isInstanceOf(ProductException.class);
    }

    @Test
    @DisplayName("음수 가격으로 수정 시 예외가 발생한다.")
    void 음수_가격_수정_예외() {
      Product product = new Product("기존상품", 10000, 5);

      assertThatThrownBy(() -> product.update("수정상품", -1000, 10))
          .isInstanceOf(ProductException.class);
    }

    @Test
    @DisplayName("null 재고로 수정 시 예외가 발생한다.")
    void null_재고_수정_예외() {
      Product product = new Product("기존상품", 10000, 5);

      assertThatThrownBy(() -> product.update("수정상품", 20000, null))
          .isInstanceOf(ProductException.class);
    }

    @Test
    @DisplayName("음수 재고로 수정 시 예외가 발생한다.")
    void 음수_재고_수정_예외() {
      Product product = new Product("기존상품", 10000, 5);

      assertThatThrownBy(() -> product.update("수정상품", 20000, -5))
          .isInstanceOf(ProductException.class);
    }

    @Test
    @DisplayName("예외 발생 시 기존 값이 유지된다.")
    void 예외_발생시_기존값_유지() {
      Product product = new Product("기존상품", 10000, 5);
      String originalName = product.getName();
      Integer originalPrice = product.getPrice();
      Integer originalStock = product.getStock();

      assertThatThrownBy(() -> product.update(null, 20000, 10))
          .isInstanceOf(ProductException.class);

      assertThat(product.getName()).isEqualTo(originalName);
      assertThat(product.getPrice()).isEqualTo(originalPrice);
      assertThat(product.getStock()).isEqualTo(originalStock);
    }
  }
}