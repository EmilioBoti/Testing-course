package com.embot.testingcourse.productList.data.repository

import com.embot.testingcourse.productList.data.remote.RemoteDataSource
import com.embot.testingcourse.productList.domain.model.Product
import com.embot.testingcourse.productList.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    val remoteDataSource: RemoteDataSource
): ProductRepository {

    override fun getProduct(): Flow<List<Product>> {
        TODO("Not yet implemented")
    }

    override fun getProductById(id: String): Flow<Product?> {
        TODO("Not yet implemented")
    }

    override suspend fun refreshPRoduct() {
        remoteDataSource.getPromotions()
    }
}