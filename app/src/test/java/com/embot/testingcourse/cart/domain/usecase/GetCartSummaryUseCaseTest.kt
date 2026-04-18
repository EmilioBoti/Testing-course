package com.embot.testingcourse.cart.domain.usecase

import com.embot.testingcourse.core.builders.cartItem
import com.embot.testingcourse.core.builders.product
import com.embot.testingcourse.core.builders.promotion
import com.embot.testingcourse.core.fakes.FakeCartItemRepository
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

class GetCartSummaryUseCaseTest {

    private lateinit var clock: FakeSystemClock
    private lateinit var cartItemRepository: FakeCartItemRepository
    private lateinit var productRepository: FakeProductRepository
    private lateinit var promotionRepository: FakePromotionRepository


    @Before
    fun setUp() {
        clock = FakeSystemClock().apply { setTime(Instant.parse("2026-04-03T10:00:00Z")) }
        cartItemRepository = FakeCartItemRepository()
        productRepository = FakeProductRepository()
        promotionRepository = FakePromotionRepository()
    }

    private fun useCase(): GetCartSummaryUseCase {
        return GetCartSummaryUseCase(
            cartItemRepository = cartItemRepository,
            productRepository = productRepository,
            promotionRepository = promotionRepository,
            getPromotionForProduct = GetPromotionForProduct(),
            clock = clock
        )
    }

    @Test
    fun `given percent promotion when invoke then calculate correctly`() = runTest {
        val productId = "p1"
        val product = product { withId(productId); withPrice(100.0) }
        val promo = promotion {
            withProductIds(listOf(productId))
            withType(PromotionType.PERCENT)
            withValue(10.0)
            withStartTime(clock.now().minusSeconds(10))
            withEndTime(clock.now().plusSeconds(10))
        }

        val cartItem = cartItem {
            withProductId(productId)
            withQuantity(2)
        }

        productRepository.setProducts(listOf(product))
        promotionRepository.setPromotion(listOf(promo))
        cartItemRepository.setCartItem(listOf(cartItem))

        val summary = (useCase()()).first()

        assertEquals(180.0, summary.finalTotal, 0.0001)
        assertEquals(20.0, summary.discountTotal, 0.0001)
        assertEquals(200.0, summary.subTotal, 0.0001)

    }

    @Test
    fun `given 3 items in 2x1 promotion when invoke then only discounts 1 unit`() = runTest {
        val productId = "p1"
        val product = product { withId(productId); withPrice(100.0) }
        val promo = promotion {
            withProductIds(listOf(productId))
            withType(PromotionType.BUY_X_PAY_Y)
            withBuyQuantity(2)
            withValue(1.0)
            withStartTime(clock.now().minusSeconds(10))
            withEndTime(clock.now().plusSeconds(10))
        }

        val cartItem = cartItem {
            withProductId(productId)
            withQuantity(3)
        }

        productRepository.setProducts(listOf(product))
        promotionRepository.setPromotion(listOf(promo))
        cartItemRepository.setCartItem(listOf(cartItem))

        val summary = (useCase()()).first()


        assertEquals(300.0, summary.subTotal, 0.0001)
        assertEquals(200.0, summary.finalTotal, 0.0001)
        assertEquals(100.0, summary.discountTotal, 0.0001)
    }

    @Test
    fun `given miultiple produts with differents promotions when invoke then sums all corrrectly`() = runTest {
        val now = clock.now()

        val p1 = product { withId("p1"); withPrice(100.0) } // With promo
        val p2 = product { withId("p2"); withPrice(50.0) } // Without promo

        val promoPercent = promotion {
            withProductIds(listOf("p1"))
            withType(PromotionType.PERCENT)
            withValue(10.0)
            withStartTime(now.minusSeconds(10))
            withEndTime(now.plusSeconds(10))
        }

        val cartItems = listOf(
            cartItem { withProductId("p1"); withQuantity(1) },
            cartItem { withProductId("p2"); withQuantity(1) }
        )

        productRepository.setProducts(listOf(p1, p2))
        promotionRepository.setPromotion(listOf(promoPercent))
        cartItemRepository.setCartItem(cartItems)

        val summaary = useCase()().first()

        assertEquals(150.0, summaary.subTotal, 0.0001)
        assertEquals(140.0, summaary.finalTotal, 0.0001)
        assertEquals(10.0, summaary.discountTotal, 0.0001)

    }

    @Test
    fun `given expired promotion when invoke then discount is zero`() = runTest {
        val now = clock.now()

        val p1 = product { withId("p1"); withPrice(100.0) }

        val promoPercent = promotion {
            withProductIds(listOf("p1"))
            withType(PromotionType.PERCENT)
            withValue(10.0)
            withStartTime(now.minusSeconds(10))
            withEndTime(now.minusSeconds(5))
        }

        val cartItem = cartItem { withProductId("p1"); withQuantity(1) }

        productRepository.setProducts(listOf(p1))
        promotionRepository.setPromotion(listOf(promoPercent))
        cartItemRepository.setCartItem(listOf(cartItem))

        val summaary = useCase()().first()

        assertEquals(0.0, summaary.discountTotal, 0.0001)
        assertEquals(100.0, summaary.finalTotal, 0.0001)

    }

    @Test
    fun `given active promotion when time advances then summary update automatically`() = runTest {
        val now = clock.now()

        val p1 = product { withId("p1"); withPrice(100.0) }

        val promoPercent = promotion {
            withProductIds(listOf("p1"))
            withType(PromotionType.PERCENT)
            withValue(10.0)
            withStartTime(now.minusSeconds(10))
            withEndTime(now.plusSeconds(5))
        }

        val cartItem = cartItem { withProductId("p1"); withQuantity(1) }

        productRepository.setProducts(listOf(p1))
        promotionRepository.setPromotion(listOf(promoPercent))
        cartItemRepository.setCartItem(listOf(cartItem))

        val summaary = useCase()()

        assertEquals(10.0, summaary.first().discountTotal, 0.0001)

        clock.advanceTime(6)

        assertEquals(0.0, summaary.first().discountTotal, 0.0001)

    }

}