package com.embot.testingcourse.cart.data.local.db.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.embot.testingcourse.core.builders.cartItemEntity
import com.embot.testingcourse.core.data.local.db.MiniMarketDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CartItemDaoTest {

    private lateinit var database: MiniMarketDatabase
    private lateinit var dao: CartItemDao


    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            context = ApplicationProvider.getApplicationContext(),
            MiniMarketDatabase::class.java
        ).build()

        dao = database.cartItemDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun givenEmptyCart_when_GetAllCartItems_thenEmitsEmptyList() = runTest {
        val items = dao.getAllCartItems().first()

        assertTrue(items.isEmpty())
    }

    @Test
    fun givenEmptyCart_whenInsertItem_thenItemIsPersisted() = runTest {
        val productId = "p1"
        val quantity = 3

        val item = cartItemEntity { withProductId(productId); withQuantity(quantity) }

        dao.insertCartItem(item)

        val insertedItem = dao.getAllCartItems().first()

        assertEquals(1, insertedItem.size)
        assertEquals(productId, insertedItem.first().productId)
        assertEquals(quantity, insertedItem.first().quantity)
    }

    @Test
    fun givenInsertedItem_whenGetItemById_thenReturnsCorrectItem() = runTest {
        val productId = "p1"
        val quantity = 3

        val item = cartItemEntity { withProductId(productId); withQuantity(quantity) }

        dao.insertCartItem(item)

        val insertedItem = dao.getCartItemById(productId)

        assertEquals(productId, insertedItem?.productId)
        assertEquals(quantity, insertedItem?.quantity)
    }

    @Test
    fun givenEmptyCart_whenGetItemById_thenReturnsNull() = runTest {
        val item = dao.getCartItemById("p1")
        assertNull(item)
    }

    @Test
    fun givenExistingItem_whenUpdateItemQuantity_thenQuantityIsUpdated() = runTest {
        // GIVEN
        val productId = "p1"
        val newQuantity = 5
        dao.insertCartItem(cartItemEntity { withProductId(productId); withQuantity(3) })

        // WHEN
        dao.updateCartItem(cartItemEntity { withProductId(productId); withQuantity(newQuantity) })

        // THEN
        val item = dao.getCartItemById(productId)

        assertEquals(newQuantity, item?.quantity)
    }

    @Test
    fun givenItemCart_whenDeleteItem_thenDeleteItemFromCart() = runTest {
        // GIVEN
        val cartItem = cartItemEntity { withProductId("p1"); withQuantity(5) }
        dao.insertCartItem(cartItem)

        // WHEN
        dao.deleteCartItem(cartItem)

        // THEN
        val item = dao.getAllCartItems().first()

        assertTrue(item.isEmpty())
    }

    @Test
    fun givenMultipleItems_whenClearCart_thenAllItemAreRemoved() = runTest {
        // GIVEN
        val cartItem1 = cartItemEntity { withProductId("p1"); withQuantity(5) }
        val cartItem2 = cartItemEntity { withProductId("p2"); withQuantity(5) }
        val cartItem3 = cartItemEntity { withProductId("p3"); withQuantity(5) }

        dao.insertCartItem(cartItem1)
        dao.insertCartItem(cartItem2)
        dao.insertCartItem(cartItem3)

        // WHEN
        dao.clearCartItems()

        // THEN
        val item = dao.getAllCartItems().first()

        assertTrue(item.isEmpty())
    }

    @Test
    fun givenExistingItemId_whenInsertDuplicateId_thenItemReplaced() = runTest {
        // GIVEN
        val productId = "p1"
        val cartItem1 = cartItemEntity { withProductId(productId); withQuantity(2) }
        val cartItem2 = cartItemEntity { withProductId(productId); withQuantity(8) }

        dao.insertCartItem(cartItem1)
        // WHEN
        dao.insertCartItem(cartItem2)

        // THEN
        val result = dao.getAllCartItems().first()

        assertEquals(8, result.first().quantity)

    }

}