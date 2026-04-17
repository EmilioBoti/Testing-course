package com.embot.testingcourse.cart.domain.usecase

import com.embot.testingcourse.core.builders.cartItem
import com.embot.testingcourse.core.builders.product
import com.embot.testingcourse.core.builders.promotion
import com.embot.testingcourse.core.fakes.FakeCartItemRepository
import com.embot.testingcourse.core.fakes.FakeProductRepository
import com.embot.testingcourse.core.fakes.FakePromotionRepository
import com.embot.testingcourse.core.fakes.FakeSystemClock
import com.embot.testingcourse.productList.domain.usecase.GetPromotionForProduct
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test
import java.time.Instant

class GetCartItemWithPromotionUseCaseTest {

    private val clock = FakeSystemClock().apply { setTime(Instant.parse("2026-04-03T10:00:00Z")) }

    private fun useCase(
        cartItemRepository: FakeCartItemRepository = FakeCartItemRepository(),
        productRepository: FakeProductRepository = FakeProductRepository(),
        promotionRepository: FakePromotionRepository = FakePromotionRepository(),
        clock: FakeSystemClock = this.clock
    ): GetCartItemWithPromotionUseCase {
        return GetCartItemWithPromotionUseCase(
            cartItemRepository = cartItemRepository,
            productRepository = productRepository,
            promotionRepository = promotionRepository,
            getPromotionForProduct = GetPromotionForProduct(),
            clock = clock
        )
    }

    @Test
    fun `give empty cart when invokes then retruns empty list`() = runTest {
        val cart = FakeCartItemRepository().apply { setCartItem(emptyList()) }

        val result = (useCase(cartItemRepository = cart)()).first()

        assertTrue("The initial state should returns always a empty list", result.isEmpty())
    }

    @Test
    fun `given existing cart item with active promotion when invoke then returns item with promotion`() = runTest {
        val productId = "product-id"

        val product = product {
            withId(productId)
        }
        val now = clock.now()

        val promo = promotion {
            withProductIds(listOf(productId))
            withStartTime(now.minusSeconds(10))
            withEndTime(now.plusSeconds(10))
        }

        val cartItem = cartItem {
            withProductId(productId)
            withQuantity(2)
        }

        val cartItemRepository = FakeCartItemRepository().apply { setCartItem(listOf(cartItem)) }
        val productRepository = FakeProductRepository().apply { setProducts(listOf(product)) }
        val promotionRepository = FakePromotionRepository().apply { setPromotion(listOf(promo)) }

        val result = (useCase(
            cartItemRepository = cartItemRepository,
            productRepository = productRepository,
            promotionRepository = promotionRepository
        )()).first()

        assertEquals(1, result.size)
        assertNotNull(result.first().item.promotion)
    }

    @Test
    fun `given cart item without matching product when invoke then skip item`() = runTest {

        val productId = "product-id"
        val product = product {
            withId(productId)
        }

        val cartItem = cartItem {
            withProductId("ghost-id")
            withQuantity(2)
        }

        val cartItemRepository = FakeCartItemRepository().apply { setCartItem(listOf(cartItem)) }
        val productRepository = FakeProductRepository().apply { setProducts(listOf(product)) }

        val result = (useCase(
            cartItemRepository = cartItemRepository,
            productRepository = productRepository,
        )()).first()

        assertTrue(result.isEmpty())

    }

    @Test
    fun `given promotion ending exactly now when invoke then it must be include`() = runTest {

        val productId = "product-id"

        val product = product {
            withId(productId)
        }
        val now = clock.now()

        val endingPromo = promotion {
            withProductIds(listOf(productId))
            withStartTime(now.minusSeconds(10))
            withEndTime(now)
        }

        val cartItem = cartItem {
            withProductId(productId)
            withQuantity(2)
        }

        val cartItemRepository = FakeCartItemRepository().apply { setCartItem(listOf(cartItem)) }
        val productRepository = FakeProductRepository().apply { setProducts(listOf(product)) }
        val promotionRepository = FakePromotionRepository().apply { setPromotion(listOf(endingPromo)) }

        val result = (useCase(
            cartItemRepository = cartItemRepository,
            productRepository = productRepository,
            promotionRepository = promotionRepository
        )()).first()

        assertNotNull(result.first().item.promotion)

    }

    @Test
    fun `given expired promotion when invoke then item remains but without promotion`() = runTest {
        val productId = "product-id"

        val product = product {
            withId(productId)
        }
        val now = clock.now()

        val endPromo = promotion {
            withProductIds(listOf(productId))
            withStartTime(now.minusSeconds(10))
            withEndTime(now.minusSeconds(1))
        }

        val cartItem = cartItem {
            withProductId(productId)
            withQuantity(2)
        }

        val cartItemRepository = FakeCartItemRepository().apply { setCartItem(listOf(cartItem)) }
        val productRepository = FakeProductRepository().apply { setProducts(listOf(product)) }
        val promotionRepository = FakePromotionRepository().apply { setPromotion(listOf(endPromo)) }

        val result = (useCase(
            cartItemRepository = cartItemRepository,
            productRepository = productRepository,
            promotionRepository = promotionRepository
        )()).first()


        assertNull(result.first().item.promotion)

    }

    @Test
    fun `given actice promotion when time advances then flows emits update list without promotion`() = runTest {
        val productId = "product-id"

        val product = product {
            withId(productId)
        }
        val now = clock.now()

        val endPromo = promotion {
            withProductIds(listOf(productId))
            withStartTime(now.minusSeconds(10))
            withEndTime(now.plusSeconds(5))
        }

        val cartItem = cartItem {
            withProductId(productId)
            withQuantity(2)
        }

        val cartItemRepository = FakeCartItemRepository().apply { setCartItem(listOf(cartItem)) }
        val productRepository = FakeProductRepository().apply { setProducts(listOf(product)) }
        val promotionRepository = FakePromotionRepository().apply { setPromotion(listOf(endPromo)) }

        val myUseCase = useCase(
            cartItemRepository = cartItemRepository,
            productRepository = productRepository,
            promotionRepository = promotionRepository
        )()

        val firstEmission = myUseCase.first()

        assertNotNull(firstEmission.first().item.promotion)

        clock.advanceTime(6)

        val secondEmission = myUseCase.first()

        assertNull(secondEmission.first().item.promotion)

    }

}