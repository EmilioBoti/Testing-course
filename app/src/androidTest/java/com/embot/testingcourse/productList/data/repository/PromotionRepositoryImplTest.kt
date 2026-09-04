package com.embot.testingcourse.productList.data.repository

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.embot.testingcourse.core.mockwebserver.MockWebServerUrlHolder
import com.embot.testingcourse.core.mockwebserver.rules.MockWebServerRule
import com.embot.testingcourse.core.utils.JsonUtils.readJsonFile
import com.embot.testingcourse.productList.domain.repository.PromotionRepository
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
class PromotionRepositoryImplTest {

    @get:Rule(order = 0)
    val mockWebServer: MockWebServerRule = MockWebServerRule()

    @get:Rule(order = 1)
    val hilt = HiltAndroidRule(this)

    @Inject
    lateinit var promotionRepositoryImpl: PromotionRepository


    @Before
    fun setUp() {
        hilt.inject()
    }

    @After
    fun tearDown() {
        MockWebServerUrlHolder.baseUrl = "http:://localhost:8080/"
    }

    @Test
    fun givenActivePromotionJson_whenRefreshIsCalled_thenFlowEmitsActivePromotion() = runTest {
        val json = readJsonFile("promotions_percent.json")
        mockWebServer.server.enqueue(MockResponse().setBody(json).setResponseCode(200))

        promotionRepositoryImpl.refreshPromotions()

        val promotions = promotionRepositoryImpl.getActivePromotions().first()
        assertTrue(promotions.isNotEmpty())
    }

    @Test
    fun givenEmptyPromotionJson_whenRefreshIsCalled_thenListIsEmpty() = runTest {
        mockWebServer.server.enqueue(MockResponse().setBody("""{ "promotions":[] }""").setResponseCode(200))

        promotionRepositoryImpl.refreshPromotions()

        val promotions = promotionRepositoryImpl.getActivePromotions().first()
        assertTrue(promotions.isEmpty())
    }

    @Test
    fun givenBuyXPayYJson_whenRefreshIsCalled_thenDomainMapsQuantitiesCorrecty() = runTest {
        val json = readJsonFile("promotions_buy_x_pay_y.json")
        mockWebServer.server.enqueue(MockResponse().setBody(json).setResponseCode(200))

        promotionRepositoryImpl.refreshPromotions()

        val promotion = promotionRepositoryImpl.getActivePromotions().first().find { it.id == "bxpy" }
        assertNotNull(promotion)
        assertEquals(2.0, promotion?.value)
        assertEquals(3, promotion?.buyQuantity)
    }

    @Test(expected = Exception::class)
    fun givenServerReturns500_whenRefreshIsCalled_thenItThrowsException() = runTest {
        mockWebServer.server.enqueue(MockResponse().setResponseCode(500))

        promotionRepositoryImpl.refreshPromotions()
    }

    @Test
    fun givenPromotionsEndPoint_whenRefreshIsCalled_thenRequestIsGetToCorrectPath() = runTest {
        val json = readJsonFile("promotions_buy_x_pay_y.json")
        mockWebServer.server.enqueue(MockResponse().setBody(json).setResponseCode(200))
        // WHEN
        promotionRepositoryImpl.refreshPromotions()

        val request = mockWebServer.server.takeRequest()
        assertEquals("GET", request.method)
        assertTrue(request.path?.contains("data/promotions.json") == true)
    }

}