package com.embot.testingcourse.cart.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.embot.testingcourse.cart.domain.repository.CartItemRepository
import com.embot.testingcourse.cart.domain.usecase.GetCartSummaryUseCase
import com.embot.testingcourse.cart.domain.usecase.UpdateCartItemUseCase
import com.embot.testingcourse.cart.presentation.model.CartItemWithPromotion
import com.embot.testingcourse.productList.domain.repository.ProductRepository
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
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartItemRepository: CartItemRepository,
    private val productRepository: ProductRepository,
    private val getCartSummaryUseCase: GetCartSummaryUseCase,
    private val updateCartItemUseCase: UpdateCartItemUseCase
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

        cartJob = cartItemRepository.getCartItems()
            .flatMapLatest { cartItems ->
                val ids = cartItems.mapTo(mutableSetOf()) { it.productId }
                if (ids.isEmpty()) {
                    getCartSummaryUseCase().map { summary ->
                        _uiState.update {
                            CartUiState.Success(
                                summary = summary,
                                cartItems = emptyList(),
                                isLoading = false
                            )
                        }
                    }
                } else {
                    combine(
                        productRepository.getProductsByIds(ids),
                        getCartSummaryUseCase()
                    ) { products, summary ->
                        val productsById = products.associateBy { it.id }
                        val cartItemsWithRpoducts = cartItems.mapNotNull { cartItem ->
                            val finalProduct = productsById[cartItem.productId] ?: return@mapNotNull null
                            CartItemWithPromotion(
                                product = finalProduct,
                                cartItem = cartItem
                            )
                        }
                        _uiState.update {
                            CartUiState.Success(
                                summary = summary,
                                cartItems = cartItemsWithRpoducts,
                                isLoading = false
                            )
                        }
                    }
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