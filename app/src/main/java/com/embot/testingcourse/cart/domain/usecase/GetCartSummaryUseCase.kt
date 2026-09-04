package com.embot.testingcourse.cart.domain.usecase

import com.embot.testingcourse.cart.domain.extensions.activeAt
import com.embot.testingcourse.cart.domain.model.CartItem
import com.embot.testingcourse.cart.domain.model.CartSummary
import com.embot.testingcourse.cart.domain.repository.CartItemRepository
import com.embot.testingcourse.core.domain.utils.Clock
import com.embot.testingcourse.productList.domain.model.Product
import com.embot.testingcourse.productList.domain.model.ProductPromotion
import com.embot.testingcourse.productList.domain.model.Promotion
import com.embot.testingcourse.productList.domain.repository.ProductRepository
import com.embot.testingcourse.productList.domain.repository.PromotionRepository
import com.embot.testingcourse.productList.domain.usecase.GetPromotionForProduct
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class GetCartSummaryUseCase @Inject constructor(
    private val cartItemRepository: CartItemRepository,
    private val productRepository: ProductRepository,
    private val promotionRepository: PromotionRepository,
    private val getPromotionForProduct: GetPromotionForProduct,
    private val clock: Clock
) {

    operator fun invoke(): Flow<CartSummary> {
        return cartItemRepository.getCartItems()
            .flatMapLatest { cartItems ->
                val ids = cartItems.mapTo(mutableSetOf()) { it.productId }
                if (ids.isEmpty()) {
                    flowOf(CartSummary(0.0, 0.0, 0.0))
                } else {
                    combine(
                        productRepository.getProductsByIds(ids),
                        promotionRepository.getActivePromotions()
                    ) { products, promotions ->
                        calculateSummary(cartItems, products, promotions, clock)
                    }
                }
            }
    }

    private fun calculateSummary(
        cartItems: List<CartItem>,
        products: List<Product>,
        promotions: List<Promotion>,
        clock: Clock
    ): CartSummary {
        val now = clock.now()

        val activePromotions = promotions.activeAt(now)

        val productsById = products.associateBy { it.id }
        var subTotal = 0.0
        var discountTotal = 0.0

        for (cartItem in cartItems) {
            val product = productsById[cartItem.productId] ?: continue
            val itemTotal = product.price * cartItem.quantity
            subTotal += itemTotal

            discountTotal += calculateDiscountForProdcut(
                product = product,
                quantity = cartItem.quantity,
                activePromotions = activePromotions
            )
        }

        val total = (subTotal - discountTotal).coerceAtLeast(0.0)

        return CartSummary(
            subTotal = subTotal,
            discountTotal = discountTotal,
            finalTotal = total
        )
    }

    private fun calculateDiscountForProdcut(
        product: Product,
        quantity: Int,
        activePromotions: List<Promotion>
    ): Double {
        return when(val selectedPromotion = getPromotionForProduct(product, activePromotions)) {
            is ProductPromotion.BuyXPayY -> {
                val buy = selectedPromotion.buy
                val pay = selectedPromotion.pay
                val freePerGroup = (buy - pay).coerceAtLeast(0)
                val groups = quantity / buy
                val freeItems = freePerGroup * groups
                product.price * freeItems
            }
            is ProductPromotion.Percent -> {
                val itemSubTotal = product.price * quantity
                itemSubTotal * (selectedPromotion.percent / 100)
            }
            null -> 0.0
        }

    }

}