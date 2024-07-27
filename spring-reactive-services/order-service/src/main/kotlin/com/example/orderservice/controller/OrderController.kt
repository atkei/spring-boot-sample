package com.example.orderservice.controller

import com.example.orderservice.model.Order
import com.example.orderservice.service.OrderService
import kotlinx.coroutines.flow.Flow
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/orders")
class OrderController(private val orderService: OrderService) {

    @GetMapping("/{id}")
    suspend fun getOrderById(@PathVariable id: Long): Order? {
        return orderService.getOrderById(id)
    }

    @GetMapping
    suspend fun getAllOrders(): Flow<Order> {
        return orderService.getAllOrders()
    }

    @GetMapping("/users/{userId}")
    suspend fun getOrdersByUserId(@PathVariable userId: Int): Flow<Order> {
        return orderService.getOrdersByUserId(userId)
    }

    @PostMapping
    suspend fun createOrder(@RequestBody order: Order): Order {
        return orderService.createOrder(order)
    }

    @PutMapping("/{id}")
    suspend fun updateOrder(@PathVariable id: Long, @RequestBody order: Order): Order? {
        return orderService.updateOrder(id, order)
    }

    @DeleteMapping("/{id}")
    suspend fun deleteOrder(@PathVariable id: Long): Boolean {
        return orderService.deleteOrder(id)
    }
}