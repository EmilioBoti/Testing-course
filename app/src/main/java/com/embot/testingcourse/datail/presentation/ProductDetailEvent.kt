package com.embot.testingcourse.datail.presentation

sealed interface ProductDetailEvent {
    data object SuccessAddToCart: ProductDetailEvent
    data object UnknownError: ProductDetailEvent
    data object NetworkError: ProductDetailEvent
    data object InsuficientStockError: ProductDetailEvent
}