package com.ecom.order_service.exception;

public class OrderNotFoundException extends RuntimeException{
    String message;

    public OrderNotFoundException(String message) {
        super(message);
    }
}
