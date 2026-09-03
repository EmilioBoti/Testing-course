package com.embot.testingcourse.cart.presentation

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.embot.testingcourse.core.mothers.uistate.CartUiStateMother
import com.embot.testingcourse.core.testing.UiTestTag.CART_EMPTY
import com.embot.testingcourse.core.testing.UiTestTag.CART_ERROR_MESSAGE
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

}