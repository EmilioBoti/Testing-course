package com.embot.testingcourse.datail.presentation

sealed interface ProductDetailEvent {
    data object UNKNOWN_ERROR: ProductDetailEvent
    data object NETWORK_ERROR: ProductDetailEvent
    data object INSUFICIENT_STOCK_ERROR: ProductDetailEvent
}