package com.embot.testingcourse.cart.domain.extensions

import com.embot.testingcourse.core.builders.promotion
import com.embot.testingcourse.productList.domain.model.Promotion
import org.junit.Assert.*
import org.junit.Test
import java.time.Instant

class PromotionExtensionsTest {

    private val now = Instant.parse("2026-04-03T10:00:00Z")

    @Test
    fun give_future_promotion_when_activeAt_then_exclude() {
        // GIVEN
        val futurePromotion = promotion {
            withStartTime(now.plusSeconds(1))
            withEndTime(now.plusSeconds(120))
        }
        val promotions = listOf(futurePromotion)

        // WHEN
        val result = promotions.activeAt(now)

        // THEN
        assertEquals(0, result.size)
    }

    @Test
    fun give_expired_promotion_when_activeAt_then_exclude() {
        // GIVEN
        val expiredPromotion = promotion {
            withStartTime(now.minusSeconds(120))
            withEndTime(now.minusSeconds(5))
        }
        val promotions = listOf(expiredPromotion)

        // WHEN
        val result = promotions.activeAt(now)

        // THEN
        assertEquals(0, result.size)
    }


    @Test
    fun give_ongoing_promotion_when_activeAt_then_include() {
        // GIVEN
        val onGoingPromotion = promotion {
            withStartTime(now.minusSeconds(1))
            withEndTime(now.plusSeconds(1))
        }
        val promotions = listOf(onGoingPromotion)

        // WHEN
        val result = promotions.activeAt(now)

        // THEN
        assertEquals(1, result.size)
    }

    @Test
    fun give_exact_startTime_promotion_when_activeAt_then_exclude() {
        // GIVEN
        val exactStartTimePromotion = promotion {
            withStartTime(now)
            withEndTime(now.plusSeconds(15))
        }
        val promotions = listOf(exactStartTimePromotion)

        // WHEN
        val result = promotions.activeAt(now)

        // THEN
        assertEquals(1, result.size)
    }

    @Test
    fun give_exact_EndTime_promotion_when_activeAt_then_exclude() {
        // GIVEN
        val exactStartTimePromotion = promotion {
            withStartTime(now.minusSeconds(15))
            withEndTime(now)
        }
        val promotions = listOf(exactStartTimePromotion)

        // WHEN
        val result = promotions.activeAt(now)

        // THEN
        assertEquals(1, result.size)
    }

    @Test
    fun give_empty_list_when_activeAt_then_returns_empty() {
        // GIVEN
        val promotions = emptyList<Promotion>()

        // WHEN
        val result = promotions.activeAt(now)

        // THEN
        assertEquals(0, result.size)
    }

}