package com.embot.testingcourse.datail.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.embot.testingcourse.datail.domain.usecase.GetProductDetailWithPromotionUserCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
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
class ProductDetailViewModel @Inject constructor(
    private val getProductDetailWithPromotionUserCase: GetProductDetailWithPromotionUserCase
): ViewModel() {

    private val _uiState: MutableStateFlow<ProductDetailUistate> = MutableStateFlow(ProductDetailUistate())
    val uiState: StateFlow<ProductDetailUistate> = _uiState.asStateFlow()

    private val _events: MutableSharedFlow<ProductDetailEvent> = MutableSharedFlow(extraBufferCapacity = 1)
    val events: SharedFlow<ProductDetailEvent> = _events

    private var productJob: Job? = null

    fun loadProduct(productId: String) {
        _uiState.update { _uiState.value.copy(isLoading = true) }
        productJob?.cancel()

        productJob = getProductDetailWithPromotionUserCase(productId)
            .onEach { product ->
                _uiState.update {
                    _uiState.value.copy(
                        isLoading = false,
                        item = product
                    )
                }
            }
            .catch { error: Throwable ->
                _uiState.update { _uiState.value.copy(isLoading = false) }
                _events.emit(ProductDetailEvent.ShowError(error.message.orEmpty()))
            }
            .launchIn(viewModelScope)
    }

    fun addToCart() {

    }


}