package com.example.data

import kotlinx.coroutines.flow.Flow

class OrderRepository(private val orderDao: OrderDao) {
    val allOrders: Flow<List<Order>> = orderDao.getAllOrders()

    suspend fun insert(order: Order) {
        orderDao.insertOrder(order)
    }

    suspend fun updateStatus(id: Int, status: String) {
        orderDao.updateOrderStatus(id, status)
    }

    suspend fun delete(order: Order) {
        orderDao.deleteOrder(order)
    }
}
