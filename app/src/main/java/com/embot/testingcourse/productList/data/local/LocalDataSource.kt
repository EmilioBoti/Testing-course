package com.embot.testingcourse.productList.data.local

import com.embot.testingcourse.cart.data.local.db.dao.CartItemDao
import com.embot.testingcourse.cart.data.local.db.entity.CartItemEntity
import com.embot.testingcourse.productList.data.local.db.dao.ProductDao
import com.embot.testingcourse.productList.data.local.db.dao.PromotionDao
import com.embot.testingcourse.productList.data.local.db.entity.ProductEntity
import com.embot.testingcourse.productList.data.local.db.entity.PromotionEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val productDao: ProductDao,
    private val promotionDao: PromotionDao,
    private val cartItemDao: CartItemDao
) {

    fun getAllProducts(): Flow<List<ProductEntity>> = productDao.getAllProducts()
    fun getProductById(productId: String): Flow<ProductEntity?> = productDao.getProductById(productId)
    fun getAllPromotions(): Flow<List<PromotionEntity>> = promotionDao.getAllPromotions()
    fun getAllCartItems(): Flow<List<CartItemEntity>> = cartItemDao.getAllCartItems()

    suspend fun saveProducts(productsEntity: List<ProductEntity>) {
        productDao.replaceAll(productsEntity)
    }

    suspend fun savePromotions(promotions: List<PromotionEntity>) {
        promotionDao.replaceAll(promotions)
    }

    suspend fun saveToCartItems(item: CartItemEntity) {
        cartItemDao.insertCartItem(item)
    }

    suspend fun getCartItemById(productId: String): CartItemEntity? {
        return cartItemDao.getCartItemById(productId)
    }

    suspend fun updateCartItem(item: CartItemEntity): Result<Unit> {
        return try {
            cartItemDao.updateCartItem(item)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun insertCartItem(item: CartItemEntity): Result<Unit> {
        return try {
            cartItemDao.insertCartItem(item)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteCartItem(item: CartItemEntity): Result<Unit> {
        return try {
            cartItemDao.deleteCartItem(item)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun clearCart(): Result<Unit> {
        return try {
            cartItemDao.clearCartItems()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}