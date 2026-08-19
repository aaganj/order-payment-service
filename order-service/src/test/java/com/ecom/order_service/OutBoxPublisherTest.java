package com.ecom.order_service;

import com.ecom.order_service.entity.OutboxEvent;
import com.ecom.order_service.publisher.OutboxPublisher;
import com.ecom.order_service.repository.OutBoxEventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jms.core.JmsTemplate;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OutBoxPublisherTest {
    @Mock
    private OutBoxEventRepository outBoxEventRepository;

    @Mock
    private JmsTemplate jmsTemplate;

    @InjectMocks
    private OutboxPublisher outboxPublisher;

    @Test
    void shouldPublishNewOutboxEvent() {

        OutboxEvent event = new OutboxEvent();

        event.setEventId(UUID.randomUUID());
        event.setEventType("OrderCreated");
        event.setAggregateId(1L);
        event.setPayload("""
            {
                "orderId": 1,
                "customerId": 101,
                "amount": 100.00,
                "currency": "SGD"
            }
            """);
        event.setStatus("NEW");
        event.setRetryCount(0);

        when(outBoxEventRepository.findByStatus("NEW"))
                .thenReturn(List.of(event));

        outboxPublisher.publishEvents();

        verify(jmsTemplate).convertAndSend(
                "order.created",
                event.getPayload()
        );

        assertEquals("PUBLISHED", event.getStatus());

        verify(outBoxEventRepository).save(event);
    }

    @Test
    void shouldKeepEventAsNewWhenActiveMqFails() {

        OutboxEvent event = new OutboxEvent();

        event.setEventId(UUID.randomUUID());
        event.setEventType("OrderCreated");
        event.setAggregateId(1L);
        event.setPayload("{\"orderId\":1}");
        event.setStatus("NEW");
        event.setRetryCount(0);

        when(outBoxEventRepository.findByStatus("NEW"))
                .thenReturn(List.of(event));

        doThrow(new RuntimeException("ActiveMQ unavailable"))
                .when(jmsTemplate)
                .convertAndSend("order.created", event.getPayload());

        outboxPublisher.publishEvents();

        assertEquals("NEW", event.getStatus());

        verify(jmsTemplate).convertAndSend(
                "order.created",
                event.getPayload()
        );

        verify(outBoxEventRepository, never())
                .save(event);
    }
}
