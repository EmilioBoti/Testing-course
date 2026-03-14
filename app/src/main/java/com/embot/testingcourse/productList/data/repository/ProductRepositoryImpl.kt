package com.embot.testingcourse.productList.data.repository

import com.embot.testingcourse.core.domain.coroutines.DispatcherProvider
import com.embot.testingcourse.productList.data.local.LocalDataSource
import com.embot.testingcourse.productList.data.remote.RemoteDataSource
import com.embot.testingcourse.productList.domain.model.Product
import com.embot.testingcourse.productList.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    val dispatcher: DispatcherProvider,
    val localDataSource: LocalDataSource,
    val remoteDataSource: RemoteDataSource
): ProductRepository {

    override fun getProduct(): Flow<List<Product>> {
        TODO("Not yet implemented")
    }

    override fun getProductById(id: String): Flow<Product?> {
        TODO("Not yet implemented")
    }

    override suspend fun refreshPRoduct() {
        withContext(dispatcher.io) {
            remoteDataSource.getPromotions()
        }
    }
}