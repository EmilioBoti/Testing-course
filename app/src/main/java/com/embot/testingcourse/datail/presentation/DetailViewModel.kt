package com.embot.testingcourse.datail.presentation

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(): ViewModel() {

    private val _uiState: MutableStateFlow<ProductDetailUistate> = MutableStateFlow(ProductDetailUistate())
    val uiState: StateFlow<ProductDetailUistate> = _uiState.asStateFlow()

    private val _events: MutableSharedFlow<ProductDetailEvent> = MutableSharedFlow(extraBufferCapacity = 1)
    val events: SharedFlow<ProductDetailEvent> = _events


    fun loadProduct(productId: String) {

    }

    fun addToCart() {
        
    }


}