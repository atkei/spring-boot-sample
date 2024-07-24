package com.example.userservice.repository;

import com.example.userservice.model.User;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;


public interface UserRepository extends ReactiveCrudRepository<User, Long> {
}
