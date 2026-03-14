package com.embot.testingcourse.productList.domain.repository

import com.embot.testingcourse.productList.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun getProduct(): Flow<List<Product>>
    fun getProductById(id: String): Flow<Product?>
    suspend fun refreshPRoduct()
}