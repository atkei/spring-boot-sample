package com.example.orderservice.service

import com.example.orderservice.model.Order
import com.example.orderservice.repository.OrderRepository
import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service

@Service
class OrderService(private val orderRepository: OrderRepository) {
    suspend fun getOrderById(id: Long): Order? {
        return orderRepository.findById(id)
    }

    suspend fun getAllOrders(): Flow<Order> {
        return orderRepository.findAll()
    }

    suspend fun getOrdersByUserId(userId: Int): Flow<Order> {
        return orderRepository.findByUserId(userId)
    }

    suspend fun createOrder(order: Order): Order {
        return orderRepository.save(order)
    }

    suspend fun updateOrder(id: Long, order: Order): Order? {
        val existingOrder = orderRepository.findById(id)
        return if (existingOrder != null) {
            val updatedOrder = existingOrder.copy(name = order.name)
            orderRepository.save(updatedOrder)
        } else {
            null
        }
    }

    suspend fun deleteOrder(id: Long): Boolean {
        return if (orderRepository.existsById(id)) {
            orderRepository.deleteById(id)
            true
        } else {
            false
        }
    }
}