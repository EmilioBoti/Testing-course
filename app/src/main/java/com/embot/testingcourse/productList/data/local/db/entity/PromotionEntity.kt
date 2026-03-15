package com.embot.testingcourse.productList.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "promotions")
data class PromotionEntity(
    @PrimaryKey
    val id: String,
    val productIds: String,
    val type: String,
    val percent: Int? = null,
    val buyX: Int? = null,
    val payX: Int? = null,
    val startAtEpoch: Long,
    val endAtEpoch: Long
)
