package com.ecom.payment_service.listener;

import com.ecom.payment_service.event.OrderCreatedEvent;
import com.ecom.payment_service.service.PaymentService;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
public class OrderCreatedListener {

    private final ObjectMapper objectMapper;
    private final PaymentService paymentService;

    public OrderCreatedListener(ObjectMapper objectMapper, PaymentService paymentService) {
        this.objectMapper = objectMapper;
        this.paymentService = paymentService;
    }

    @JmsListener(destination = "order.created")
    public void consume(String message){
        try {
            OrderCreatedEvent event =
                    objectMapper.readValue(message,OrderCreatedEvent.class);
            paymentService.processOrderCreated(event);

        }catch (Exception ex){
            System.err.println(
                    "Failed to process OrderCreated: "
                            + ex.getMessage()
            );

            throw new RuntimeException(ex);
        }
    }
}
