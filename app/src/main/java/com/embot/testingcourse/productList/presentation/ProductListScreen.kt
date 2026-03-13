package com.embot.testingcourse.productList.presentation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel


@Composable
fun ProductListScreen(
    productListViewModel: ProductListViewModel = hiltViewModel()
) {

}

@Composable
fun ProductListContent() {
    Text(
        text = "ProductList",
        fontSize = 30.sp
    )
}