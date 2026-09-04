package com.embot.testingcourse.core.builders

import com.embot.testingcourse.productList.domain.model.Promotion
import com.embot.testingcourse.productList.domain.model.PromotionType
import java.time.Instant

class PromotionBuilder {

    private var id: String = "Promotion-1"
    private var type: PromotionType = PromotionType.PERCENT
    private var productIds: List<String> = listOf("Product-1")
    private var value: Double =  10.0
    private var buyQuantity: Int? = null
    private var startTime: Instant = Instant.now().minusSeconds(3600)
    private var endTime: Instant = Instant.now().plusSeconds(3600)


    fun withId(id: String) = this.apply { this.id = id }
    fun withType(type: PromotionType) = this.apply { this.type = type }
    fun withProductIds(productIds: List<String>) = this.apply { this.productIds = productIds }
    fun withValue(value: Double) = this.apply { this.value = value }
    fun withBuyQuantity(buyQuantity: Int?) = this.apply { this.buyQuantity = buyQuantity }
    fun withStartTime(startTime: Instant) = this.apply { this.startTime = startTime }
    fun withEndTime(endTime: Instant) = this.apply { this.endTime = endTime }


    fun build(): Promotion {
        return Promotion(
            id = this.id,
            type = this.type,
            productIds = this.productIds,
            value = this.value,
            buyQuantity = this.buyQuantity,
            startTime = this.startTime,
            endTime = this.endTime
        )
    }

}

fun promotion(block: PromotionBuilder.() -> Unit = {}) = PromotionBuilder()
    .apply(block)
    .build()






//enum class PromotionType {
//    PERCENT,
//    BUY_X_PAY_Y
//}
//
//data class Promotion(
//    val id: String,
//    val type: PromotionType,
//    val productIds: List<String>,
//    val value: Double,
//    val buyQuantity: Int? = null,
//    val startTime: Instant,
//    val endTime: Instant
//)