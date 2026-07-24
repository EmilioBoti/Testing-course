package com.embot.testingcourse.core.utils

import app.cash.turbine.ReceiveTurbine

suspend fun <T>ReceiveTurbine<T>.awaitMatching(
        predicate: (T) -> Boolean
    ): T {
        while (true) {
            val item = awaitItem()
            if (predicate(item)) return item
        }
    }