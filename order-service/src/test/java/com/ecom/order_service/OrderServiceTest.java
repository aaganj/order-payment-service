package com.ecom.order_service;

import com.ecom.order_service.dto.CreateOrderRequest;
import com.ecom.order_service.entity.Order;
import com.ecom.order_service.entity.OrderStatus;
import com.ecom.order_service.entity.OutboxEvent;
import com.ecom.order_service.exception.OrderNotFoundException;
import com.ecom.order_service.repository.OrderRepository;
import com.ecom.order_service.repository.OutBoxEventRepository;
import com.ecom.order_service.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OutBoxEventRepository outBoxEventRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private OrderService orderService;



    @Test
    void shouldCreateOrderAndOutboxEvent() throws Exception {

        CreateOrderRequest request = new CreateOrderRequest();
        request.setCustomerId(101L);
        request.setAmount(new BigDecimal("100.00"));
        request.setCurrency("SGD");

        Order savedOrder = new Order();
        savedOrder.setOrderId(1L);
        savedOrder.setCustomerId(101L);
        savedOrder.setAmount(new BigDecimal("100.00"));
        savedOrder.setCurrency("SGD");
        savedOrder.setStatus(OrderStatus.PENDING);

        when(orderRepository.save(any(Order.class)))
                .thenReturn(savedOrder);

        when(objectMapper.writeValueAsString(any(Order.class)))
                .thenReturn("{\"orderId\":1}");

        when(outBoxEventRepository.save(any(OutboxEvent.class)))
                .thenReturn(new OutboxEvent());

        Order result = orderService.createOrder(request);

        assertNotNull(result);
        assertEquals(1L, result.getOrderId());
        assertEquals(101L, result.getCustomerId());
        assertEquals(new BigDecimal("100.00"), result.getAmount());
        assertEquals("SGD", result.getCurrency());
        assertEquals(OrderStatus.PENDING, result.getStatus());

        verify(orderRepository).save(any(Order.class));
        verify(outBoxEventRepository).save(any(OutboxEvent.class));
    }

    @Test
    void shouldCreateCorrectOutboxEvent() throws Exception {

        CreateOrderRequest request = new CreateOrderRequest();
        request.setCustomerId(101L);
        request.setAmount(new BigDecimal("100.00"));
        request.setCurrency("SGD");

        Order savedOrder = new Order();
        savedOrder.setOrderId(1L);
        savedOrder.setCustomerId(101L);
        savedOrder.setAmount(new BigDecimal("100.00"));
        savedOrder.setCurrency("SGD");
        savedOrder.setStatus(OrderStatus.PENDING);

        when(orderRepository.save(any(Order.class)))
                .thenReturn(savedOrder);

        ArgumentCaptor<OutboxEvent> captor =
                ArgumentCaptor.forClass(OutboxEvent.class);

        when(outBoxEventRepository.save(captor.capture()))
                .thenReturn(new OutboxEvent());

        Order result = orderService.createOrder(request);

        OutboxEvent event = captor.getValue();

        assertNotNull(event.getEventId());
        assertEquals("OrderCreated", event.getEventType());
        assertEquals(1L, event.getAggregateId());
        assertEquals("NEW", event.getStatus());
        assertEquals(0, event.getRetryCount());

        verify(orderRepository).save(any(Order.class));
        verify(outBoxEventRepository).save(any(OutboxEvent.class));
    }

    @Test
    void shouldReturnOrderWhenOrderExists() {
        Long orderId = 1l;

        Order order = new Order();
        order.setOrderId(orderId);
        order.setStatus(OrderStatus.PENDING);

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));

        Order result = orderService.getOrder(orderId);
        assertNotNull(result);
        assertEquals(orderId,result.getOrderId());


        verify(orderRepository).findById(orderId);
    }

    @Test
    void shouldThrowExceptionWhenOrderDoesNotExist() {

        Long orderId = 100L;

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.empty());

        assertThrows(
                OrderNotFoundException.class,
                () -> orderService.getOrder(orderId)
        );

        verify(orderRepository).findById(orderId);
    }

    @Test
    void shouldReturnOrderStatus() {

        Long orderId = 1L;

        Order order = new Order();
        order.setOrderId(orderId);
        order.setStatus(OrderStatus.PAYMENT_PROCESSING);

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));

        OrderStatus result =
                orderService.getOrderStatus(orderId);

        assertEquals(
                OrderStatus.PAYMENT_PROCESSING,
                result
        );
    }

    @Test
    void shouldThrowExceptionWhenGettingStatusForNonExistingOrder() {

        Long orderId = 100L;

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.empty());

        assertThrows(
                OrderNotFoundException.class,
                () -> orderService.getOrderStatus(orderId)
        );
    }

    @Test
    void shouldCancelOrder() {

        Long orderId = 1L;

        Order order = new Order();
        order.setOrderId(orderId);
        order.setStatus(OrderStatus.PENDING);

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));

        when(orderRepository.save(order))
                .thenReturn(order);

        Order result =
                orderService.cancelOrder(orderId);

        assertEquals(
                OrderStatus.CANCELLED,
                result.getStatus()
        );

        verify(orderRepository).findById(orderId);
        verify(orderRepository).save(order);
    }

    @Test
    void shouldThrowExceptionWhenCancellingNonExistingOrder() {

        Long orderId = 100L;

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.empty());

        assertThrows(
                OrderNotFoundException.class,
                () -> orderService.cancelOrder(orderId)
        );

        verify(orderRepository).findById(orderId);
        verify(orderRepository, never()).save(any());
    }
}
