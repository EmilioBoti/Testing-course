package com.embot.testingcourse.productList.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.embot.testingcourse.productList.domain.model.Product
import com.embot.testingcourse.productList.domain.usecase.GetProductsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase
): ViewModel() {

    private val _uiState: MutableStateFlow<ProdcutListUiState> = MutableStateFlow(ProdcutListUiState.Loading)
    val uiState: StateFlow<ProdcutListUiState> = _uiState.asStateFlow()

    private val _events: MutableSharedFlow<ProductListEvent> = MutableSharedFlow<ProductListEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<ProductListEvent> = _events

    init {
        loadProduct()
    }

    fun loadProduct() {
        _uiState.update { ProdcutListUiState.Loading }
        getProductsUseCase()
            .onEach<List<Product>> { products ->
                _uiState.value = ProdcutListUiState.Success(products)
            }
            .catch { e: Throwable ->
                _uiState.value = ProdcutListUiState.Error(e.message.orEmpty())
            }
            .launchIn(viewModelScope)
    }

}