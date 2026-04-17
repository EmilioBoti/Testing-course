package com.embot.testingcourse.cart.domain.usecase

import com.embot.testingcourse.cart.domain.extensions.activeAt
import com.embot.testingcourse.cart.domain.repository.CartItemRepository
import com.embot.testingcourse.cart.presentation.model.CartItemWithPromotion
import com.embot.testingcourse.core.domain.utils.Clock
import com.embot.testingcourse.productList.domain.model.ProductWithPromotion
import com.embot.testingcourse.productList.domain.repository.ProductRepository
import com.embot.testingcourse.productList.domain.repository.PromotionRepository
import com.embot.testingcourse.productList.domain.usecase.GetPromotionForProduct
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import java.time.Instant
import javax.inject.Inject
import kotlin.collections.emptyList

@OptIn(ExperimentalCoroutinesApi::class)
class GetCartItemWithPromotionUseCase @Inject constructor(
    private val cartItemRepository: CartItemRepository,
    private val productRepository: ProductRepository,
    private val promotionRepository: PromotionRepository,
    private val getPromotionForProduct: GetPromotionForProduct,
    private val clock: Clock
) {

    operator fun invoke(): Flow<List<CartItemWithPromotion>> {
        return cartItemRepository.getCartItems()
            .flatMapLatest { cartItems ->
                val ids = cartItems.mapTo(mutableSetOf()) { it.productId }

                if (ids.isEmpty()) {
                    flowOf(emptyList())
                } else {
                    combine(
                        productRepository.getProductsByIds(ids),
                        promotionRepository.getActivePromotions()
                    ) { products, promotions ->
                        val now = clock.now()

                        val activePromotions = promotions.activeAt(now)
                        val productsById = products.associateBy { it.id }

                        cartItems.mapNotNull { cartItem ->
                            val finalProduct = productsById[cartItem.productId] ?: return@mapNotNull null
                            val promotion = getPromotionForProduct(finalProduct, activePromotions)

                            CartItemWithPromotion(
                                cartItem = cartItem,
                                item = ProductWithPromotion(
                                    product = finalProduct,
                                    promotion = promotion
                                )
                            )
                        }
                    }
                }
            }
    }

}