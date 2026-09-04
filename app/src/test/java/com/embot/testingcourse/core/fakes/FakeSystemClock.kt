package com.embot.testingcourse.core.fakes

import com.embot.testingcourse.core.domain.utils.Clock
import java.time.Instant

class FakeSystemClock(
    private var currentTime: Instant = Instant.now()
): Clock {

    fun setTime(time: Instant) {
        this.currentTime = time
    }

    override fun now(): Instant = currentTime

    fun advanceTime(seconds: Long) {
        currentTime = currentTime.plusSeconds(seconds)
    }


}