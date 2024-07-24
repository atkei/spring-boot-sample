package com.example.userservice.service;

import com.example.userservice.client.OrderClient;
import com.example.userservice.model.Order;
import com.example.userservice.model.User;
import com.example.userservice.model.UserOrders;
import com.example.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderClient orderClient;

    public Mono<User> createUser(User user) {
        return userRepository.save(user);
    }

    public Flux<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Mono<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Flux<UserOrders> getUserWithOrders(Long id) {
        return userRepository.findById(id)
                .flatMapMany(user -> {
                    Flux<Order> orders = orderClient.getOrdersByUserId(id);
                    return orders.collectList().map(orderList -> {
                        UserOrders userOrders = new UserOrders();
                        userOrders.setId(user.getId());
                        userOrders.setName(user.getName());
                        userOrders.setOrders(orderList);
                        return userOrders;
                    });
                });
    }

    public Mono<User> updateUser(Long id, User user) {
        return userRepository.findById(id)
                .flatMap(existingUser -> {
                    existingUser.setName(user.getName());
                    return userRepository.save(existingUser);
                });
    }

    public Mono<Void> deleteUser(Long id) {
        return userRepository.deleteById(id);
    }
}
