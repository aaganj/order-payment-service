package com.ecom.order_service.controller;

import com.ecom.order_service.dto.CreateOrderRequest;
import com.ecom.order_service.entity.Order;
import com.ecom.order_service.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Order createOder(@Valid @RequestBody
                            CreateOrderRequest request){
        return orderService.createOrder(request);
    }
}
