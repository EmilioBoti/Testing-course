package com.embot.testingcourse.cart.domain.usecase

import com.embot.testingcourse.cart.domain.repository.CartItemRepository
import com.embot.testingcourse.core.domain.model.AppError
import com.embot.testingcourse.productList.domain.repository.ProductRepository
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class UpdateCartItemUseCase @Inject constructor(
    private val cartItemRepository: CartItemRepository,
    private val productRepository: ProductRepository
) {

    suspend operator fun invoke(productId: String, quantity: Int) {
        if (quantity < 0) {
            throw AppError.Validation.QuantityMustBePositive
        }

        if (quantity == 0) {
            cartItemRepository.removeFromCart(productId)
            return
        }

        val product = productRepository.getProductById(productId).firstOrNull()
            ?: throw AppError.NotFoundError

        if (quantity > product.stock) {
            throw AppError.Validation.InsufficientStock(product.stock)
        }

        cartItemRepository.updateQuantity(productId, quantity)

    }

}