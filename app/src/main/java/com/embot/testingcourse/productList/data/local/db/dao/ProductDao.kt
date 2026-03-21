package com.embot.testingcourse.productList.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.embot.testingcourse.productList.data.local.db.entity.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    @Query("SELECT * FROM PRODUCTS")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM PRODUCTS WHERE id=:id")
    fun getProductById(id: String): Flow<ProductEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<ProductEntity>)

    @Query("DElETE FROM products")
    suspend fun clearProducts()

    @Transaction
    suspend fun replaceAll(products: List<ProductEntity>) {
        clearProducts()
        insertProducts(products)
    }

    @Query("SELECT * FROM products WHERE id IN (:ids)")
    fun getProductByIds(ids: List<String>): Flow<List<ProductEntity>>

}
