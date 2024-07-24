package com.example.orderservice.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("orders")
data class Order(
    @Id val id: Int? = null,
    val name: String,
    val userId: Int
)
