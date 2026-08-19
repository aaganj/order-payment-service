package com.ecom.payment_service.paymentProcessor;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component("realTimePaymentProcessor")
public class RealTimePaymentProcessor implements PaymentProcessor{
    private boolean nextResult = true;
    @Override
    public boolean processPayment(BigDecimal amount, String currency) {

        boolean result = nextResult;
        nextResult = !nextResult;

        return result;
    }
}
