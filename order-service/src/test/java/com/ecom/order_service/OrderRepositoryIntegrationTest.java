package com.ecom.order_service;

import com.ecom.order_service.entity.Order;
import com.ecom.order_service.entity.OrderStatus;
import com.ecom.order_service.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class OrderRepositoryIntegrationTest {

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void shouldSaveAndFindOrder() {

        Order order = new Order();
         order.setCustomerId(1001l);
         order.setAmount(new BigDecimal(150));
         order.setCurrency("SGD");
        order.setStatus(OrderStatus.PENDING);

        Order saved = orderRepository.save(order);

        assertNotNull(saved.getOrderId());

        Optional<Order> result =
                orderRepository.findById(saved.getOrderId());

        assertTrue(result.isPresent());

        assertEquals(
                OrderStatus.PENDING,
                result.get().getStatus()
        );
    }
}
