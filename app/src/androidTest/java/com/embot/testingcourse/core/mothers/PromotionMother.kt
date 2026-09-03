package com.embot.testingcourse.core.mothers

import com.embot.testingcourse.productList.domain.model.ProductPromotion

object PromotionMother {


    fun percent(
        percent: Double = 25.0, discountPrice: Double = 4.65
    ) = ProductPromotion.Percent(percent = percent, discountPrice = discountPrice)

}