package com.embot.testingcourse.core.fakes

import com.embot.testingcourse.cart.domain.model.CartItem
import com.embot.testingcourse.cart.domain.repository.CartItemRepository
import com.embot.testingcourse.core.domain.model.AppError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FakeCartItemRepository: CartItemRepository {

    private val _cartItems: MutableStateFlow<List<CartItem>> = MutableStateFlow(emptyList())

    override fun getCartItems(): Flow<List<CartItem>> = _cartItems.asStateFlow()

    override suspend fun getCartItemById(productId: String): CartItem? {
        return _cartItems.value.find { item ->  item.productId == productId }
    }

    override suspend fun addToCart(productId: String, quantity: Int) {
        val currentItems = _cartItems.value.toMutableList()
        val existingIndex = currentItems.indexOfFirst { it.productId == productId }

        if (existingIndex >= 0) {
            val item = currentItems[existingIndex]
            currentItems[existingIndex] = item.copy(quantity = item.quantity + quantity)
        } else {
            currentItems.add(CartItem(productId, quantity))
        }
        _cartItems.update { currentItems }
    }

    override suspend fun removeFromCart(productId: String) {
        val currentItems = _cartItems.value.toMutableList()
        val existingIndex = currentItems.indexOfFirst { it.productId == productId }

        if (existingIndex >= 0) {
            currentItems.removeAt(existingIndex)
            _cartItems.update { currentItems }
        } else {
            throw AppError.NotFoundError
        }
    }

    override suspend fun updateQuantity(productId: String, quantity: Int) {
        val currentItems = _cartItems.value.toMutableList()
        val existingIndex = currentItems.indexOfFirst { it.productId == productId }

        if (existingIndex >= 0) {
            val item = currentItems[existingIndex]
            currentItems[existingIndex] = item.copy(quantity = item.quantity + quantity)
            _cartItems.update { currentItems }
        } else {
            throw AppError.NotFoundError
        }
    }

    override suspend fun clearCart() {
        _cartItems.value = emptyList()
    }
}