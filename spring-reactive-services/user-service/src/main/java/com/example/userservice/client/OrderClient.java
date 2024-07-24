package com.example.userservice.client;

import com.example.userservice.model.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Component
public class OrderClient {
    private WebClient client = WebClient.create("http://localhost:8081");

    public Flux<Order> getOrdersByUserId(Long userId) {
        return client.get()
                .uri("/orders?userId=" + userId)
                .retrieve()
                .bodyToFlux(Order.class);
    }

}
