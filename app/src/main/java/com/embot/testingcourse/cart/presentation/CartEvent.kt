package com.embot.testingcourse.cart.presentation

sealed interface CartEvent {
    data class ShowMessage(val message: String): CartEvent
}