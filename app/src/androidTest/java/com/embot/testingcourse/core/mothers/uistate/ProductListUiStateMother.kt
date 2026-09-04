package com.embot.testingcourse.core.mothers.uistate

import com.embot.testingcourse.core.builders.product
import com.embot.testingcourse.core.mothers.ProductMother
import com.embot.testingcourse.core.mothers.PromotionMother
import com.embot.testingcourse.productList.domain.model.ProductWithPromotion
import com.embot.testingcourse.productList.domain.model.SortOption
import com.embot.testingcourse.productList.presentation.ProductListUiState

object ProductListUiStateMother {

    fun success(
        products: List<ProductWithPromotion> = listOf(
            ProductWithPromotion(product = ProductMother.coffee(), promotion = PromotionMother.percent()),
            ProductWithPromotion(product = ProductMother.bread()),
            ProductWithPromotion(product = ProductMother.milk()),
        ),
        categories: List<String> = listOf("dairy", "bread", "drinks"),
        selectedCategory: String? = null,
        sortOption: SortOption = SortOption.PRICE_ASC
    ) = ProductListUiState.Success(
        productList = products,
        categories = categories,
        selectedCategory = selectedCategory,
        sortOption = sortOption
    )

}