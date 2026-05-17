package com.embot.testingcourse.productList.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.embot.testingcourse.core.builders.cartItemEntity
import com.embot.testingcourse.core.builders.productEntity
import com.embot.testingcourse.core.builders.promotionEntity
import com.embot.testingcourse.core.data.local.db.MiniMarketDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LocalDataSourceTest {

    private lateinit var database: MiniMarketDatabase
    private lateinit var localDataSource: LocalDataSource


    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            context = ApplicationProvider.getApplicationContext(),
            MiniMarketDatabase::class.java
        ).build()
        localDataSource = LocalDataSource(
            productDao = database.productDao(),
            promotionDao = database.promotionDao(),
            cartItemDao = database.cartItemDao()
        )
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun givenProducts_whenSaveAndGetAll_thenreturnsPersistedProduct() = runTest {
        val products = listOf(
            productEntity { withId("1") },
            productEntity { withId("2") },
        )

        localDataSource.saveProducts(products)

        val result = localDataSource.getAllProducts().first()

        assertEquals(2, result.size)
    }

    @Test
    fun givenSavedProduct_whenGetProductById_thenReturnsCorrectProduct() = runTest {
        val products = listOf(
            productEntity { withId("1"); withName("leche") },
            productEntity { withId("2") },
        )

        localDataSource.saveProducts(products)

        val result = localDataSource.getProductById("1").first()

        assertNotNull(result)
        assertEquals("leche", result?.name)
    }

    @Test
    fun givenThreeProducts_whenGetProductsById_thenReturnsrequestSubset() = runTest {
        val products = listOf(
            productEntity { withId("1"); withName("leche") },
            productEntity { withId("2"); withName("carne")  },
            productEntity { withId("3"); withName("cookie")  },
        )

        localDataSource.saveProducts(products)

        val result = localDataSource.getProductByIds(setOf("1", "3")).first()

        assertEquals(2, result.size)
        assertTrue(result.any { it.name == "leche" })
        assertTrue(result.any { it.name == "cookie" })
    }

    @Test
    fun givenPromotions_whenSaveAndgetaLl_thenReturnsPersistedPromotions() = runTest {
        val promotions = listOf(
            promotionEntity { withId("1") },
            promotionEntity { withId("2"); withProductIds("""["p1"]""") },
        )

        localDataSource.savePromotions(promotions)

        val result = localDataSource.getAllPromotions().first()

        assertEquals(2, result.size)
    }

    @Test
    fun giveCartItem_whenIsertCartItem_thenReturnsSuccessAndItemSaved() = runTest {
        // GIVEN
        val cartItem = cartItemEntity { withProductId("p1"); withQuantity(2) }

        //WHEN
        val result = localDataSource.insertCartItem(cartItem)
        assertTrue(result.isSuccess)

        val items = localDataSource.getAllCartItems().first()

        assertEquals(1, items.size)
        assertEquals("p1", items.first().productId)
    }

    @Test
    fun giveExisting_whenUpdateCartItem_thenReturnsSuccessAndCartItemUpdated() = runTest {
        // GIVEN
        val productId = "p1"
        val cartItem = cartItemEntity { withProductId(productId); withQuantity(2) }
        localDataSource.insertCartItem(cartItem)

        val cartItem2 = cartItemEntity { withProductId(productId); withQuantity(67) }
        val result = localDataSource.updateCartItem(cartItem2)
        assertTrue(result.isSuccess)

        val item = localDataSource.getCartItemById(productId)

        assertNotNull(item)
        assertEquals(67, item?.quantity)
    }

    @Test
    fun givenCartItem_whenDeleteCartItem_thenReturnSuccessAndCartIsEmpty() = runTest {
        // GIVEN
        val productId = "p1"
        val cartItem = cartItemEntity { withProductId(productId); withQuantity(2) }
        localDataSource.insertCartItem(cartItem)

        val result = localDataSource.deleteCartItem(cartItem)
        assertTrue(result.isSuccess)

        val item = localDataSource.getAllCartItems().first()
        assertTrue(item.isEmpty())
    }

    @Test
    fun givenMultipleCartItem_whenclearCart_thenReturnSuccessAndCartIsEmpty() = runTest {
        // GIVEN
        val cartItem = cartItemEntity { withProductId("1"); withQuantity(2) }
        val cartItem2 = cartItemEntity { withProductId("2"); withQuantity(3) }
        val cartItem3 = cartItemEntity { withProductId("2"); withQuantity(4) }

        localDataSource.insertCartItem(cartItem)
        localDataSource.insertCartItem(cartItem2)
        localDataSource.insertCartItem(cartItem3)

        val result = localDataSource.clearCart()
        assertTrue(result.isSuccess)

        val item = localDataSource.getAllCartItems().first()
        assertTrue(item.isEmpty())
    }

}