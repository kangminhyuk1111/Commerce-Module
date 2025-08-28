package com.example.orderservice.service;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.orderservice.dto.request.CreateOrderRequest;
import com.example.orderservice.dto.request.OrderItemRequest;
import com.example.orderservice.dto.response.OrderResponse;
import com.example.orderservice.exception.ApplicationException;
import com.example.orderservice.repository.OrderRepository;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import feign.FeignException;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class OrderServiceIntegrationTest {

  /* product-service:8082 mocking */
  private static WireMockServer wireMockServer;
  public static final int PRODUCT_SERVICE_PORT = 8082;

  @Autowired
  private OrderService orderService;

  @Autowired
  private OrderRepository orderRepository;

  @BeforeEach
  void setUp() {
    wireMockServer = new WireMockServer(8082);
    wireMockServer.start();
    WireMock.configureFor("localhost", PRODUCT_SERVICE_PORT);

    setUpMockProduct();
  }

  @Test
  @DisplayName("주문 생성 - 성공")
  void 주문_생성_성공() {
    // given
    List<OrderItemRequest> items = List.of(
        new OrderItemRequest(1L, 10),
        new OrderItemRequest(2L, 20)
    );
    CreateOrderRequest request = new CreateOrderRequest(1L, items);

    // when
    OrderResponse result = orderService.createOrder(request);

    // then
    assertThat(result).isNotNull();
    assertThat(result.memberId()).isEqualTo(1L);
    assertThat(result.totalPrice()).isEqualTo(400000);
  }

  @Test
  @DisplayName("주문 생성 실패 - 빈 상품 목록")
  void 주문_생성_실패_빈_상품_목록() {
    // given
    CreateOrderRequest emptyItemsRequest = new CreateOrderRequest(1L, List.of());

    // when & then
    assertThatThrownBy(() -> orderService.createOrder(emptyItemsRequest))
        .isInstanceOf(ApplicationException.class)
        .hasMessage("주문 상품이 비어있습니다.");
  }

  @Test
  @DisplayName("주문 생성 실패 - null 상품 목록")
  void 주문_생성_실패_null_상품_목록() {
    // given
    CreateOrderRequest nullItemsRequest = new CreateOrderRequest(1L, null);

    // when & then
    assertThatThrownBy(() -> orderService.createOrder(nullItemsRequest))
        .isInstanceOf(ApplicationException.class)
        .hasMessage("주문 상품이 비어있습니다.");
  }

  @Test
  @DisplayName("주문 생성 실패 - 존재하지 않는 상품")
  void 주문_생성_실패_존재하지않는_상품() {
    // given
    List<OrderItemRequest> itemsWithNonExistentProduct = List.of(
        new OrderItemRequest(1000L, 1)
    );
    CreateOrderRequest request = new CreateOrderRequest(1L, itemsWithNonExistentProduct);

    // when & then
    assertThatThrownBy(() -> orderService.createOrder(request))
        .isInstanceOf(FeignException.class)
        .hasMessageContaining("404");
  }

  @Test
  @DisplayName("주문 조회 - 성공")
  void 주문_조회_성공() {
    // given
    List<OrderItemRequest> items = List.of(
        new OrderItemRequest(1L, 10),
        new OrderItemRequest(2L, 20)
    );
    CreateOrderRequest request = new CreateOrderRequest(1L, items);
    final OrderResponse createdOrder = orderService.createOrder(request);

    // when
    final OrderResponse foundOrder = orderService.findOrderById(createdOrder.id()); // 실제 생성된 ID 사용

    // then
    assertThat(foundOrder).isNotNull();
    assertThat(foundOrder.id()).isEqualTo(createdOrder.id());
    assertThat(foundOrder.memberId()).isEqualTo(createdOrder.memberId());
    assertThat(foundOrder.totalPrice()).isEqualTo(createdOrder.totalPrice());
  }

  @Test
  @DisplayName("주문 조회 실패 - 존재하지 않는 주문 ID")
  void 주문_조회_실패_존재하지않는_ID() {
    // given
    Long nonExistentOrderId = 999L;

    // when & then
    assertThatThrownBy(() -> orderService.findOrderById(nonExistentOrderId))
        .isInstanceOf(ApplicationException.class)
        .hasMessage("주문 정보를 찾을 수 없습니다.");
  }

  @Test
  @DisplayName("전체 주문 조회")
  void 전체_주문_조회() {
    // given
    List<OrderItemRequest> items1 = List.of(new OrderItemRequest(1L, 5));
    List<OrderItemRequest> items2 = List.of(new OrderItemRequest(2L, 3));

    CreateOrderRequest request1 = new CreateOrderRequest(1L, items1);
    CreateOrderRequest request2 = new CreateOrderRequest(2L, items2);

    orderService.createOrder(request1);
    orderService.createOrder(request2);

    // when
    List<OrderResponse> orders = orderService.findAllOrders();

    // then
    assertThat(orders).hasSize(2);
  }

  @Test
  @DisplayName("내 주문 조회")
  void 내_주문_조회() {
    // given
    Long userId1 = 1L;
    Long userId2 = 2L;

    List<OrderItemRequest> items1 = List.of(new OrderItemRequest(1L, 5));
    List<OrderItemRequest> items2 = List.of(new OrderItemRequest(2L, 3));
    List<OrderItemRequest> items3 = List.of(new OrderItemRequest(3L, 2));

    CreateOrderRequest request1 = new CreateOrderRequest(userId1, items1);
    CreateOrderRequest request2 = new CreateOrderRequest(userId2, items2);
    CreateOrderRequest request3 = new CreateOrderRequest(userId1, items3);

    orderService.createOrder(request1);
    orderService.createOrder(request2);
    orderService.createOrder(request3);

    // when
    List<OrderResponse> user1Orders = orderService.findMyOrders(userId1);

    // then
    assertThat(user1Orders).hasSize(2);
    assertThat(user1Orders).allMatch(order -> order.memberId().equals(userId1));
  }

  @Test
  @DisplayName("주문 취소 - 성공")
  void 주문_취소_성공() {
    // given
    List<OrderItemRequest> items = List.of(new OrderItemRequest(1L, 5));
    CreateOrderRequest request = new CreateOrderRequest(1L, items);
    OrderResponse createdOrder = orderService.createOrder(request);

    // when
    OrderResponse cancelledOrder = orderService.cancelOrder(createdOrder.id());

    // then
    assertThat(cancelledOrder).isNotNull();
    assertThat(cancelledOrder.id()).isEqualTo(createdOrder.id());
    // 주문 상태가 CANCELLED로 변경되었는지 확인 (실제 Order 엔티티 구조에 따라)
  }

  @Test
  @DisplayName("주문 취소 실패 - 존재하지 않는 주문")
  void 주문_취소_실패_존재하지않는_주문() {
    // given
    Long nonExistentOrderId = 999L;

    // when & then
    assertThatThrownBy(() -> orderService.cancelOrder(nonExistentOrderId))
        .isInstanceOf(ApplicationException.class)
        .hasMessage("주문 정보를 찾을 수 없습니다.");
  }

  @Test
  @DisplayName("주문 결제 처리 - 성공")
  void 주문_결제_처리_성공() {
    // given
    List<OrderItemRequest> items = List.of(new OrderItemRequest(1L, 5));
    CreateOrderRequest request = new CreateOrderRequest(1L, items);
    OrderResponse createdOrder = orderService.createOrder(request);

    // when
    OrderResponse paidOrder = orderService.processOrderPayment(createdOrder.id());

    // then
    assertThat(paidOrder).isNotNull();
    assertThat(paidOrder.id()).isEqualTo(createdOrder.id());
  }

  @Test
  @DisplayName("주문 결제 처리 실패 - 존재하지 않는 주문")
  void 주문_결제_처리_실패_존재하지않는_주문() {
    // given
    Long nonExistentOrderId = 999L;

    // when & then
    assertThatThrownBy(() -> orderService.processOrderPayment(nonExistentOrderId))
        .isInstanceOf(ApplicationException.class)
        .hasMessage("주문 정보를 찾을 수 없습니다.");
  }

  @Test
  @DisplayName("재고 부족 시 주문 실패")
  void 주문_생성_실패_재고_부족() {
    // given
    // 재고가 1개뿐인 상품 모킹
    stubFor(get(urlEqualTo("/api/products/4"))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody("""
                {
                    "id": 4,
                    "name": "재고부족상품",
                    "price": 20000,
                    "stock": 1
                }
                """)));

    List<OrderItemRequest> itemsWithInsufficientStock = List.of(
        new OrderItemRequest(4L, 5) // 재고 1개인데 5개 주문
    );
    CreateOrderRequest request = new CreateOrderRequest(1L, itemsWithInsufficientStock);

    // when & then
    // OrderItemService의 재고 검증 로직에 따라 예외 타입이 결정됩니다
    assertThatThrownBy(() -> orderService.createOrder(request))
        .isInstanceOf(Exception.class); // 실제 구현에 따라 구체적인 예외 타입으로 변경
  }

  @AfterEach
  void shutDown() {
    if (wireMockServer != null) {
      wireMockServer.stop();
    }
  }

  private void setUpMockProduct() {
    // 상품 1번 API 응답 모킹
    stubFor(get(urlEqualTo("/api/products/1"))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody("""
                {
                    "id": 1,
                    "name": "상품1",
                    "price": 10000,
                    "stock": 100
                }
                """)));

    // 상품 2번 API 응답 모킹
    stubFor(get(urlEqualTo("/api/products/2"))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody("""
                {
                    "id": 2,
                    "name": "상품2",
                    "price": 15000,
                    "stock": 50
                }
                """)));

    // 상품 3번 API 응답 모킹
    stubFor(get(urlEqualTo("/api/products/3"))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody("""
                {
                    "id": 3,
                    "name": "상품3",
                    "price": 5000,
                    "stock": 30
                }
                """)));

    // 존재하지 않는 상품 요청 모킹
    stubFor(get(urlEqualTo("/api/products/1000"))
        .willReturn(aResponse()
            .withStatus(404)));
  }
}
