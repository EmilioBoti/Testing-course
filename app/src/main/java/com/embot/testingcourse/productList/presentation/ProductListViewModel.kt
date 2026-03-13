package com.embot.testingcourse.productList.presentation

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ProductListViewModel @Inject constructor(): ViewModel() {

    private val _uiState: MutableStateFlow<ProdcutListUiState> = MutableStateFlow(ProdcutListUiState.Loading)
    val uiState: StateFlow<ProdcutListUiState> = _uiState.asStateFlow()

    private val _events: MutableSharedFlow<ProductListEvent> = MutableSharedFlow<ProductListEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<ProductListEvent> = _events

    init {
        loadProduct()
    }

    fun loadProduct() {
        _uiState.update { ProdcutListUiState.Loading }
    }

}