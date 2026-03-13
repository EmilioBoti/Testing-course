package com.embot.testingcourse.productList.presentation

sealed interface ProductListEvent {
    data class ShowMessage(val message: String): ProductListEvent
}