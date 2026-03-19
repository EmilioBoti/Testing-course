package com.embot.testingcourse.productList.data.local

import com.embot.testingcourse.productList.data.local.db.dao.ProductDao
import com.embot.testingcourse.productList.data.local.db.dao.PromotionDao
import com.embot.testingcourse.productList.data.local.db.entity.ProductEntity
import com.embot.testingcourse.productList.data.local.db.entity.PromotionEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val productDao: ProductDao,
    private val promotionDao: PromotionDao
) {

    fun getAllProducts(): Flow<List<ProductEntity>> = productDao.getAllProducts()
    fun getProductById(productId: String): Flow<ProductEntity?> = productDao.getProductById(productId)
    fun getAllPromotions(): Flow<List<PromotionEntity>> = promotionDao.getAllPromotions()

    suspend fun saveProducts(productsEntity: List<ProductEntity>) {
        productDao.replaceAll(productsEntity)
    }

    suspend fun savePromotions(promotions: List<PromotionEntity>) {
        promotionDao.replaceAll(promotions)
    }
}