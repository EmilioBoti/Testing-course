package com.embot.testingcourse.core.data

import com.embot.testingcourse.core.domain.utils.Clock
import java.time.Instant
import javax.inject.Inject

class SystemClock @Inject constructor(): Clock {
    override fun now(): Instant = Instant.now()
}