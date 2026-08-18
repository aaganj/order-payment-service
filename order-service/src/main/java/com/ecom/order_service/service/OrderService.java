package com.ecom.order_service.service;

import com.ecom.order_service.dto.CreateOrderRequest;
import com.ecom.order_service.entity.Order;
import com.ecom.order_service.entity.OrderStatus;
import com.ecom.order_service.entity.OutboxEvent;
import com.ecom.order_service.event.OrderCreatedEvent;
import com.ecom.order_service.repository.OrderRepository;
import com.ecom.order_service.repository.OutBoxEventRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OutBoxEventRepository outBoxEventRepository;
    private final ObjectMapper objectMapper;

    public OrderService(OrderRepository orderRepository
            ,OutBoxEventRepository outBoxEventRepository,
                        ObjectMapper objectMapper) {
        this.orderRepository = orderRepository;
        this.outBoxEventRepository=outBoxEventRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public Order createOrder(CreateOrderRequest orderRequest){
        Order order = new Order();
        order.setCustomerId(orderRequest.getCustomerId());
        order.setAmount(orderRequest.getAmount());
        order.setCurrency(orderRequest.getCurrency());
        order.setStatus(OrderStatus.PENDING);

        LocalDateTime now = LocalDateTime.now();
        order.setCreatedAt(now);
        order.setUpdatedAt(now);

        Order savedOrder = orderRepository.save(order);

        UUID eventId = UUID.randomUUID();

        OrderCreatedEvent orderCreatedEvent = new OrderCreatedEvent();
        orderCreatedEvent.setEventId(eventId);
        orderCreatedEvent.setEventType("OrderCreated");
        orderCreatedEvent.setOrderId(savedOrder.getOrderId());
        orderCreatedEvent.setCustomerId(savedOrder.getCustomerId());
        orderCreatedEvent.setAmount(savedOrder.getAmount());
        orderCreatedEvent.setCurrency(savedOrder.getCurrency());

        OutboxEvent event = new OutboxEvent();
        event.setEventId(eventId);
        event.setEventType("OrderCreated");
        event.setAggregateId(savedOrder.getOrderId());
        event.setStatus("NEW");
        event.setCreatedAt(now);
        event.setRetryCount(0);

        event.setPayload(objectMapper.writeValueAsString(savedOrder));

        outBoxEventRepository.save(event);
        return savedOrder;
    }
}
