package com.embot.testingcourse.cart.presentation

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeRight
import com.embot.testingcourse.core.mothers.ProductMother
import com.embot.testingcourse.core.mothers.uistate.CartUiStateMother
import com.embot.testingcourse.core.mothers.uistate.CartUiStateMother.cartItemWithPromotion
import com.embot.testingcourse.core.testing.UiTestTag
import com.embot.testingcourse.core.testing.UiTestTag.CART_EMPTY
import com.embot.testingcourse.core.testing.UiTestTag.CART_LOADING
import com.embot.testingcourse.core.testing.UiTestTag.CART_RETRY_BUTTON
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class CartScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private fun createCartScreen(
        uiState: CartUiState = CartUiStateMother.success(),
        onRefresh: () -> Unit = {},
        onBack: () -> Unit = {},
        onDecreaseQuantity: (String, Int) -> Unit = { _, _ -> },
        onIncreaseQuantity: (String, Int) -> Unit = { _, _ -> },
        onRemove: (String) -> Unit = {},
    ) {
        composeRule.setContent {
            CartContentScreen(
                uiState = uiState,
                onRefresh = onRefresh,
                onBack = onBack,
                onDecreaseQuantity = onDecreaseQuantity,
                onIncreaseQuantity = onIncreaseQuantity,
                onRemove = onRemove,
            )
        }
    }

    @Test
    fun givenLoadingState_whenRendered_thenShowProgressView() {
        createCartScreen(uiState = CartUiState.Loading)

        composeRule.onNodeWithTag(CART_LOADING).assertIsDisplayed()
    }

    @Test
    fun givenErrorState_whenRendered_thenShowErrorMessage() {
        val errorText = "something went wrong"
        createCartScreen(uiState = CartUiState.Error(message = errorText))

        composeRule.onNodeWithText("Error: $errorText").assertIsDisplayed()
        composeRule.onNodeWithTag(CART_RETRY_BUTTON).assertIsDisplayed()
    }

    @Test
    fun givenErrorState_whenRetryClicked_thenEmitsRetryCallback() {
        var retryClicked = false

        createCartScreen(uiState = CartUiState.Error(message = "something went wrong"), onRefresh = { retryClicked = true })

        composeRule.onNodeWithTag(CART_RETRY_BUTTON).performClick()

        assertEquals(true, retryClicked)
    }

    @Test
    fun givenEmptySuccessState_whenRendered_thenShowEmptyCartMessage() {
        createCartScreen(uiState = CartUiState.Success(summary = null, cartItems = listOf(), isLoading = false))

        composeRule.onNodeWithTag(CART_EMPTY).assertIsDisplayed()
        composeRule.onNodeWithText("🛒").assertIsDisplayed()
        composeRule.onNodeWithText("Add products").assertIsDisplayed()
    }

    @Test
    fun givenSuccessState_whenRendered_thenShowsItemsQuantityAndSummary() {
        createCartScreen(uiState = CartUiStateMother.success())

        composeRule.onNodeWithText(ProductMother.coffee().name).assertIsDisplayed()
        composeRule.onNodeWithText(ProductMother.bread().name).assertIsDisplayed()
        composeRule.onNodeWithText("SubTotal").assertIsDisplayed()
        composeRule.onNodeWithText("Discount").assertIsDisplayed()
        composeRule.onNodeWithText("Total").assertIsDisplayed()

        composeRule.onNodeWithTag(UiTestTag.cartItem(ProductMother.coffee().id)).assertIsDisplayed()
        composeRule.onNodeWithTag(UiTestTag.cartItem(ProductMother.bread().id)).assertIsDisplayed()
    }

    @Test
    fun givenInitialQuantity_whenIncreaseClicked_thenEmitsIncreaseQuantity() {
        var emitted: Pair<String, Int>? = null
        val initialQuantity = 2

        createCartScreen(
            uiState = CartUiStateMother.success(
                cartItems = listOf(
                    cartItemWithPromotion(
                        product = ProductMother.bread(),
                        quantity = initialQuantity
                    )
                )
            ),
            onIncreaseQuantity = { productId, quantity -> emitted = productId to quantity }
        )

        composeRule.onNodeWithTag(UiTestTag.cartQuantityIncrease(ProductMother.bread().id))
            .assertIsEnabled()
            .performClick()

        assertEquals(ProductMother.bread().id to initialQuantity, emitted)
    }

    @Test
    fun givenInitialQuantity_whenDecreaseClicked_thenEmitsDecreaseQuantity() {
        var emitted: Pair<String, Int>? = null
        val initialQuantity = 3

        createCartScreen(
            uiState = CartUiStateMother.success(
                cartItems = listOf(
                    cartItemWithPromotion(
                        product = ProductMother.bread(),
                        quantity = initialQuantity
                    )
                )
            ),
            onDecreaseQuantity = { productId, quantity -> emitted = productId to quantity }
        )

        composeRule.onNodeWithTag(UiTestTag.cartQuantityDecrease(ProductMother.bread().id))
            .assertIsEnabled()
            .performClick()

        assertEquals(ProductMother.bread().id to initialQuantity, emitted)
    }

    @Test
    fun givenCartItem_whenSwipeRight_thenEmitsRemoveCallback() {
        var removeProductId: String? = null

        createCartScreen(
            uiState = CartUiStateMother.success(
                cartItems = listOf(
                    cartItemWithPromotion(
                        product = ProductMother.bread(),
                        quantity = 3
                    )
                )
            ),
            onRemove = { id -> removeProductId = id },
        )

        composeRule.onNodeWithTag(UiTestTag.cartItem(ProductMother.bread().id))
            .assertIsEnabled()
            .performTouchInput { swipeRight() }

        composeRule.waitUntil(timeoutMillis = 3000) { removeProductId != null }

        assertEquals(ProductMother.bread().id, removeProductId)
    }

    @Test
    fun givenItemsAtStockEdges_whenRendered_thenInvalidControlAreDisable() {
        val fulStockWithPromotion = cartItemWithPromotion(
            product = ProductMother.bread(7),
            quantity = 7
        )
        createCartScreen(
            uiState = CartUiStateMother.success(
                cartItems = listOf(fulStockWithPromotion)
            )
        )

        composeRule.onNodeWithTag(UiTestTag.cartQuantityIncrease(ProductMother.bread().id))
            .assertIsNotEnabled()
            .performClick()

    }

}