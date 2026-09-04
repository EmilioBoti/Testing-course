package com.embot.testingcourse.core.builders

import com.embot.testingcourse.productList.data.local.db.entity.ProductEntity

class ProductEntityBuilder {

    private var id: String = "Product-1"
    private var name: String = "Product-1"
    private var description: String = "Product-1"
    private var price: Double = 10.0
    private var category: String = "Test category"
    private var stock: Int = 10
    private var imageUrl: String? = null


    fun withId(id: String) = apply { this.id = id }
    fun withName(name: String) = apply { this.name = name }
    fun withDescription(description: String) = apply { this.description = description }
    fun withPrice(price: Double) = apply { this.price = price }
    fun withCategory(category: String) = apply { this.category = category }
    fun withStock(stock: Int) = apply { this.stock = stock }
    fun withImageUrl(imageUrl: String) = apply { this.imageUrl = imageUrl }

    fun build() = ProductEntity(
        id = this.id,
        name = this.name,
        description = this.description,
        price = this.price,
        category = this.category,
        stock = this.stock,
        imageUrl = this.imageUrl
    )

}

fun productEntity(block: ProductEntityBuilder.() -> Unit = {}) = ProductEntityBuilder()
    .apply(block)
    .build()