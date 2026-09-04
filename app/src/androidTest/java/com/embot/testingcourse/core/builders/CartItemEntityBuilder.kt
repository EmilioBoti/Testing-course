package com.embot.testingcourse.core.builders

import com.embot.testingcourse.cart.data.local.db.entity.CartItemEntity

class CartItemEntityBuilder {

    private var productId: String = "product-1"
    private var quantity: Int = 1


    fun withProductId(id: String) = this.apply { this.productId = id }
    fun withQuantity(quantity: Int) = this.apply { this.quantity = quantity }

    fun build() = CartItemEntity(
        productId = productId,
        quantity = quantity
    )

}

fun cartItemEntity(block: CartItemEntityBuilder.() -> Unit = {}) = CartItemEntityBuilder().apply(block).build()
