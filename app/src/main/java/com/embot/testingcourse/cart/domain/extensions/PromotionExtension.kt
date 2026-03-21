package com.embot.testingcourse.cart.domain.extensions

import com.embot.testingcourse.productList.domain.model.Promotion
import java.time.Instant


fun List<Promotion>.activeAt(now: Instant): List<Promotion> {
    return this.filter { it.startTime <= now && it.endTime >= now }
}