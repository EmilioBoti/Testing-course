package com.embot.testingcourse.cart.presentation

import app.cash.turbine.test
import com.embot.testingcourse.cart.domain.repository.CartItemRepository
import com.embot.testingcourse.cart.domain.usecase.GetCartItemWithPromotionUseCase
import com.embot.testingcourse.cart.domain.usecase.GetCartSummaryUseCase
import com.embot.testingcourse.cart.domain.usecase.UpdateCartItemUseCase
import com.embot.testingcourse.core.MainDispatcherRule
import com.embot.testingcourse.core.builders.cartItem
import com.embot.testingcourse.core.builders.product
import com.embot.testingcourse.core.domain.utils.Clock
import com.embot.testingcourse.core.fakes.FakeCartItemRepository
import com.embot.testingcourse.core.fakes.FakeProductRepository
import com.embot.testingcourse.core.fakes.FakePromotionRepository
import com.embot.testingcourse.core.fakes.FakeSystemClock
import com.embot.testingcourse.productList.domain.repository.ProductRepository
import com.embot.testingcourse.productList.domain.repository.PromotionRepository
import com.embot.testingcourse.productList.domain.usecase.GetPromotionForProduct
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class CartViewModelTest {

    @get:Rule
    private val mainDispatcherRule: MainDispatcherRule = MainDispatcherRule()

    private fun createViewModel(
        productRepository: ProductRepository = FakeProductRepository(),
        cartItemRepository: CartItemRepository = FakeCartItemRepository(),
        promotionRepository: PromotionRepository = FakePromotionRepository(),
        clock: Clock = FakeSystemClock()
    ): CartViewModel {

        val getCartSummaryUseCase = GetCartSummaryUseCase(
            cartItemRepository = cartItemRepository,
            productRepository = productRepository,
            promotionRepository = promotionRepository,
            getPromotionForProduct = GetPromotionForProduct(),
            clock = clock
        )

        val updateCartItemUseCase = UpdateCartItemUseCase(
            cartItemRepository = cartItemRepository,
            productRepository = productRepository
        )

        val getCartItemWithPromotionUseCase = GetCartItemWithPromotionUseCase(
            cartItemRepository = cartItemRepository,
            productRepository = productRepository,
            promotionRepository = promotionRepository,
            getPromotionForProduct = GetPromotionForProduct(),
            clock = clock
        )

        return CartViewModel(
            cartItemRepository = cartItemRepository,
            getCartSummaryUseCase = getCartSummaryUseCase,
            updateCartItemUseCase = updateCartItemUseCase,
            getCartItemWithPromotionUseCase = getCartItemWithPromotionUseCase
        )
    }

    @Test
    fun `given cart data when initialized then emit success state`() = runTest(mainDispatcherRule.scheduler) {
        // GIVEN
        val productId = "p1"
        val p1 = product { withId(productId); withName("Pan"); withPrice(2.0) }
        val item = cartItem { withProductId(productId); withQuantity(3) }

        val fakeProductRepository = FakeProductRepository().apply { setProducts(listOf(p1)) }
        val fakeCartItemRepository = FakeCartItemRepository().apply { setCartItem(listOf(item)) }

        // WHEN
        val viewModel = createViewModel(productRepository = fakeProductRepository, cartItemRepository = fakeCartItemRepository)

        // THEN

        viewModel.uiState.test {
            awaitItem()
            val state = awaitItem() as CartUiState.Success
            assertEquals(1, state.cartItems.size)
            assertEquals(6.0, state.summary?.subTotal)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `given quantity one when decrease quantity then remove item from cart`() = runTest(mainDispatcherRule.scheduler) {
        // GIVEN
        val productId = "p1"
        val p1 = product { withId(productId); withStock(5); withPrice(2.0) }
        val item = cartItem { withProductId(productId); withQuantity(1) }

        val fakeProductRepository = FakeProductRepository().apply { setProducts(listOf(p1)) }
        val fakeCartItemRepository = FakeCartItemRepository().apply { setCartItem(listOf(item)) }

        val viewModel = createViewModel(productRepository = fakeProductRepository, cartItemRepository = fakeCartItemRepository)

        // THEN
        viewModel.uiState.test {
            awaitItem()

            // WHEN
            viewModel.decreaseQuantity(productId, 1)

            val state = awaitItem() as CartUiState.Success
            assertTrue(state.cartItems.isEmpty())
            assertEquals(0.0, state.summary?.finalTotal)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `given insuffient stock when update quantity quantity then emits error event`() = runTest(mainDispatcherRule.scheduler) {
        // GIVEN
        val productId = "p1"
        val p1 = product { withId(productId); withStock(2) }
        val item = cartItem { withProductId(productId); withQuantity(1) }

        val fakeProductRepository = FakeProductRepository().apply { setProducts(listOf(p1)) }
        val fakeCartItemRepository = FakeCartItemRepository().apply { setCartItem(listOf(item)) }

        val viewModel = createViewModel(productRepository = fakeProductRepository, cartItemRepository = fakeCartItemRepository)

        // THEN
        viewModel.events.test {

            viewModel.increaseQuantity(productId, 5)

            val event = awaitItem()
            assertTrue(event is CartEvent.ShowMessage)
            cancelAndIgnoreRemainingEvents()
        }
    }

}