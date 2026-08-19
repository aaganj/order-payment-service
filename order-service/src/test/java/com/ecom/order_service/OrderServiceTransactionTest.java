package com.ecom.order_service;

import com.ecom.order_service.dto.CreateOrderRequest;
import com.ecom.order_service.repository.OrderRepository;
import com.ecom.order_service.repository.OutBoxEventRepository;
import com.ecom.order_service.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class OrderServiceTransactionTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OutBoxEventRepository outBoxEventRepository;



    @Test
    void shouldRollbackOrderWhenOutboxSaveFails() {

        CreateOrderRequest request = new CreateOrderRequest();
        request.setCustomerId(101L);
        request.setAmount(new BigDecimal("100.00"));
        request.setCurrency("SGD");

        assertThrows(
                Exception.class,
                () -> orderService.createOrder(request)
        );

        assertEquals(
                0,
                orderRepository.count()
        );

        assertEquals(
                0,
                outBoxEventRepository.count()
        );
    }
}
