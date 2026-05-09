package com.embot.testingcourse.datail.presentation

import app.cash.turbine.test
import com.embot.testingcourse.cart.domain.usecase.AddToCartUserCase
import com.embot.testingcourse.core.MainDispatcherRule
import com.embot.testingcourse.core.builders.product
import com.embot.testingcourse.core.builders.promotion
import com.embot.testingcourse.core.fakes.FakeCartItemRepository
import com.embot.testingcourse.core.fakes.FakeProductRepository
import com.embot.testingcourse.core.fakes.FakePromotionRepository
import com.embot.testingcourse.core.fakes.FakeSystemClock
import com.embot.testingcourse.datail.domain.usecase.GetProductDetailWithPromotionUserCase
import com.embot.testingcourse.productList.domain.model.ProductPromotion
import com.embot.testingcourse.productList.domain.model.PromotionType
import com.embot.testingcourse.productList.domain.usecase.GetPromotionForProduct
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.Instant

class ProductDetailViewModelTest {

    private lateinit var productRepository: FakeProductRepository
    private lateinit var promotionRepository: FakePromotionRepository
    private lateinit var cartItemRepository: FakeCartItemRepository
    private val clock: FakeSystemClock = FakeSystemClock().apply { setTime(Instant.parse("2026-04-03T10:00:00Z")) }

    private val productId = "id1"

    @get:Rule
    private val mainDispatcherRule: MainDispatcherRule = MainDispatcherRule()


    @Before
    fun setUp() {
        this.productRepository = FakeProductRepository()
        this.promotionRepository = FakePromotionRepository()
        this.cartItemRepository = FakeCartItemRepository()
    }


    private fun createViewModel(): ProductDetailViewModel {
        val getProductDetailWithPromotionUserCase = GetProductDetailWithPromotionUserCase(
            productRepository = productRepository,
            promotionRepository = promotionRepository,
            getPromotionForProduct = GetPromotionForProduct(),
            clock = clock
        )

        val addToCartUserCase = AddToCartUserCase(
            cartItemRepository = cartItemRepository,
            productRepository = productRepository
        )

        return ProductDetailViewModel(
            getProductDetailWithPromotionUserCase = getProductDetailWithPromotionUserCase,
            addToCartUserCase = addToCartUserCase
        )
    }

    @Test
    fun `given valid productId when load product then emits item`() = runTest(mainDispatcherRule.scheduler) {

        // GIVEN
        val product = product {
            withId(productId)
            withPrice(15.0)
        }

        productRepository.apply { setProducts(listOf(product)) }

        val viewModel = createViewModel()

        viewModel.uiState.test {
            awaitItem()

            // WHEN
            viewModel.loadProduct(productId)

            // THEN
            val state = awaitItem()

            assertEquals(productId, state.item?.product?.id)
            cancelAndIgnoreRemainingEvents()
        }

    }

    @Test
    fun `given missing productId when load product then emits item null`() = runTest(mainDispatcherRule.scheduler) {

        // GIVEN
        productRepository.apply { setProducts(emptyList()) }
        val viewModel = createViewModel()

        viewModel.uiState.test {
            awaitItem()

            // WHEN
            viewModel.loadProduct(productId)

            // THEN
            val state = awaitItem()

            assertNull(state.item)
            cancelAndIgnoreRemainingEvents()
        }

    }

    @Test
    fun `given product with promotion when load data then update state with not promotion`() = runTest(mainDispatcherRule.scheduler) {
        // GIVEN
        val now = clock.now()

        val product = product {
            withId(productId)
            withPrice(15.0)
        }

        val promotion = promotion {
            withProductIds(listOf(productId))
            withType(PromotionType.PERCENT)
            withValue(30.0)
            withStartTime(now.minusSeconds(10))
            withEndTime(now.plusSeconds(10))
        }

        productRepository.apply { setProducts(listOf(product)) }
        promotionRepository.apply { setPromotion(listOf(promotion)) }

        val viewModel = createViewModel()

        // WHEN
        viewModel.loadProduct(productId)

        viewModel.uiState.test {

            awaitItem()
            val state = awaitItem()

            assertNotNull(state.item?.promotion)
            assertTrue(state.item?.promotion is ProductPromotion.Percent)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `given product when add to cart then emits success add to cart`() = runTest(mainDispatcherRule.scheduler) {
        // GIVEN
        val product = product {
            withId(productId)
            withStock(10)
            withPrice(15.0)
        }
        productRepository.setProducts(listOf(product))
        val viewModel = createViewModel()

        viewModel.events.test {
            // WHEN
            viewModel.addToCart(productId)
            // THEN
            val event = awaitItem()

            assertEquals(ProductDetailEvent.SuccessAddToCart, event)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `given product when add to cart then emits insuffient stock error`() = runTest(mainDispatcherRule.scheduler) {
        // GIVEN
        val product = product {
            withId(productId)
            withStock(0)
            withPrice(15.0)
        }
        productRepository.setProducts(listOf(product))
        val viewModel = createViewModel()

        viewModel.events.test {
            // WHEN
            viewModel.addToCart(productId)
            // THEN
            val event = awaitItem()

            assertEquals(ProductDetailEvent.InsuficientStockError, event)
            cancelAndIgnoreRemainingEvents()
        }
    }

}