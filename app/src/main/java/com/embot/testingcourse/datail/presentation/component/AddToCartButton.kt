package com.embot.testingcourse.datail.presentation.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.embot.testingcourse.productList.domain.model.Product

@Composable
fun AddToCartButton(
    modifier: Modifier = Modifier,
    product: Product?,
    isLoading: Boolean,
    addToCart: () -> Unit
) {
    product?.let {
        if (it.stock > 0) {
            AddToCartButtonInStock(modifier, product, isLoading, addToCart)
        } else {
            AddToCartButtonNoStock(modifier)
        }
    }
}