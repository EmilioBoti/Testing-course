package com.embot.testingcourse.core.presentation.navigation.extension

import kotlin.math.roundToInt

fun Double.roundTo2Decimals(): Double {
    return (this * 100).roundToInt() / 100.0
}