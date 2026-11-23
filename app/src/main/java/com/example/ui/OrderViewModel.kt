package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.Order
import com.example.data.OrderRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class OrderViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: OrderRepository

    val allOrders: StateFlow<List<Order>>

    init {
        val orderDao = AppDatabase.getDatabase(application).orderDao()
        repository = OrderRepository(orderDao)
        allOrders = repository.allOrders.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    private val _messageFlow = MutableSharedFlow<String>()
    val messageFlow: SharedFlow<String> = _messageFlow.asSharedFlow()

    // Form inputs state
    var clientName = MutableStateFlow("")
    var clientAddress = MutableStateFlow("")
    var quantityString = MutableStateFlow("1")

    // Filter status for order manager panel
    private val _filterStatus = MutableStateFlow("All")
    val filterStatus: StateFlow<String> = _filterStatus.asStateFlow()

    fun setFilterStatus(status: String) {
        _filterStatus.value = status
    }

    fun placeOrder() {
        val name = clientName.value.trim()
        val address = clientAddress.value.trim()
        val qty = quantityString.value.toIntOrNull() ?: 0

        if (name.isEmpty()) {
            viewModelScope.launch { _messageFlow.emit("Please enter your name.") }
            return
        }
        if (address.isEmpty()) {
            viewModelScope.launch { _messageFlow.emit("Please enter your delivery address.") }
            return
        }
        if (qty <= 0) {
            viewModelScope.launch { _messageFlow.emit("Please enter a valid quantity (1 or more).") }
            return
        }

        viewModelScope.launch {
            val order = Order(
                name = name,
                address = address,
                quantity = qty,
                status = "Pending"
            )
            repository.insert(order)
            _messageFlow.emit("Shukriya! Your order for $qty bottle(s) has been placed successfully.")
            
            // Clear fields on success
            clientName.value = ""
            clientAddress.value = ""
            quantityString.value = "1"
        }
    }

    fun updateStatus(id: Int, status: String) {
        viewModelScope.launch {
            repository.updateStatus(id, status)
            _messageFlow.emit("Order status updated to $status.")
        }
    }

    fun deleteOrder(order: Order) {
        viewModelScope.launch {
            repository.delete(order)
            _messageFlow.emit("Order deleted successfully.")
        }
    }
}
