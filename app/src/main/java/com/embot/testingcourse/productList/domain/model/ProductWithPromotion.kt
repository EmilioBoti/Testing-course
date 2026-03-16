package com.embot.testingcourse.productList.domain.model

data class ProductWithPromotion(
    val product: Product,
    val promotion: ProductPromotion?
)

sealed interface ProductPromotion {
    data class Percent(
        val percent: Double,
        val discountPrice: Double,
    ): ProductPromotion
    data class BuyXPayY(
        val buy: Int,
        val pay: Int,
        val label: String
    ): ProductPromotion
}
