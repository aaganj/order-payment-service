package com.ecom.payment_service.service;

import com.ecom.payment_service.entity.Payment;
import com.ecom.payment_service.entity.PaymentStatus;
import com.ecom.payment_service.event.OrderCreatedEvent;
import com.ecom.payment_service.paymentProcessor.PaymentProcessor;
import com.ecom.payment_service.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private PaymentProcessor paymentProcessor;

    public PaymentService(PaymentRepository paymentRepository,
                          @Qualifier("realTimePaymentProcessor")PaymentProcessor paymentProcessor) {

        this.paymentRepository = paymentRepository;
        this.paymentProcessor = paymentProcessor;
    }

    @Transactional
    public void processOrderCreated(OrderCreatedEvent orderCreatedEvent) {

        if (paymentRepository.findByOrderId(orderCreatedEvent.getOrderId()).isPresent()) {
            System.out.println(
                    "Payment already exists for order: "
                            + orderCreatedEvent.getOrderId()
            );
            return;
        }

        Payment payment = new Payment();
        payment.setOrderId(orderCreatedEvent.getOrderId());
        payment.setCustomerId(orderCreatedEvent.getCustomerId());
        payment.setAmount(orderCreatedEvent.getAmount());
        payment.setCurrency(orderCreatedEvent.getCurrency());

        payment.setStatus(PaymentStatus.PENDING);

        LocalDateTime now = LocalDateTime.now();
        payment.setCreatedAt(now);
        payment.setUpdatedAt(now);
        paymentRepository.save(payment);


        boolean success = paymentProcessor
                .processPayment(orderCreatedEvent.getAmount(), orderCreatedEvent.getCurrency());

        if (success) {
            payment.setStatus(PaymentStatus.COMPLETED);
            paymentRepository.save(payment);

        } else {
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);

        }

    }
}
