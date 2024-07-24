package com.example.orderservice.repository

import com.example.orderservice.model.Order;
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository;

interface OrderRepository : CoroutineCrudRepository<Order, Long> {
    suspend fun findByUserId(userId: Int): Flow<Order>
}
