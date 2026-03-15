package com.embot.testingcourse.productList.data.mappers

import com.embot.testingcourse.productList.data.local.db.entity.PromotionEntity
import com.embot.testingcourse.productList.data.remote.response.PromotionResponse
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

fun PromotionResponse.toEntity(json: Json): PromotionEntity? {

    if (startAtEpoch === null || endAtEpoch ===  null) return null

    val productIds = listOf(this.productId)
    val productIdsJson = json.encodeToString(
        serializer = ListSerializer(String.serializer()),
        value = productIds
    )
    return PromotionEntity(
        id = this.id,
        productIds = productIdsJson,
        type = this.type,
        percent = this.percent,
        buyX = this.buyX,
        payX = this.payX,
        startAtEpoch = 0,
        endAtEpoch = 0
    )
}
