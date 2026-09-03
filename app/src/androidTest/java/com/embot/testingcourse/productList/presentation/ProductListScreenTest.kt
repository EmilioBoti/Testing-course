package com.embot.testingcourse.productList.presentation

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.embot.testingcourse.core.mothers.ProductMother
import com.embot.testingcourse.core.mothers.uistate.ProductListUiStateMother
import com.embot.testingcourse.core.testing.UiTestTag
import com.embot.testingcourse.core.testing.UiTestTag.FILTER_VIEW
import com.embot.testingcourse.core.testing.UiTestTag.PRODUCT_LIST_LOADING
import com.embot.testingcourse.productList.domain.model.SortOption
import org.junit.Rule
import org.junit.Test

class ProductListScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()


    private fun createProductListScreen(
        uiState: ProductListUiState = ProductListUiStateMother.success(),
        cartItemCount: Int = 0,
        filterVisible: Boolean = true,
        onFilterClick: (Boolean) -> Unit = {},
        onCategorySelected: (String?) -> Unit = {},
        onSortSelected: (SortOption) -> Unit = {},
        navigateToSettings: () -> Unit = {},
        navigateToCart: () -> Unit = {},
        navigateToProductDetail: (String) -> Unit = {},
    ) {
        composeRule.setContent {
            ProductListContent(
                uiState = uiState,
                cartItemCount = cartItemCount,
                filterVisible = filterVisible,
                onFilterClick = onFilterClick,
                onCategorySelected = onCategorySelected,
                onSortSelected = onSortSelected,
                navigateToSettings = navigateToSettings,
                navigateToCart = navigateToCart,
                navigateToProductDetail = navigateToProductDetail
            )
        }
    }

    @Test
    fun givenLoadingState_whenRendered_thenShowsProgressView() {
        createProductListScreen(uiState = ProductListUiState.Loading)

        composeRule.onNodeWithTag(PRODUCT_LIST_LOADING).assertIsDisplayed()
    }

    @Test
    fun givenErrorState_whenRendered_thenShowsErrorMessage() {
        createProductListScreen(uiState = ProductListUiState.Error("something went wrong"))

        composeRule.onNodeWithText("Error").assertIsDisplayed()
    }

    @Test
    fun givenSuccessState_whenRendered_thenShowsProductsAndCount() {
        createProductListScreen(uiState = ProductListUiStateMother.success())

        composeRule.onNodeWithText("3 products").assertIsDisplayed()
        composeRule.onNodeWithTag(FILTER_VIEW).assertIsDisplayed()

        composeRule.onNodeWithTag(UiTestTag.productListItem(ProductMother.coffee().id)).assertIsDisplayed()
        composeRule.onNodeWithTag(UiTestTag.productListItem(ProductMother.bread().id)).assertIsDisplayed()
        composeRule.onNodeWithTag(UiTestTag.productListItem(ProductMother.milk().id)).assertIsDisplayed()


//        composeRule.onNodeWithTag(PRODUCT_LIST_LIST).performScrollToNode(hasTestTag(UiTestTag.productListItem("12356")))
//        composeRule.onNodeWithTag(PRODUCT_LIST_LIST).performScrollToIndex(6).assertIsDisplayed()
//        composeRule.onNodeWithTag(UiTestTag.productListItem("12356")).performScrollTo().assertIsDisplayed()
    }

    @Test
    fun givenSuccessState_whenRendered_thenShowsEmptyMessage() {
        createProductListScreen(uiState = ProductListUiStateMother.success(products = emptyList()))

        composeRule.onNodeWithText("Products not found").assertIsDisplayed()
    }


}