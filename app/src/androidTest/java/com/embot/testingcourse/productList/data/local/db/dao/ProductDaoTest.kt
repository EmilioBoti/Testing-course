package com.embot.testingcourse.productList.data.local.db.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.embot.testingcourse.core.builders.productEntity
import com.embot.testingcourse.core.data.local.db.MiniMarketDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProductDaoTest {

    private lateinit var database: MiniMarketDatabase
    private lateinit var dao: ProductDao

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            MiniMarketDatabase::class.java
        ).build()
        dao = database.productDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun givenEmptyDatabse_whenGetAllProducts_thenEmitsEmptyList() = runTest {
        val products = dao.getAllProducts().first()

        assertTrue(products.isEmpty())
    }

    @Test
    fun givenInsertProduct_whenGetProductById_thenReturnsRow() = runTest {
        val productId = "p1"
        val entity = productEntity {
            withId(productId)
        }
        dao.insertProducts(listOf(entity))

        val product = dao.getProductById(productId).first()

        assertNotNull(product)
        assertEquals(productId, product.id)
    }

    @Test
    fun givenThreeProducts_whenGetProductByIds_thenReturnsResquestedSubSet() = runTest {
        val productId1 = "p1"
        val productId2 = "p2"
        val productId3 = "p3"

        val entity1 = productEntity { withId(productId1) }
        val entity2 = productEntity { withId(productId2) }
        val entity3 = productEntity { withId(productId3) }

        dao.insertProducts(listOf(entity1, entity2, entity3))

        val products = dao.getProductByIds(listOf(productId1, productId3)).first()


        assertTrue(products.any { it.id == productId1 })
        assertTrue(products.any { it.id == productId3 })
        assertTrue(products.none { it.id == productId2 })

    }


    @Test
    fun givenOldProducts_whenReplaceAll_thenOnlyNewProductsRemain() = runTest {
        // GIVEN
        val oldId1 = "old-p1"
        val oldId2 = "old-p2"

        val oldEntity1 = productEntity { withId(oldId1) }
        val oldEntity2 = productEntity { withId(oldId2) }

        val newId1 = "new-p1"
        val newId2 = "new-p2"
        val newId3 = "new-p3"

        val newEntity1 = productEntity { withId(newId1) }
        val newEntity2 = productEntity { withId(newId2) }
        val newEntity3 = productEntity { withId(newId3) }


        dao.insertProducts(listOf(oldEntity1, oldEntity2))

        // WHEN
        dao.replaceAll(listOf(newEntity1, newEntity2, newEntity3))

        // THEN
        val result = dao.getAllProducts().first()

        assertEquals(3, result.size)
        assertTrue(result.none { it.id == oldId1 || it.id == oldId2 })
        assertTrue(result.any { it.id == newId1 || it.id == newId2 || it.id == newId3 })

    }

    @Test
    fun givenExistingProduct_whenInsertSaleIdWithDifferentData_thenReplaceOldData() = runTest {
        // GIVEN
        val productId = "p1"

        val oldEntity = productEntity { withId(productId); withPrice(15.3) }
        val newEntity = productEntity { withId(productId); withPrice(20.0) }

        dao.insertProducts(listOf(oldEntity))

        // WHEN
        dao.insertProducts(listOf(newEntity))


        // WHEN
        val product = dao.getAllProducts().first()

        assertTrue(product.size == 1)
        assertEquals(20.0, product.first().price, 0.00001)

    }

    @Test
    fun givenFlowSubscribed_whenInsertAfterSubcribe_thenEmitsUpdatedList() = runTest {
        val productId = "p1"
        dao.getAllProducts().test {
            val initail = awaitItem()

            assertTrue(initail.isEmpty())

            dao.insertProducts(listOf(productEntity { withId(productId) }))

            val updated = awaitItem()

            assertEquals(1, updated.size)
            assertEquals(productId, updated.first().id)
            cancelAndIgnoreRemainingEvents()
        }
    }

}