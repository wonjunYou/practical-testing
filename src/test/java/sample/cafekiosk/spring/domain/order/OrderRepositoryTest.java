package sample.cafekiosk.spring.domain.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.*;
import static sample.cafekiosk.spring.domain.order.OrderStatus.INIT;
import static sample.cafekiosk.spring.domain.product.ProductSellingStatus.SELLING;
import static sample.cafekiosk.spring.domain.product.ProductType.BAKERY;
import static sample.cafekiosk.spring.domain.product.ProductType.HANDMADE;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import sample.cafekiosk.spring.domain.product.Product;
import sample.cafekiosk.spring.domain.product.ProductRepository;
import sample.cafekiosk.spring.domain.product.ProductSellingStatus;
import sample.cafekiosk.spring.domain.product.ProductType;


@SpringBootTest
@ActiveProfiles("test")
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @DisplayName("시작 시간과 종료 시간 사이, 결제 완료된 상품을 조회한다.")
    @Test
    void findOrdersBy() {
        // given
        Product product1 = Product.builder().productNumber("001")
                .type(HANDMADE)
                .sellingStatus(SELLING)
                .name("아메리카노")
                .price(4000)
                .build();

        Product product2 = Product.builder().productNumber("002")
                .type(HANDMADE)
                .sellingStatus(SELLING)
                .name("핫초코")
                .price(5000)
                .build();

        Product product3 = Product.builder().productNumber("003")
                .type(BAKERY)
                .sellingStatus(SELLING)
                .name("소금빵")
                .price(3000)
                .build();
        productRepository.saveAll(List.of(product1, product2, product3));

        List<Product> orderProducts = productRepository.findAllByProductNumberIn(List.of("001", "002"));
        LocalDateTime registeredDateTime = LocalDateTime.now();


        Order order1 = Order.create(orderProducts, registeredDateTime);
        Order order2 = Order.create(orderProducts, registeredDateTime.minusHours(3));
        orderRepository.saveAll(List.of(order1, order2));
        // when
        List<Order> orders = orderRepository.findOrdersBy(registeredDateTime.minusHours(1),
                registeredDateTime.plusHours(1), INIT);

        // then
        assertThat(orders).hasSize(1)
                .extracting("id", "orderStatus")
                .contains(
                    tuple("001", INIT)
                );
    }

}