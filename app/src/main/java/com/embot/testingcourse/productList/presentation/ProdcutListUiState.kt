package com.embot.testingcourse.productList.presentation

sealed class ProdcutListUiState {
    data object Loading: ProdcutListUiState()
    data class Error(val message: String): ProdcutListUiState()
    data class Success(
//        val productList: List<String>,
//        val category: List<String>,
        val selectedCategory: String
    ): ProdcutListUiState()
}