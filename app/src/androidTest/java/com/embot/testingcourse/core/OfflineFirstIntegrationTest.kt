package com.embot.testingcourse.core

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.embot.testingcourse.core.data.local.db.MiniMarketDatabase
import com.embot.testingcourse.core.domain.model.AppError
import com.embot.testingcourse.core.mockwebserver.MiniMarketApiDispatcher
import com.embot.testingcourse.core.mockwebserver.MiniMarketApiDispatcherError
import com.embot.testingcourse.core.mockwebserver.MockWebServerUrlHolder
import com.embot.testingcourse.core.mockwebserver.rules.MockWebServerRule
import com.embot.testingcourse.core.utils.asAsset
import com.embot.testingcourse.productList.domain.repository.ProductRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject
import kotlin.test.assertFailsWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class OfflineFirstIntegrationTest {

    companion object {
        const val DEFAULT_PRODUCT_ASSET = "product_list_default.json"
        const val UPDATED_PRODUCT_ASSET = "product_list_updated.json"
        const val DEFAULT_PRODUCT_SIZE = 3
        const val UPDATED_PRODUCT_SIZE = 1
    }

    @get:Rule(0)
    val mockWebServerRule = MockWebServerRule()

    @get:Rule(1)
    val hilt = HiltAndroidRule(this)

    @Inject
    lateinit var db: MiniMarketDatabase

    @Inject
    lateinit var productRepository: ProductRepository

    @Before
    fun setUp() {
        hilt.inject()
        db.clearAllTables()
    }

    @After
    fun tearDown() {
        MockWebServerUrlHolder.baseUrl = "http://localhost:8080/"
        db.close()
    }

    @Test
    fun givenSuccessfulRefresh_whenGetProducts_thenRoomContainsRemoteProducts() = runTest {
        serverProductsFromAssets(DEFAULT_PRODUCT_ASSET)

        productRepository.refreshPRoduct()

        val cachedProducts = productRepository.getProduct().first { products -> products.size == DEFAULT_PRODUCT_SIZE }

        assertEquals(DEFAULT_PRODUCT_SIZE, cachedProducts.size)
    }

    @Test
    fun givenEmptyCacheAndFailedRefresh_whenGetProducts_thenEmitsEmptyList() = runTest {
        serverProductsError()

        /**
         * This is another way can test it, but we cannot know the exact error type
         * with solution below we solve that
         */
//        val result = runCatching { productRepository.refreshPRoduct() }
//        assertTrue(result.isFailure)

        assertFailsWith<AppError.NetworkError> {
            productRepository.refreshPRoduct()
        }

        val products = productRepository.getProduct().first { products -> products.isEmpty() }
        assertTrue(products.isEmpty())
    }

    @Test
    fun givenCachedProductsAndFailedRefresh_whenGetProducts_thenReturnsPreviousProductsCached() = runTest {
        serverProductsFromAssets(DEFAULT_PRODUCT_ASSET)
        productRepository.refreshPRoduct()

        productRepository.getProduct().first { products -> products.size == DEFAULT_PRODUCT_SIZE }

        serverProductsError()

        assertFailsWith<AppError.NetworkError> { productRepository.refreshPRoduct() }

        val cachedProducts = productRepository.getProduct().first { products -> products.size == DEFAULT_PRODUCT_SIZE }
        assertEquals(DEFAULT_PRODUCT_SIZE, cachedProducts.size)
    }

    @Test
    fun givenCachedProducts_whenRefreshWithNewPayload_thenContainsOnlyLatestProducts() = runTest {
        serverProductsFromAssets(DEFAULT_PRODUCT_ASSET)
        productRepository.refreshPRoduct()

        productRepository.getProduct().first { products -> products.size == DEFAULT_PRODUCT_SIZE }

        serverProductsFromAssets(UPDATED_PRODUCT_ASSET)

        productRepository.refreshPRoduct()

        val updatedProducts = productRepository.getProduct().first { products -> products.size == UPDATED_PRODUCT_SIZE }
        assertEquals(UPDATED_PRODUCT_SIZE, updatedProducts.size)
        assertEquals("updated-p1", updatedProducts.first().id)
        assertEquals("Pan integral", updatedProducts.first().name)
    }

    private fun serverProductsFromAssets(assetsName: String) {
        mockWebServerRule.server.dispatcher = MiniMarketApiDispatcher(
            productJson = assetsName.asAsset()
        )
    }

    private fun serverProductsError() {
        mockWebServerRule.server.dispatcher = MiniMarketApiDispatcherError()
    }
}