package com.ecom.order_service.dto;

import jakarta.validation.constraints.DecimalMin;
import org.jspecify.annotations.NonNull;

import java.math.BigDecimal;

public class CreateOrderRequest {

    @NonNull
    private Long customerId;

    @NonNull
    @DecimalMin(value = "0.01")
    private BigDecimal amount;

    private String currency;

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
