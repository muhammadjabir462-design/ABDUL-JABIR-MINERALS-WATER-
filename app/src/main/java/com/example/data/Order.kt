package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class Order(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val address: String,
    val quantity: Int,
    val unitPrice: Double = 50.0,
    val status: String = "Pending", // Pending, Delivered, Cancelled
    val timestamp: Long = System.currentTimeMillis()
) {
    val totalPrice: Double
        get() = quantity * unitPrice
}
