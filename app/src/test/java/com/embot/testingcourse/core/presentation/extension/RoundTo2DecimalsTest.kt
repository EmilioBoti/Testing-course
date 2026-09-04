package com.embot.testingcourse.core.presentation.extension

import org.junit.Assert.*
import org.junit.Test

class DoubleExTest {


    @Test
    fun roundTo2Deciamls_roundtCorrectly() {
        assertEquals(4.66, 4.6578.roundTo2Decimals(), 0.0)
        assertEquals(1.01, 1.0148.roundTo2Decimals(), 0.0)
    }

}