package com.embot.testingcourse.cart.presentation

import com.embot.testingcourse.cart.domain.model.CartSummary
import com.embot.testingcourse.cart.presentation.model.CartItemWithPromotion

sealed class CartUiState {
    data class Success(
        val summary: CartSummary? = null,
        val cartItems: List<CartItemWithPromotion>,
        val isLoading: Boolean
    ): CartUiState()

    data class Error(val message: String): CartUiState()
    data object Loading: CartUiState()
}
