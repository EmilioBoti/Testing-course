package com.embot.testingcourse.productList.data.mappers

import com.embot.testingcourse.productList.data.local.db.entity.ProductEntity
import com.embot.testingcourse.productList.data.remote.response.ProductResponse
import com.embot.testingcourse.productList.domain.model.Product

fun ProductResponse.toEntity(): ProductEntity {
    val finalPrice = this.priceCents?.div(100.0) ?: 0.0
    return ProductEntity(
        id = this.id,
        name = this.name,
        description = this.description,
        price = finalPrice,
        category = this.category,
        stock = this.stock,
        imageUrl = this.imageUrl
    )
}

fun ProductEntity.toDomain(): Product? {
    if (this.category.isNullOrEmpty()) return null
    return Product(
        id = this.id,
        name = this.name,
        description = this.description.orEmpty(),
        price = this.price,
        category = this.category,
        stock = this.stock ?: 0,
        imageUrl = this.imageUrl
    )
}