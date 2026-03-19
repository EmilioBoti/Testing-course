package com.embot.testingcourse.datail.presentation

sealed interface ProductDetailEvent {
    data class ShowMessage(
        val msg: String
    ): ProductDetailEvent
    data class ShowError(
        val msg: String
    ): ProductDetailEvent
}