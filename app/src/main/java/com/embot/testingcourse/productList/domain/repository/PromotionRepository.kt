package com.embot.testingcourse.productList.domain.repository

import com.embot.testingcourse.productList.domain.model.Promotion
import kotlinx.coroutines.flow.Flow

interface PromotionRepository {
    fun getActivePromotions(): Flow<List<Promotion>>
    suspend fun refreshPromotions()
}