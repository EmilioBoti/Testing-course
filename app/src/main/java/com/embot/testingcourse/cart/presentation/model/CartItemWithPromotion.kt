package com.embot.testingcourse.cart.presentation.model

import com.embot.testingcourse.cart.domain.model.CartItem
import com.embot.testingcourse.productList.domain.model.Product

data class CartItemWithPromotion(
    val product: Product,
    val cartItem: CartItem
)
