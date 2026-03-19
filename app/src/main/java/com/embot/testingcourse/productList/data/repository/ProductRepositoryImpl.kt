package com.embot.testingcourse.productList.data.repository

import com.embot.testingcourse.core.domain.coroutines.DispatcherProvider
import com.embot.testingcourse.productList.data.local.LocalDataSource
import com.embot.testingcourse.productList.data.mappers.toDomain
import com.embot.testingcourse.productList.data.mappers.toEntity
import com.embot.testingcourse.productList.data.remote.RemoteDataSource
import com.embot.testingcourse.productList.domain.model.Product
import com.embot.testingcourse.productList.domain.repository.ProductRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val dispatcher: DispatcherProvider,
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource
): ProductRepository {

    private val refreshScope: CoroutineScope = CoroutineScope(SupervisorJob() + dispatcher.io)
    private val refreshMutex: Mutex = Mutex()

    override fun getProduct(): Flow<List<Product>> {
        return localDataSource.getAllProducts()
            .map { entities -> entities.mapNotNull { it.toDomain() } }
            .onStart {
                refreshScope.launch {
                    if (!refreshMutex.tryLock()) return@launch
                    try {
                        refreshPRoduct()
                    } catch (e: Exception) {

                    } finally {
                        refreshMutex.unlock()
                    }
                }
            }.catch {
                // LOGs in case the process fail -> Do nothing
            }
    }

    override fun getProductById(id: String): Flow<Product?> {
        return localDataSource.getProductById(id)
            .map { entity -> entity?.toDomain() }
            .catch { e ->
                // Analytic trackError
            }
    }

    override suspend fun refreshPRoduct() {
        withContext(dispatcher.io) {
            val products = remoteDataSource.getProduts().getOrThrow()
            val productsEntity = products.products.map { it.toEntity() }
            localDataSource.saveProducts(productsEntity)
        }
    }
}