package com.embot.testingcourse.productList.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.embot.testingcourse.productList.data.local.db.entity.PromotionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PromotionDao {

    @Query("SELECT * FROM promotions")
    fun getAllPromotions(): Flow<List<PromotionEntity>>

    @Query("SELECT * FROM promotions WHERE id=:id")
    fun getPromotionsById(id: String): Flow<PromotionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPromotions(promotions: List<PromotionEntity>)

    @Query("DElETE FROM promotions")
    suspend fun clearPromotions()

    @Transaction
    suspend fun replaceAll(promotions: List<PromotionEntity>) {
        clearPromotions()
        insertPromotions(promotions)
    }
}
