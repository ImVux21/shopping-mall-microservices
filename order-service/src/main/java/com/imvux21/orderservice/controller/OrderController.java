package com.imvux21.orderservice.controller;

import com.imvux21.orderservice.dto.request.CreateOrderRequest;
import com.imvux21.orderservice.service.OrderService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @CircuitBreaker(name = "inventory", fallbackMethod = "createOrderFallback")
    @TimeLimiter(name = "inventory")
    @Retry(name = "inventory")
    public CompletableFuture<String> createOrder(@RequestBody CreateOrderRequest createOrderRequest) {
        return CompletableFuture.supplyAsync(() -> orderService.createOrder(createOrderRequest));
    }

    public CompletableFuture<String> createOrderFallback(CreateOrderRequest createOrderRequest, RuntimeException e) {
        return CompletableFuture.supplyAsync(() -> "Opps! Something went wrong while creating the order. Please try again later.");
    }
}
