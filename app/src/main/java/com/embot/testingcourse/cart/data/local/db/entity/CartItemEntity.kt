package com.embot.testingcourse.cart.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "cart_itmes")
data class CartItemEntity(
    @PrimaryKey
    val productId: String,
    val quantity: Int
)