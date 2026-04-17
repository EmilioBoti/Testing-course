package com.embot.testingcourse.core.domain.utils

import java.time.Instant

interface Clock {

    fun now(): Instant

}