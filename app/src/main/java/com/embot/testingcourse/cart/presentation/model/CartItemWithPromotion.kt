package com.embot.testingcourse.cart.presentation.model

import com.embot.testingcourse.cart.domain.model.CartItem
import com.embot.testingcourse.productList.domain.model.ProductWithPromotion

data class CartItemWithPromotion(
    val item: ProductWithPromotion,
    val cartItem: CartItem
)
