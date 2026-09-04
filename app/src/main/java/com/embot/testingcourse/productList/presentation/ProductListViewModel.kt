package com.embot.testingcourse.productList.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.embot.testingcourse.productList.domain.model.ProductPromotion
import com.embot.testingcourse.productList.domain.model.ProductWithPromotion
import com.embot.testingcourse.productList.domain.model.SortOption
import com.embot.testingcourse.productList.domain.repository.SettingsRepository
import com.embot.testingcourse.productList.domain.usecase.GetProductsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val uiState: StateFlow<ProductListUiState> = combine(
        getProductsUseCase(),
        settingsRepository.selectedCategory,
        settingsRepository.sortOption
    ) { products, category, sortOption ->
        var filteredProduct = products

        if (category != null) {
            filteredProduct = filteredProduct.filter { it.product.category == category }
        }

        val sortedProduct = when (sortOption) {
            SortOption.NONE -> filteredProduct
            SortOption.PRICE_ASC -> filteredProduct.sortedBy { effectivePrice(it) }
            SortOption.PRICE_DESC -> filteredProduct.sortedByDescending { effectivePrice(it) }
            SortOption.DISCOUNT ->
                filteredProduct.sortedWith(
                    comparator = compareByDescending<ProductWithPromotion> {
                        effectiveDiscountPercent(it)
                    }.thenBy { it.promotion == null }
                )
        }

        val categories: List<String> = products.map { it.product.category }.distinct().sorted()

        ProductListUiState.Success(
            productList = sortedProduct,
            categories = categories,
            selectedCategory = category,
            sortOption = sortOption
        ) as ProductListUiState
    }.catch { e: Throwable ->
        emit(ProductListUiState.Error(e.message.orEmpty()))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ProductListUiState.Loading
    )

    val filterVisible: StateFlow<Boolean> = settingsRepository.filterVisible.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = true,
    )

    private val _events: MutableSharedFlow<ProductListEvent> = MutableSharedFlow(extraBufferCapacity = 1)
    val events: SharedFlow<ProductListEvent> = _events

    fun setCategory(category: String?) {
        viewModelScope.launch {
            settingsRepository.setSelectedCategory(category)
        }
    }

    fun setSortOption(sortOption: SortOption) {
        viewModelScope.launch {
            settingsRepository.setSortOption(sortOption)
        }
    }

    fun setFilterVisible(showFilter: Boolean) {
        viewModelScope.launch {
            settingsRepository.setFilterVisible(showFilter)
        }
    }

    private fun effectivePrice(item: ProductWithPromotion): Double {
        return when (val pormo = item.promotion) {
            is ProductPromotion.Percent -> pormo.discountPrice
            else -> item.product.price
        }
    }

    private fun effectiveDiscountPercent(item: ProductWithPromotion): Double {
        return when (val promo = item.promotion) {
            is ProductPromotion.Percent -> promo.percent
            else -> 0.0
        }
    }


}