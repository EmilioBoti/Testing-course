package com.embot.testingcourse.cart.data.local.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.embot.testingcourse.cart.data.local.db.entity.CartItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CartItemDao {

    @Query("SELECT * FROM cart_itmes")
    fun getAllCartItems(): Flow<List<CartItemEntity>>


    @Query("SELECT * FROM cart_itmes WHERE productId = :productId")
    suspend fun getCartItemById(productId: String): CartItemEntity?

    @Insert(CartItemEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(item: CartItemEntity)

    @Update()
    suspend fun updateCartItem(item: CartItemEntity)

    @Delete
    suspend fun deleteCartItem(item: CartItemEntity)

    @Query("DELETE FROM cart_itmes")
    suspend fun clearCartItems()

}