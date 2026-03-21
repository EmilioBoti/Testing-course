package com.embot.testingcourse.cart.data.local.repository

import com.embot.testingcourse.cart.data.local.mapper.toDomain
import com.embot.testingcourse.cart.data.local.mapper.toEntity
import com.embot.testingcourse.cart.domain.model.CartItem
import com.embot.testingcourse.cart.domain.repository.CartItemRepository
import com.embot.testingcourse.core.domain.model.AppError
import com.embot.testingcourse.productList.data.local.LocalDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CartItemRepositoryImpl @Inject constructor(
    private val localDataSource: LocalDataSource
): CartItemRepository {

    override fun getCartItems(): Flow<List<CartItem>> {
        return localDataSource.getAllCartItems()
            .map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun getCartItemById(productId: String): CartItem? {
        return localDataSource.getCartItemById(productId)?.toDomain()
    }

    override suspend fun addToCart(productId: String, quantity: Int) {
        val existingItem = localDataSource.getCartItemById(productId)

        if (existingItem != null) {
            val newQuality = existingItem.quantity + quantity
            localDataSource.updateCartItem(existingItem.copy(quantity = newQuality))
        } else {
            localDataSource.insertCartItem(CartItem(productId, quantity).toEntity())
        }

    }

    override suspend fun removeFromCart(productId: String) {
        val item = localDataSource.getCartItemById(productId) ?: throw AppError.NotFoundError
        localDataSource.deleteCartItem(item)
    }

    override suspend fun updateQuantity(productId: String, quantity: Int) {
        val item = localDataSource.getCartItemById(productId) ?: throw AppError.NotFoundError
        localDataSource.updateCartItem(
            item.copy(
                quantity = quantity
            )
        )
    }

    override suspend fun clearCart() {
        localDataSource.clearCart()
    }


}