package com.embot.testingcourse.productList.presentation

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.embot.testingcourse.productList.domain.model.SortOption
import org.junit.Assert.*
import org.junit.Rule

class ProductListScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()


    private fun createProductListScreen(
        uiState: ProductListUiState = ProductListUiState.Loading,
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


}