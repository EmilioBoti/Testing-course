package com.embot.testingcourse.productList.domain.usecase

import com.embot.testingcourse.productList.domain.model.ProductWithPromotion
import com.embot.testingcourse.productList.domain.repository.ProductRepository
import com.embot.testingcourse.productList.domain.repository.PromotionRepository
import com.embot.testingcourse.productList.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.Instant
import javax.inject.Inject

class GetProductsUseCase @Inject constructor(
    private val productRepository: ProductRepository,
    private val promotionRepository: PromotionRepository,
    private val getPromotionForProduct: GetPromotionForProduct,
    private val settingsRepository: SettingsRepository
) {

    operator  fun invoke(): Flow<List<ProductWithPromotion>> {
        return combine(
            productRepository.getProduct(),
            promotionRepository.getActivePromotions(),
            settingsRepository.inStockOnly
        ) { products, promotions, inStockOnly ->
            val now = Instant.now()

            val activePromotions = promotions.filter {
                it.startTime <= now && it.endTime >= now
            }

            val filteredProduct = if (inStockOnly) {
                products.filter { product -> product.stock >= 0 }
            } else {
                products
            }

            filteredProduct.map { product ->
                val promotion = getPromotionForProduct(product, activePromotions)
                ProductWithPromotion(
                    product =   product,
                    promotion = promotion
                )
            }
        }
    }
}