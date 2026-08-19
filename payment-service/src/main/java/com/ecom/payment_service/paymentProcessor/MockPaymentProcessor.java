package com.ecom.payment_service.paymentProcessor;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component("mockPaymentProcessor")
@Primary
public class MockPaymentProcessor implements PaymentProcessor{
    private boolean nextResult = true;
    @Override
    public boolean processPayment(BigDecimal amount, String currency) {

        boolean result = nextResult;
        nextResult = !nextResult;

        return result;
    }
}
