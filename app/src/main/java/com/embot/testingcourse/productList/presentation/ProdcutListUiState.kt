package com.embot.testingcourse.productList.presentation

import com.embot.testingcourse.productList.domain.model.Product

sealed class ProdcutListUiState {
    data object Loading: ProdcutListUiState()
    data class Error(val message: String): ProdcutListUiState()
    data class Success(
        val productList: List<Product>,
//        val category: List<String>,
//        val selectedCategory: String
    ): ProdcutListUiState()
}