package com.ecom.order_service;

import com.ecom.order_service.controller.OrderController;
import com.ecom.order_service.dto.CreateOrderRequest;
import com.ecom.order_service.entity.Order;
import com.ecom.order_service.entity.OrderStatus;
import com.ecom.order_service.service.OrderService;

import static org.mockito.Mockito.verify;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @Test
    void shouldCreateOrder() throws Exception {

        Order order = new Order();
        order.setOrderId(1L);
        order.setStatus(OrderStatus.PENDING);

        when(orderService.createOrder(any(CreateOrderRequest.class)))
                .thenReturn(order);

        mockMvc.perform(
                        post("/api/orders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                        {
                            "customerId": 1001,
                            "amount": 150.00,
                            "currency": "SGD"
                        }
                    """)
                )
                .andExpect(status().isCreated());
    }

    @Test
    void shouldGetOrder() throws Exception {

        Long orderId = 1L;

        Order order = new Order();
        order.setOrderId(orderId);
        order.setStatus(OrderStatus.PENDING);

        when(orderService.getOrder(orderId))
                .thenReturn(order);

        mockMvc.perform(
                        get("/api/orders/{orderId}", orderId)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(1))
                .andExpect(jsonPath("$.status").value("PENDING"));

        verify(orderService).getOrder(orderId);
    }

    @Test
    void shouldCancelOrder() throws Exception {

        Long orderId = 1L;

        Order order = new Order();
        order.setOrderId(orderId);
        order.setStatus(OrderStatus.CANCELLED);

        when(orderService.cancelOrder(orderId))
                .thenReturn(order);

        mockMvc.perform(
                        post("/api/orders/{orderId}/cancel", orderId)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(1))
                .andExpect(jsonPath("$.status").value("CANCELLED"));

        verify(orderService).cancelOrder(orderId);
    }

    @Test
    void shouldGetOrderStatus() throws Exception {

        Long orderId = 1L;

        when(orderService.getOrderStatus(orderId))
                .thenReturn(OrderStatus.PAYMENT_PROCESSING);

        mockMvc.perform(
                        get("/api/orders/{orderId}/status", orderId)
                )
                .andExpect(status().isOk())
                .andExpect(content().string("\"PAYMENT_PROCESSING\""));

        verify(orderService).getOrderStatus(orderId);
    }
}
