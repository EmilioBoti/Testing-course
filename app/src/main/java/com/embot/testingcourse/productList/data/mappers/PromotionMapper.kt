package com.embot.testingcourse.productList.data.mappers

import com.embot.testingcourse.productList.data.local.db.entity.PromotionEntity
import com.embot.testingcourse.productList.data.remote.response.PromotionResponse
import com.embot.testingcourse.productList.domain.model.Promotion
import com.embot.testingcourse.productList.domain.model.PromotionType
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import java.time.Instant

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

fun PromotionEntity.toDomain(json: Json): Promotion? {
    val decodedProductIds = runCatching { json.decodeFromString(
        deserializer = ListSerializer(String.serializer()),
        string = productIds
    )}.getOrNull()

    val finalType = runCatching {
        PromotionType.valueOf(
            type.trim().uppercase()
        )
    }.getOrNull()

    if (finalType == null || decodedProductIds == null) return null

    val finalofferValue = when(finalType) {
        PromotionType.PERCENT -> percent
        PromotionType.BUY_X_PAY_Y -> payX
    }?.toDouble()

    finalofferValue ?: return null

    return Promotion(
        id = this.id,
        type = finalType,
        productIds = decodedProductIds,
        value = finalofferValue,
        startTime = Instant.ofEpochSecond(startAtEpoch),
        endTime = Instant.ofEpochSecond(endAtEpoch)
    )
}
