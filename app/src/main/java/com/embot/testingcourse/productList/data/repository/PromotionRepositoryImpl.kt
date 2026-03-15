package com.embot.testingcourse.productList.data.repository

import com.embot.testingcourse.core.domain.coroutines.DispatcherProvider
import com.embot.testingcourse.productList.data.local.LocalDataSource
import com.embot.testingcourse.productList.data.local.db.entity.PromotionEntity
import com.embot.testingcourse.productList.data.mappers.toDomain
import com.embot.testingcourse.productList.data.mappers.toEntity
import com.embot.testingcourse.productList.data.remote.RemoteDataSource
import com.embot.testingcourse.productList.domain.model.Promotion
import com.embot.testingcourse.productList.domain.repository.PromotionRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import javax.inject.Inject

class PromotionRepositoryImpl @Inject constructor(
    private val dispatcher: DispatcherProvider,
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource,
    private val json: Json
): PromotionRepository {

    private val refreshScope: CoroutineScope = CoroutineScope(SupervisorJob() + dispatcher.io)
    private val refreshMutex: Mutex = Mutex()


    override fun getActivePromotions(): Flow<List<Promotion>> {
        return localDataSource.getAllPromotions()
            .map { entities -> entities.mapNotNull { it.toDomain(json) } }
            .onStart {
                refreshScope.launch {
                    if (!refreshMutex.tryLock()) return@launch
                    try {
                        refreshPromotions()
                    } catch (e: Exception) {

                    } finally {
                        refreshMutex.unlock()
                    }
                }
            }.catch {
                // LOGs in case the process fail -> Do nothing
            }
    }

    override suspend fun refreshPromotions() {
        withContext(dispatcher.io) {
            val promotion = remoteDataSource.getPromotions().getOrThrow()
            val promotionEntites: List<PromotionEntity> = promotion.mapNotNull { it.toEntity(json) }
            localDataSource.savePromotions(promotionEntites)
        }
    }
}