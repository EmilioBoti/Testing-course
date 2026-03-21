package com.embot.testingcourse.datail.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.embot.testingcourse.cart.domain.usecase.AddToCartUserCase
import com.embot.testingcourse.core.domain.model.AppError
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
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val getProductDetailWithPromotionUserCase: GetProductDetailWithPromotionUserCase,
    private val addToCartUserCase: AddToCartUserCase
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
                if (error is AppError) {
                    handleError(error)
                } else {
                    handleError(AppError.UnKnownError(error.message))
                }
            }
            .launchIn(viewModelScope)
    }

    fun addToCart() {
        val productId = _uiState.value.item?.product?.id ?: return
        viewModelScope.launch {
            try {
                addToCartUserCase(productId)
                _events.emit(ProductDetailEvent.SuccessAddToCart)
            } catch (e: AppError) {
                handleError(e)
            } catch (e: Exception) {
                handleError(AppError.UnKnownError(e.message))
            }
        }
    }

    private suspend fun handleError(e: AppError) {
        val newEvent = when(e) {
            AppError.DatabaseError,
            AppError.NotFoundError,
            AppError.Validation.QuantityMustBePositive,
            is AppError.UnKnownError -> ProductDetailEvent.UnknownError
            AppError.NetworkError -> ProductDetailEvent.NetworkError
            is AppError.Validation.InsufficientStock -> ProductDetailEvent.InsuficientStockError
        }
        _events.emit(newEvent)
    }


}