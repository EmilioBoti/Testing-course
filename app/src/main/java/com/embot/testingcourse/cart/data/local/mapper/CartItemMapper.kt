package com.embot.testingcourse.cart.data.local.mapper

import com.embot.testingcourse.cart.data.local.db.entity.CartItemEntity
import com.embot.testingcourse.cart.domain.model.CartItem


fun CartItemEntity.toDomain(): CartItem {
    return CartItem(
        productId = this.productId,
        quantity = this.quantity
    )
}

fun CartItem.toEntity(): CartItemEntity {
    return CartItemEntity(
        productId = this.productId,
        quantity = this.quantity
    )
}