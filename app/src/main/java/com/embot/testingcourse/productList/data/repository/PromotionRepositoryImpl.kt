package com.embot.testingcourse.productList.data.repository

import com.embot.testingcourse.core.domain.coroutines.DispatcherProvider
import com.embot.testingcourse.productList.data.local.LocalDataSource
import com.embot.testingcourse.productList.data.local.db.entity.PromotionEntity
import com.embot.testingcourse.productList.data.mappers.toEntity
import com.embot.testingcourse.productList.data.remote.RemoteDataSource
import com.embot.testingcourse.productList.domain.model.Promotion
import com.embot.testingcourse.productList.domain.repository.PromotionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import javax.inject.Inject

class PromotionRepositoryImpl @Inject constructor(
    private val dispatcher: DispatcherProvider,
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource,
    private val json: Json
): PromotionRepository {

    override fun getActivePromotions(): Flow<List<Promotion>> {
        TODO("Not yet implemented")
    }

    override suspend fun refreshPromotions() {
        withContext(dispatcher.io) {
            val promotion = remoteDataSource.getPromotions().getOrThrow()
            val promotionEntites: List<PromotionEntity> = promotion.mapNotNull { it.toEntity(json) }
            localDataSource.savePromotions(promotionEntites)
        }
    }
}