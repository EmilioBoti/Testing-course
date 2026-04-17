package com.embot.testingcourse.core.fakes

import com.embot.testingcourse.productList.domain.model.Promotion
import com.embot.testingcourse.productList.domain.repository.PromotionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakePromotionRepository: PromotionRepository {

    private val _activePromotion = MutableStateFlow<List<Promotion>>(emptyList())

    fun setPromotion(promotions: List<Promotion>) {
        _activePromotion.value = promotions
    }

    override fun getActivePromotions(): Flow<List<Promotion>> {
        return _activePromotion.asStateFlow()
    }

    override suspend fun refreshPromotions() {}

}
