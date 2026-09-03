package com.embot.testingcourse.core.mothers.uistate

import com.embot.testingcourse.cart.domain.model.CartSummary
import com.embot.testingcourse.cart.presentation.CartUiState

object CartUiStateMother {

    fun success(

    )= CartUiState.Success(
        summary = CartSummary(
            subTotal = 30.0,
            discountTotal = 10.0,
            finalTotal = 40.0
        ),
        cartItems = listOf(),
        isLoading = false
    )

}