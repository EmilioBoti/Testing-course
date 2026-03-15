package com.embot.testingcourse.productList.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.embot.testingcourse.productList.domain.model.SortOption
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
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase
): ViewModel() {

    private val _uiState: MutableStateFlow<ProductListUiState> = MutableStateFlow(ProductListUiState.Loading)
    val uiState: StateFlow<ProductListUiState> = _uiState.asStateFlow()

    private val _filterVisible: MutableStateFlow<Boolean> = MutableStateFlow(true)
    val filterVisible: StateFlow<Boolean> = _filterVisible.asStateFlow()

    private val _events: MutableSharedFlow<ProductListEvent> = MutableSharedFlow<ProductListEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<ProductListEvent> = _events

    init {
        loadProduct()
    }

    fun loadProduct() {
        _uiState.update { ProductListUiState.Loading }
        getProductsUseCase()
            .onEach { products ->
                val categories: List<String> = products.map { it.category }.distinct().sorted()
                _uiState.value = ProductListUiState.Success(
                    productList = products,
                    categories = categories,
                    selectedCategory = null,
                    sortOption = SortOption.NONE
                )
            }
            .catch { e: Throwable ->
                _uiState.value = ProductListUiState.Error(e.message.orEmpty())
            }
            .launchIn(viewModelScope)
    }

    fun setCategory(category: String?) {
        viewModelScope.launch {
            // call settingsRepository
        }
    }

    fun setSortOption(sortOption: SortOption) {

    }

    fun setFilterVisible(showFilter: Boolean) {
        _filterVisible.update { showFilter }
    }

}