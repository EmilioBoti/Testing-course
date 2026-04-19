package com.embot.testingcourse.datail.domain.usecase

import com.embot.testingcourse.cart.domain.extensions.activeAt
import com.embot.testingcourse.core.domain.utils.Clock
import com.embot.testingcourse.productList.domain.model.ProductWithPromotion
import com.embot.testingcourse.productList.domain.repository.ProductRepository
import com.embot.testingcourse.productList.domain.repository.PromotionRepository
import com.embot.testingcourse.productList.domain.usecase.GetPromotionForProduct
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.Instant
import javax.inject.Inject

class GetProductDetailWithPromotionUserCase @Inject constructor(
    private val productRepository: ProductRepository,
    private val promotionRepository: PromotionRepository,
    private val getPromotionForProduct: GetPromotionForProduct,
    private val clock: Clock
) {

    operator fun invoke(productId: String): Flow<ProductWithPromotion?> {
        return combine(
            productRepository.getProductById(productId),
            promotionRepository.getActivePromotions()
        ) { product, promotions ->
            val now = clock.now()
            val activePromotions = promotions.activeAt(now)
            product?.let {
                val filnalPromotion = getPromotionForProduct(product, activePromotions)
                ProductWithPromotion(
                    product = it,
                    promotion = filnalPromotion
                )
            }
        }
    }

}