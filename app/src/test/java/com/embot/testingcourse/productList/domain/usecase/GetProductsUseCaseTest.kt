package com.embot.testingcourse.productList.domain.usecase

import com.embot.testingcourse.core.builders.product
import com.embot.testingcourse.core.builders.promotion
import com.embot.testingcourse.core.fakes.FakeProductRepository
import com.embot.testingcourse.core.fakes.FakePromotionRepository
import com.embot.testingcourse.core.fakes.FakeSettingsRepository
import com.embot.testingcourse.core.fakes.FakeSystemClock
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test
import java.time.Instant

class GetProductsUseCaseTest {

    private fun useCase(
        product: FakeProductRepository = FakeProductRepository(),
        promo: FakePromotionRepository = FakePromotionRepository(),
        settings: FakeSettingsRepository = FakeSettingsRepository(),
        clock: FakeSystemClock = FakeSystemClock()
    ) = GetProductsUseCase(
        product,
        promo,
        GetPromotionForProduct(),
        settings,
        clock
    )


    @Test
    fun `give promotion ending now when invoke then it should be included`() = runTest {
        // GIVEN
        val now = Instant.parse("2026-04-03T10:00:00Z")
        val productId = "product-1"
        val product = product {
            withId(productId)
        }
        val promo = promotion {
            withProductIds(listOf(productId))
            withStartTime(now.minusSeconds(60))
            withEndTime(now)
        }

        val clock = FakeSystemClock().apply { setTime(now) }
        val productRepository = FakeProductRepository().apply { setProducts(listOf(product)) }
        val promotionRepository = FakePromotionRepository().apply { setPromotion(listOf(promo)) }

        // WHEN
        val result = (useCase(productRepository, promotionRepository, clock = clock)()).first()

        // THEN
        assertNotNull(result.first())
    }

    @Test
    fun `given active promotion when time advances then promotion should not be longer be retuened`() = runTest {
        // GIVEN
        val now = Instant.parse("2026-04-03T10:00:00Z")
        val productId = "product-1"
        val product = product {
            withId(productId)
        }
        val promo = promotion {
            withProductIds(listOf(productId))
            withStartTime(now)
            withEndTime(now.plusSeconds(5))
        }

        val clock = FakeSystemClock().apply { setTime(now) }
        val productRepository = FakeProductRepository().apply { setProducts(listOf(product)) }
        val promotionRepository = FakePromotionRepository().apply { setPromotion(listOf(promo)) }

        // WHEN
        val firstResult = (useCase(product = productRepository, promo = promotionRepository, clock = clock)()).first()

        clock.advanceTime(6)

        val secondResult = (useCase(product = productRepository, promo = promotionRepository, clock = clock)()).first()

        // THEN
        assertNotNull(firstResult.first().promotion)
        assertNull(secondResult.first().promotion)

    }

    @Test
    fun `given inStokcOnly enebled when product goes out of stock then it should be filtered`() = runTest {
        // GIVEN
        val productId = "product-1"
        val product = product {
            withId(productId)
            withStock(0)
        }

        val settings = FakeSettingsRepository().apply { setInStockOnly(true) }
        val productRepository = FakeProductRepository().apply { setProducts(listOf(product)) }

        val myUseCase = useCase(settings = settings, product = productRepository)

        // WHEN
        val result = myUseCase().first()

        // THEN
        assertTrue(result.isEmpty())

    }
}