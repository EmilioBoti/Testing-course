package com.embot.testingcourse.cart.domain.usecase

import com.embot.testingcourse.cart.domain.repository.CartItemRepository
import com.embot.testingcourse.core.domain.model.AppError
import com.embot.testingcourse.productList.domain.repository.ProductRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class AddToCartUserCase @Inject constructor(
    private val cartItemRepository: CartItemRepository,
    private val productRepository: ProductRepository
) {

    suspend operator fun invoke(productId: String, quantity: Int = 1) {
        if (quantity <= 0) {
            throw AppError.Validation.QuantityMustBePositive
        }

        val product = productRepository.getProductById(productId).first() ?: throw AppError.NotFoundError

        val existingItem = cartItemRepository.getCartItemById(productId)
        val newQuality = (existingItem?.quantity ?: 0) + quantity

        if (newQuality > product.stock) throw AppError.Validation.InsufficientStock(product.stock)

        cartItemRepository.addToCart(productId, quantity)
    }

}