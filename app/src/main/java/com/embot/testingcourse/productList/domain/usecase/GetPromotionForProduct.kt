package com.embot.testingcourse.productList.domain.usecase

import com.embot.testingcourse.core.presentation.extension.roundTo2Decimals
import com.embot.testingcourse.productList.domain.model.Product
import com.embot.testingcourse.productList.domain.model.ProductPromotion
import com.embot.testingcourse.productList.domain.model.Promotion
import com.embot.testingcourse.productList.domain.model.PromotionType
import javax.inject.Inject

class GetPromotionForProduct @Inject constructor() {

    operator fun invoke(product: Product, promotions: List<Promotion>): ProductPromotion? {
        val productPromotions = promotions.filter { it.productIds.contains(product.id) }

        val percentPromo = productPromotions.filter { it.type === PromotionType.PERCENT }
            .maxByOrNull { it.value }

        if (percentPromo != null) {
            val percent = percentPromo.value.coerceIn(0.0, 100.0)
            val discountPrice = (product.price * (1 - percent / 100)).roundTo2Decimals()
            return ProductPromotion.Percent(
                percent = percent,
                discountPrice = discountPrice
            )
        }

        val buyPayPromo = productPromotions.firstOrNull { it.type === PromotionType.BUY_X_PAY_Y }

        if (buyPayPromo != null) {
            val buy = buyPayPromo.buyQuantity ?: return null
            val pay = buyPayPromo.value.toInt().coerceIn(0, buy)
            return ProductPromotion.BuyXPayY(
                buy = buy,
                pay = pay,
                label = "${buy}x${pay}"
            )
        }

        return null
    }

}