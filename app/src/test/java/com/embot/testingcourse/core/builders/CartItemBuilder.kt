package com.embot.testingcourse.core.builders

import com.embot.testingcourse.cart.domain.model.CartItem

class CartItemBuilder {

    private var productId: String = "product-1"
    private var quantity: Int = 1


    fun withId(id: String) = this.apply { this.productId = id }
    fun withQuantity(quantity: Int) = this.apply { this.quantity = quantity }

    fun build() = CartItem(
        productId = productId,
        quantity = quantity
    )

}

fun cartItem(block: CartItemBuilder.() -> Unit = {}) = CartItemBuilder().apply(block).build()
