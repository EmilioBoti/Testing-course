package com.embot.testingcourse.cart.presentation

import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.ReceiveTurbine
import app.cash.turbine.test
import com.embot.testingcourse.cart.domain.repository.CartItemRepository
import com.embot.testingcourse.cart.domain.usecase.GetCartItemWithPromotionUseCase
import com.embot.testingcourse.cart.domain.usecase.GetCartSummaryUseCase
import com.embot.testingcourse.cart.domain.usecase.UpdateCartItemUseCase
import com.embot.testingcourse.core.MainDispatcherRule
import com.embot.testingcourse.core.mockwebserver.MiniMarketApiDispatcher
import com.embot.testingcourse.core.mockwebserver.MockWebServerUrlHolder
import com.embot.testingcourse.core.mockwebserver.rules.MockWebServerRule
import com.embot.testingcourse.core.utils.asAsset
import com.embot.testingcourse.productList.domain.repository.ProductRepository
import com.embot.testingcourse.productList.domain.repository.PromotionRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class CartViewModelIntegrationTest {

    companion object {
        const val PRODUCT_ID = "p1"
        const val UPDATED_QUANTITY = 2
        const val INITIAL_QUANTITY = 1
    }

    @get:Rule(order = 0)
    val mockWebServerRule = MockWebServerRule()

    @get:Rule(order = 1)
    val hilt = HiltAndroidRule(this)

    @get:Rule(order = 2)
    val mainDispatcherRule = MainDispatcherRule()

    @Inject
    lateinit var cartItemRepository: CartItemRepository

    @Inject
    lateinit var productRepository: ProductRepository

    @Inject
    lateinit var promotionRepository: PromotionRepository

    @Inject
    lateinit var  getCartSummaryUseCase: GetCartSummaryUseCase

    @Inject
    lateinit var updateCartItemUseCase: UpdateCartItemUseCase

    @Inject
    lateinit var getCartItemWithPromotionUseCase: GetCartItemWithPromotionUseCase


    @Before
    fun setUp() = runTest {
        mockWebServerRule.server.dispatcher = MiniMarketApiDispatcher(
            productJson = "product_list_default.json".asAsset(),
        )
        hilt.inject()
        cartItemRepository.clearCart()
        productRepository.refreshPRoduct()
        promotionRepository.refreshPromotions()
    }

    @After
    fun tearDown() {
        MockWebServerUrlHolder.baseUrl = "http://localhost:8080/"
    }

    @Test
    fun givenCartWithItems_whenViewModelCollectsUiState_thenSuccessWithSummary() = runTest {
        cartItemRepository.addToCart(PRODUCT_ID, UPDATED_QUANTITY)
        
        val viewModel = createViewModel()

        viewModel.uiState.test {
//            val result = awaitMatching { state -> state is CartUiState.Success && state.summary != null && state.cartItems.isNotEmpty() }

            val result = awaitSuccessMatching { state -> state.summary != null && state.cartItems.isNotEmpty() }
            assertTrue(result.cartItems.isNotEmpty())
            assertTrue(result.summary != null)
            assertEquals(2.4, result.summary!!.subTotal, 0.01)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun givenSingleProduct_whenIncreaseQuantity_thenQuantityUpdated() = runTest {
        cartItemRepository.addToCart(PRODUCT_ID, INITIAL_QUANTITY)

        val viewModel = createViewModel()

        viewModel.uiState.test {
            val success = awaitSuccessMatching { state ->
                state.cartItems.any {
                    it.cartItem.productId == PRODUCT_ID && it.cartItem.quantity == INITIAL_QUANTITY
                }
            }

            assertEquals(INITIAL_QUANTITY, success.cartItems.first().cartItem.quantity)

            viewModel.increaseQuantity(PRODUCT_ID, INITIAL_QUANTITY)

            val updatedSuccess = awaitSuccessMatching { state ->
                state.cartItems.any {
                    it.cartItem.productId == PRODUCT_ID && it.cartItem.quantity == UPDATED_QUANTITY
                }
            }
            assertEquals(UPDATED_QUANTITY, updatedSuccess.cartItems.first().cartItem.quantity)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun givenSingleProduct_wheDecreaseToZero_thenCartBecomeEmpty() = runTest {
        cartItemRepository.addToCart(PRODUCT_ID, INITIAL_QUANTITY)

        val viewModel = createViewModel()

        viewModel.uiState.test {
            val success = awaitSuccessMatching { state ->
                state.cartItems.any {
                    it.cartItem.productId == PRODUCT_ID && it.cartItem.quantity == INITIAL_QUANTITY
                }
            }
            assertEquals(INITIAL_QUANTITY, success.cartItems.first().cartItem.quantity)

            viewModel.decreaseQuantity(PRODUCT_ID, INITIAL_QUANTITY)

            val emptySuccess = awaitSuccessMatching { state -> state.cartItems.isEmpty() }
            assertTrue(emptySuccess.cartItems.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun createViewModel(): CartViewModel {
        return CartViewModel(
            cartItemRepository = cartItemRepository,
            getCartSummaryUseCase = getCartSummaryUseCase,
            updateCartItemUseCase = updateCartItemUseCase,
            getCartItemWithPromotionUseCase = getCartItemWithPromotionUseCase
        )
    }

    private suspend fun ReceiveTurbine<CartUiState>.awaitSuccessMatching(
        predicate: (CartUiState.Success) -> Boolean
    ): CartUiState.Success {
        while (true) {
            when(val item = awaitItem()) {
                is CartUiState.Success -> if (predicate(item)) return item
                is CartUiState.Error -> error("Unexpected error: ${item.message}")
                CartUiState.Loading -> Unit
            }
        }
    }

}