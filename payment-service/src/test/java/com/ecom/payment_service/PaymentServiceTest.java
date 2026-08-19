package com.ecom.payment_service;

import com.ecom.payment_service.entity.Payment;
import com.ecom.payment_service.event.OrderCreatedEvent;
import com.ecom.payment_service.repository.PaymentRepository;
import com.ecom.payment_service.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Optional;

public class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void shouldCreatePaymentWhenPaymentDoesNotExist() {

        OrderCreatedEvent event = new OrderCreatedEvent();
        event.setOrderId(100L);
        event.setCustomerId(101L);
        event.setAmount(new BigDecimal("100.00"));
        event.setCurrency("SGD");

        when(paymentRepository.findByOrderId(100L))
                .thenReturn(Optional.empty());

        paymentService.processOrderCreated(event);

        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    void shouldNotCreateDuplicatePayment() {

        OrderCreatedEvent event = new OrderCreatedEvent();
        event.setOrderId(100L);
        event.setCustomerId(101L);
        event.setAmount(new BigDecimal("100.00"));
        event.setCurrency("SGD");

        Payment existingPayment = new Payment();
        existingPayment.setOrderId(100L);

        when(paymentRepository.findByOrderId(100L))
                .thenReturn(Optional.of(existingPayment));

        paymentService.processOrderCreated(event);

        verify(paymentRepository, never())
                .save(any(Payment.class));
    }
}
