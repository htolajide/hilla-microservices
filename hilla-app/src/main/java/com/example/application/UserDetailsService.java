package com.example.application;

import com.vaadin.flow.server.auth.AnonymousAllowed;
import dev.hilla.Endpoint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Endpoint
@AnonymousAllowed
public class UserDetailsService {

    public record User(Long id, String name, String email) {
    }

    public record Order(Long id, Long userId, String product, Double price) {
    }

    private final WebClient.Builder webClientBuilder;

    @Value("${user-service.url}")
    private String userServiceUrl;

    @Value("${order-service.url}")
    private String orderServiceUrl;

    public UserDetailsService(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    public List<User> getUsers() {
        WebClient userClient = webClientBuilder.baseUrl(userServiceUrl).build();
        var users = userClient.get()
                .uri("/users")
                .retrieve()
                .bodyToFlux(User.class)
                .collectList()
                .block();

        return users;
    }

    public List<Order> getOrders(String userId) {
        WebClient orderClient = webClientBuilder.baseUrl(orderServiceUrl).build();
        var orders = orderClient.get()
                .uri("/orders/user/" + userId)
                .retrieve()
                .bodyToFlux(Order.class)
                .collectList()
                .block();

        return orders;
    }
}
