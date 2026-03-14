package com.embot.testingcourse.productList.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.embot.testingcourse.productList.data.local.db.dao.ProductDao
import com.embot.testingcourse.productList.data.local.db.dao.PromotionDao
import com.embot.testingcourse.productList.data.local.db.entity.ProductEntity
import com.embot.testingcourse.productList.data.local.db.entity.PromotionEntity

@Database(
    entities = [ProductEntity::class, PromotionEntity::class],
    version = 1,
    exportSchema = true
)
abstract class MiniMarketDatabase: RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun promotionDao(): PromotionDao
}