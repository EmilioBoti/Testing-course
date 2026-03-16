package com.embot.testingcourse.productList.presentation

import com.embot.testingcourse.productList.domain.model.ProductWithPromotion
import com.embot.testingcourse.productList.domain.model.SortOption

sealed class ProductListUiState {
    data object Loading: ProductListUiState()
    data class Error(val message: String): ProductListUiState()
    data class Success(
        val productList: List<ProductWithPromotion>,
        val categories: List<String>,
        val selectedCategory: String?,
        val sortOption: SortOption
    ): ProductListUiState()
}