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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartItemRepository: CartItemRepository,
    getCartSummaryUseCase: GetCartSummaryUseCase,
    private val updateCartItemUseCase: UpdateCartItemUseCase,
    getCartItemWithPromotionUseCase: GetCartItemWithPromotionUseCase
) : ViewModel() {

    private val refreshTriger: MutableSharedFlow<Unit> = MutableSharedFlow(extraBufferCapacity = 1)
    val uiState: StateFlow<CartUiState> = combine(
        refreshTriger.onStart { emit(Unit) },
        getCartItemWithPromotionUseCase(),
        getCartSummaryUseCase(),
    ) { _, cartItemWithPromotion, summary ->
        CartUiState.Success(
            summary = summary,
            cartItems = cartItemWithPromotion,
            isLoading = false
        ) as CartUiState
    }.catch { e: Throwable ->
        this.emit(CartUiState.Error(e.message.orEmpty()))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CartUiState.Loading
    )

    private val _events: MutableSharedFlow<CartEvent> = MutableSharedFlow(extraBufferCapacity = 1)
    val events: SharedFlow<CartEvent> = _events

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

    fun refresh() {
        refreshTriger.tryEmit(Unit)
    }

}