package com.embot.testingcourse.productList.data.repository

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.embot.testingcourse.core.mockwebserver.MockWebServerUrlHolder
import com.embot.testingcourse.core.mockwebserver.rules.MockWebServerRule
import com.embot.testingcourse.productList.domain.repository.ProductRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class ProductRepositoryImplTest {

    @get:Rule(order = 0)
    val mockWebServer: MockWebServerRule = MockWebServerRule()

    @get:Rule(order = 1)
    val hilt = HiltAndroidRule(this)

    @Inject
    lateinit var productRepository: ProductRepository


    private val productJson = """
        { "products": [
            { "id": "p1", "name": "Leche Entera 1L", "category": "Dairy", "priceCents": 120, "stock": 0, "imageUrl": "https://images.unsplash.com/photo-1580910051074-3eb694886505" },
            { "id": "p2", "name": "Huevos Camperos (12u)", "category": "Dairy", "priceCents": 310, "stock": 8, "imageUrl": "https://images.unsplash.com/photo-1587486913049-53fc88980cfc" }
        ]}
    """.trimIndent()

    @Before
    fun setUp() {
        hilt.inject()
    }

    @After
    fun tearDown() {
        MockWebServerUrlHolder.baseUrl = "http:://localhost:8080/"
    }

    @Test
    fun givenValidProductJson_whenRefreshIsCalled_thenDatabaseEmitProductsFromRoom() = runTest {
        mockWebServer.server.enqueue(MockResponse().setBody(productJson).setResponseCode(200))

        // WHEN
        productRepository.refreshPRoduct()

        // THEN
        val products = productRepository.getProduct().first()

        assertTrue(products.isNotEmpty())
        assertTrue(products.size == 2)
        assertEquals("Leche Entera 1L", products.find { it.id == "p1" }?.name )
    }

    @Test
    fun givenEmptProductJson_whenRefreshIsCalled_thenGetProductsEmitsEmptyList() = runTest {
        mockWebServer.server.enqueue(MockResponse().setBody("""{ "products": [] }""").setResponseCode(200))

        // WHEN
        productRepository.refreshPRoduct()

        // THEN
        val products = productRepository.getProduct().first()
        assertTrue(products.isEmpty())
    }

    @Test
    fun fivenProductJson_when_resfreshAndGetProductById_then_ReturnsCorrectProduct() = runTest {
        mockWebServer.server.enqueue(MockResponse().setBody(productJson).setResponseCode(200))
        // WHEN
        productRepository.refreshPRoduct()

        // THEN
        val product = productRepository.getProductById("p1").first()
        assertNotNull(product)
        assertEquals("Leche Entera 1L", product?.name)
    }

    @Test(expected = Exception::class)
    fun givenServerReturns500_thenRefreshIsCalled_thenItthrowsException() = runTest {
        mockWebServer.server.enqueue(MockResponse().setResponseCode(500))
        // WHEN
        productRepository.refreshPRoduct()
    }

    @Test
    fun givenCachedProducts_whenRefreshWithNewProducts_thenFlowEmitUpdatedData() = runTest {
        mockWebServer.server.enqueue(MockResponse().setBody(productJson).setResponseCode(200))
        productRepository.refreshPRoduct()

        val productJsonUpdated = """
        { "products": [
            { "id": "p1", "name": "Leche Entera 3L", "category": "Dairy", "priceCents": 360, "stock": 0, "imageUrl": "https://images.unsplash.com/photo-1580910051074-3eb694886505" }
        ]}
    """.trimIndent()

        mockWebServer.server.enqueue(MockResponse().setBody(productJsonUpdated).setResponseCode(200))
        productRepository.refreshPRoduct()

        val products = productRepository.getProduct().first()
        assertEquals("Leche Entera 3L", products.find { it.id == "p1" }?.name)
        assertEquals(3.6, products.find { it.id == "p1" }?.price)
    }

    @Test
    fun givenProductsEndPoint_whenRefreshIsCalled_thenRequestIsGetToCorrectPath() = runTest {
        mockWebServer.server.enqueue(MockResponse().setBody(productJson).setResponseCode(200))
        // WHEN
        productRepository.refreshPRoduct()

//        mockWebServer.server.
        val request = mockWebServer.server.takeRequest()
        assertEquals("GET", request.method)
        assertTrue(request.path?.contains("data/products.json") == true)
    }

}