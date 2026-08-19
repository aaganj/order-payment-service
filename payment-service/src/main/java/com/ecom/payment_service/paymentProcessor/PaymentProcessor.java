package com.ecom.payment_service.paymentProcessor;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public interface PaymentProcessor {

    boolean processPayment(BigDecimal amount,String currency);
}
