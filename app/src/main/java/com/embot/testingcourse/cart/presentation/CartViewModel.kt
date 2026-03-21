package com.embot.testingcourse.cart.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.embot.testingcourse.cart.domain.repository.CartItemRepository
import com.embot.testingcourse.cart.domain.usecase.GetCartItemWithPromotionUseCase
import com.embot.testingcourse.cart.domain.usecase.GetCartSummaryUseCase
import com.embot.testingcourse.cart.domain.usecase.UpdateCartItemUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartItemRepository: CartItemRepository,
    private val getCartSummaryUseCase: GetCartSummaryUseCase,
    private val updateCartItemUseCase: UpdateCartItemUseCase,
    private val getCartItemWithPromotionUseCase: GetCartItemWithPromotionUseCase
) : ViewModel() {

    private val _uiState: MutableStateFlow<CartUiState> = MutableStateFlow(CartUiState.Loading)
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    private val _events: MutableSharedFlow<CartEvent> = MutableSharedFlow(extraBufferCapacity = 1)
    val events: SharedFlow<CartEvent> = _events

    private var cartJob: Job? = null

    init {
        loadCart()
    }

    fun loadCart() {
        _uiState.update { CartUiState.Loading }
        cartJob?.cancel()

        cartJob = combine(
            getCartItemWithPromotionUseCase(),
            getCartSummaryUseCase()
        ) { cartItemWithPromotion, summary ->
            _uiState.update {
                CartUiState.Success(
                    summary = summary,
                    cartItems = cartItemWithPromotion,
                    isLoading = false
                )
            }
        }.catch { e: Throwable ->
            _uiState.update { CartUiState.Error(e.message.orEmpty()) }
        }.launchIn(viewModelScope)
    }

    private fun updateCartItem(productId: String, quantity: Int) {
        viewModelScope.launch {
            try {
                updateCartItemUseCase(productId, quantity)
            } catch (e: Exception) {
                _events.emit(CartEvent.ShowMessage(e.message.orEmpty()))
            }
        }
    }

    fun removeFromCart(productId: String) {
        viewModelScope.launch {
            try {
                cartItemRepository.removeFromCart(productId)
            } catch (e: Exception) {
                _events.emit(CartEvent.ShowMessage(e.message.orEmpty()))
            }
        }
    }

    fun increaseQuantity(productId: String, currentQuantity: Int) {
        updateCartItem(productId, currentQuantity + 1)
    }

    fun decreaseQuantity(productId: String, currentQuantity: Int) {
        if (currentQuantity > 1) {
            updateCartItem(productId, currentQuantity - 1)
        } else {
            removeFromCart(productId)
        }
    }

}