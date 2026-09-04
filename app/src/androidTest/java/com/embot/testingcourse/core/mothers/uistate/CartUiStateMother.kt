package com.embot.testingcourse.core.mothers.uistate

import com.embot.testingcourse.cart.domain.model.CartItem
import com.embot.testingcourse.cart.domain.model.CartSummary
import com.embot.testingcourse.cart.presentation.CartUiState
import com.embot.testingcourse.cart.presentation.model.CartItemWithPromotion
import com.embot.testingcourse.core.mothers.ProductMother
import com.embot.testingcourse.core.mothers.PromotionMother
import com.embot.testingcourse.productList.domain.model.Product
import com.embot.testingcourse.productList.domain.model.ProductPromotion
import com.embot.testingcourse.productList.domain.model.ProductWithPromotion

object CartUiStateMother {

    fun success(
        summary: CartSummary = CartSummary(
            subTotal = 30.0,
            discountTotal = 10.0,
            finalTotal = 40.0
        ),
        cartItems: List<CartItemWithPromotion> = listOf(
            cartItemWithPromotion(
                product = ProductMother.bread(),
                quantity = 2
            ),
            cartItemWithPromotion(
                product = ProductMother.coffee(),
                quantity = 1,
                promotion = PromotionMother.percent()
            )
        ),
        isLoading: Boolean = false
    )= CartUiState.Success(
        summary = summary,
        cartItems = cartItems,
        isLoading = isLoading
    )

    fun cartItemWithPromotion(
        product: Product,
        quantity: Int,
        promotion: ProductPromotion? = null
    ): CartItemWithPromotion {
        return CartItemWithPromotion(
            item = ProductWithPromotion(product = product, promotion = promotion),
            cartItem = CartItem(
                productId = product.id,
                quantity = quantity
            )
        )
    }

}