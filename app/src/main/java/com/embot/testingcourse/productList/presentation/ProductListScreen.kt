package com.embot.testingcourse.productList.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.embot.testingcourse.cart.presentation.CartUiState
import com.embot.testingcourse.cart.presentation.CartViewModel
import com.embot.testingcourse.core.testing.UiTestTag.PRODUCT_LIST_LIST
import com.embot.testingcourse.core.testing.UiTestTag.PRODUCT_LIST_LOADING
import com.embot.testingcourse.productList.domain.model.ProductWithPromotion
import com.embot.testingcourse.productList.domain.model.SortOption
import com.embot.testingcourse.productList.presentation.component.FilterMenu
import com.embot.testingcourse.productList.presentation.component.HomeTopAppBar
import com.embot.testingcourse.productList.presentation.component.ProductItem


@Composable
fun ProductListScreen(
    productListViewModel: ProductListViewModel = hiltViewModel(),
    cartViewModel: CartViewModel = hiltViewModel(),
    navigateToSettings: () -> Unit,
    navigateToCart: () -> Unit,
    navigateToProductDetail: (String) -> Unit,
) {
    val uiState by productListViewModel.uiState.collectAsStateWithLifecycle()
    val filterVisible by productListViewModel.filterVisible.collectAsStateWithLifecycle()
    val cartUiState by cartViewModel.uiState.collectAsStateWithLifecycle()

    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        productListViewModel.events.collect { event ->
            when(event) {
                is ProductListEvent.ShowMessage -> snackBarHostState.showSnackbar(event.message)
            }
        }
    }


    val cartItemCount = remember(cartUiState) {
        when(val state = cartUiState) {
            is CartUiState.Success -> state.cartItems.sumOf { it.cartItem.quantity }
            else -> 0
        }
    }

    ProductListContent(
        snackBarHostState = snackBarHostState,
        uiState = uiState,
        filterVisible = filterVisible,
        cartItemCount = cartItemCount,
        onFilterClick = productListViewModel::setFilterVisible,
        onCategorySelected = productListViewModel::setCategory,
        onSortSelected = productListViewModel::setSortOption,
        navigateToSettings = navigateToSettings,
        navigateToCart = navigateToCart,
        navigateToProductDetail = navigateToProductDetail,
    )

}

@Composable
fun ProductListContent(
    snackBarHostState: SnackbarHostState = remember { SnackbarHostState() },
    uiState: ProductListUiState,
    cartItemCount: Int,
    filterVisible: Boolean,
    onFilterClick: (Boolean) -> Unit = {},
    onCategorySelected: (String?) -> Unit = {},
    onSortSelected: (SortOption) -> Unit = {},
    navigateToSettings: () -> Unit = {},
    navigateToCart: () -> Unit = {},
    navigateToProductDetail: (String) -> Unit = {},
) {
    Scaffold(
        topBar = {
            HomeTopAppBar(
                filterVisible = filterVisible,
                cartItemCount = cartItemCount,
                onFilterClick = onFilterClick,
                onShoppingCartClick = navigateToCart,
                onSettingsClick = navigateToSettings
            )
        },
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) { paddingValues ->
        when(uiState) {
            ProductListUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier.testTag(PRODUCT_LIST_LOADING))
                }
            }
            is ProductListUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                ) {
                    Text(
                        text = "Error",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            is ProductListUiState.Success -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    AnimatedVisibility(
                        visible = filterVisible
                    ) {
                        FilterMenu(
                            state = uiState,
                            onCategorySelected = onCategorySelected,
                            onSortSelected = onSortSelected
                        )
                    }
                    Text(
                        text = "${uiState.productList.size} products",
                        modifier = Modifier.padding(
                            horizontal = 16.dp,
                            vertical = 4.dp
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    if (uiState.productList.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = "🔍",
                                    style = MaterialTheme.typography.displayMedium
                                )
                                Text(
                                    text = "Products not found",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.tertiary
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.testTag(PRODUCT_LIST_LIST)
                        ) {
                            items(uiState.productList) { item: ProductWithPromotion ->
                                ProductItem(
                                    item = item,
                                    onClick = { item -> navigateToProductDetail(item.product.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProductListScreenPreview() {
    val snackBarHostState = remember { SnackbarHostState() }
    ProductListContent(
        snackBarHostState = snackBarHostState,
        uiState = ProductListUiState.Success(
            productList = emptyList(),
            categories = emptyList(),
            selectedCategory = null,
            sortOption = SortOption.PRICE_ASC
        ),
        cartItemCount = 4,
        filterVisible = false,
    )
}