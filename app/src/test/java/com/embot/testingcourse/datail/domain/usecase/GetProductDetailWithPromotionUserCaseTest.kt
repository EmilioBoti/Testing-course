package com.embot.testingcourse.datail.domain.usecase

import com.embot.testingcourse.core.builders.product
import com.embot.testingcourse.core.builders.promotion
import com.embot.testingcourse.core.fakes.FakeProductRepository
import com.embot.testingcourse.core.fakes.FakePromotionRepository
import com.embot.testingcourse.core.fakes.FakeSystemClock
import com.embot.testingcourse.productList.domain.model.PromotionType
import com.embot.testingcourse.productList.domain.usecase.GetPromotionForProduct
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.time.Instant

class GetProductDetailWithPromotionUserCaseTest {

    private lateinit var productRepository: FakeProductRepository
    private lateinit var promotionRepository: FakePromotionRepository
    private lateinit var clock: FakeSystemClock

    @Before
    fun setUp() {
        productRepository = FakeProductRepository()
        promotionRepository = FakePromotionRepository()
        clock = FakeSystemClock().apply { setTime(Instant.parse("2026-04-03T10:00:00Z")) }
    }

    private fun useCase(): GetProductDetailWithPromotionUserCase {
        return GetProductDetailWithPromotionUserCase(
            productRepository = productRepository,
            promotionRepository = promotionRepository,
            getPromotionForProduct = GetPromotionForProduct(),
            clock = clock
        )
    }

    @Test
    fun `given product when invoke then returns correctly product with promotion`() = runTest {
        val now = clock.now()
        val productId = "p1"
        val product = product {
            withId(productId)
            withPrice(100.0)
        }

        val promo = promotion {
            withProductIds(listOf(productId))
            withType(PromotionType.PERCENT)
            withValue(10.0)
            withStartTime(now.minusSeconds(10))
            withEndTime(now.plusSeconds(10))
        }

        productRepository.setProducts(listOf(product))
        promotionRepository.setPromotion(listOf(promo))

        val result = (useCase()(productId)).first()

        assertNotNull(result)
        assertNotNull(result?.product)
        assertNotNull(result?.promotion)
    }

    @Test
    fun `given not active promotion when invoke then returns product without promotion`() = runTest {
        val productId = "p1"
        val product = product { withId(productId) }

        productRepository.setProducts(listOf(product))
        promotionRepository.setPromotion(emptyList())

        val result = (useCase()(productId)).first()

        assertNotNull(result)
        assertNotNull(result?.product)
        assertNull(result?.promotion)
    }

    @Test
    fun `given invalid productId when invoke then returns null`() = runTest {
        val productId = "p1"
        val product = product { withId(productId) }

        productRepository.setProducts(listOf(product))

        val result = (useCase()("p3")).first()

        assertNull(result)
    }


    @Test
    fun `given expired promotion when invoke then returns product without promotion`() = runTest {
        val now = clock.now()
        val productId = "p1"
        val product = product {
            withId(productId)
            withPrice(100.0)
        }

        val promo = promotion {
            withProductIds(listOf(productId))
            withType(PromotionType.PERCENT)
            withValue(10.0)
            withStartTime(now.minusSeconds(100))
            withEndTime(now.minusSeconds(10))
        }

        productRepository.setProducts(listOf(product))
        promotionRepository.setPromotion(listOf(promo))

        val result = (useCase()(productId)).first()

        assertNotNull(result)
        assertNotNull(result?.product)
        assertNull(result?.promotion)
    }

}