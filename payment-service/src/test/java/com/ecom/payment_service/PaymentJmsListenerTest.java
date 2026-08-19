package com.ecom.payment_service;

import com.ecom.payment_service.event.OrderCreatedEvent;
import com.ecom.payment_service.listener.OrderCreatedListener;
import com.ecom.payment_service.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentJmsListenerTest {
    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private OrderCreatedListener listener;

    @Test
    void shouldProcessOrderCreatedMessage() throws Exception {

        String message = """
            {
                "eventId": "550e8400-e29b-41d4-a716-446655440000",
                "eventType": "OrderCreated",
                "orderId": 1,
                "customerId": 101,
                "amount": 100.00,
                "currency": "SGD"
            }
            """;

        OrderCreatedEvent event = new OrderCreatedEvent();

        event.setEventId(
                UUID.fromString("550e8400-e29b-41d4-a716-446655440000")
        );
        event.setEventType("OrderCreated");
        event.setOrderId(1L);
        event.setCustomerId(101L);
        event.setAmount(new BigDecimal("100.00"));
        event.setCurrency("SGD");

        when(objectMapper.readValue(message, OrderCreatedEvent.class))
                .thenReturn(event);

        listener.consume(message);

        verify(paymentService)
                .processOrderCreated(event);
    }

    @Test
    void shouldThrowExceptionWhenPaymentProcessingFails() throws Exception {

        String message = """
            {
                "eventId": "550e8400-e29b-41d4-a716-446655440000",
                "eventType": "OrderCreated",
                "orderId": 1
            }
            """;

        OrderCreatedEvent event = new OrderCreatedEvent();

        when(objectMapper.readValue(message, OrderCreatedEvent.class))
                .thenReturn(event);

        doThrow(new RuntimeException("Payment processing failed"))
                .when(paymentService)
                .processOrderCreated(event);

        assertThrows(
                RuntimeException.class,
                () -> listener.consume(message)
        );

        verify(paymentService)
                .processOrderCreated(event);
    }
}
