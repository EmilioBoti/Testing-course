package com.embot.testingcourse.datail.presentation

import com.embot.testingcourse.productList.domain.model.ProductWithPromotion

data class ProductDetailUistate(
    val item: ProductWithPromotion? = null,
    val isLoading: Boolean = true
)