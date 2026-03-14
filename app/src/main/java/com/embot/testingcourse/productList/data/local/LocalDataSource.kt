package com.embot.testingcourse.productList.data.local

import com.embot.testingcourse.productList.data.local.db.dao.ProductDao
import com.embot.testingcourse.productList.data.local.db.dao.PromotionDao
import com.embot.testingcourse.productList.data.local.db.entity.ProductEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val productDao: ProductDao,
    private val promotionDao: PromotionDao
) {

    fun getAllProducts(): Flow<List<ProductEntity>> = productDao.getAllProducts()
}