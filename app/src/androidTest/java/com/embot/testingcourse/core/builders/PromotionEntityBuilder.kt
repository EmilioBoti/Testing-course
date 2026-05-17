package com.embot.testingcourse.core.builders

import com.embot.testingcourse.productList.data.local.db.entity.PromotionEntity

class PromotionEntityBuilder {

    private var id: String = "Promotion-1"
    private var type: String = "PERCENT"
    private var productIds: String = """["Product-1"]""""
    private var value: Double =  10.0
    private var buyQuantity: Int? = null
    private var startAtEpoch: Long = 1700000000L
    private var endAtEpoch: Long = 1800000000L
    private var percent: Int? = null
    private var buyX: Int? = null
    private var payY: Int? = null


    fun withId(id: String) = this.apply { this.id = id }
    fun withType(type: String) = this.apply { this.type = type }
    fun withProductIds(productIds: String) = this.apply { this.productIds = productIds }
    fun withValue(value: Double) = this.apply { this.value = value }
    fun withBuyQuantity(buyQuantity: Int?) = this.apply { this.buyQuantity = buyQuantity }
    fun withStartTime(startTime: Long) = this.apply { this.startAtEpoch = startTime }
    fun withEndTime(endTime: Long) = this.apply { this.endAtEpoch = endTime }
    fun withPercent(percent: Int?) = this.apply { this.percent = percent }
    fun withBuyX(buyX: Int?) = this.apply { this.buyX = buyX }
    fun withPayY(payY: Int?) = this.apply { this.payY = payY }


    fun build(): PromotionEntity {
        return PromotionEntity(
            id = this.id,
            type = this.type,
            productIds = this.productIds,
            percent = null,
            buyX = null,
            payY = null,
            startAtEpoch = this.startAtEpoch,
            endAtEpoch = this.endAtEpoch,
        )
    }

}

fun promotionEntity(block: PromotionEntityBuilder.() -> Unit = {}) = PromotionEntityBuilder()
    .apply(block)
    .build()