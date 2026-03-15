package com.embot.testingcourse.productList.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.embot.testingcourse.productList.presentation.component.FilterMenu
import com.embot.testingcourse.productList.presentation.component.HomeTopAppBar
import com.embot.testingcourse.productList.presentation.component.ProductItem


@Composable
fun ProductListScreen(
    productListViewModel: ProductListViewModel = hiltViewModel()
) {
    val uiState by productListViewModel.uiState.collectAsStateWithLifecycle()
    val filterVisible by productListViewModel.filterVisible.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        productListViewModel.events.collect { event ->
            when(event) {
                is ProductListEvent.ShowMessage -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    Scaffold(
        topBar = {
            HomeTopAppBar(
                filterVisible = filterVisible,
                onFilterClick = { showFilter -> productListViewModel.setFilterVisible(showFilter) },
                onSettingsClick = {}
            )
         },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        when(val state = uiState) {
            ProductListUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is ProductListUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize()
                        .padding(paddingValues),
                ) {
                    Text(
                        text = "Error",
                        fontSize = 24.sp
                    )
                }
            }
            is ProductListUiState.Success -> {
                Column(
                    modifier = Modifier.fillMaxSize()
                        .padding(paddingValues)
                ) {
                    AnimatedVisibility(
                        visible = filterVisible
                    ) {
                        FilterMenu(
                            state = state,
                            onCategorySelected = { category -> productListViewModel.setCategory(category) },
                            onSortSelected = { sortOption -> productListViewModel.setSortOption(sortOption) }
                        )
                    }
                    Text(
                        text = "${state.productList.size} products",
                        modifier = Modifier.padding(
                            horizontal = 16.dp,
                            vertical = 4.dp
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    if (state.productList.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize().padding(32.dp),
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
                        LazyColumn {
                            items(state.productList) { product ->
                                ProductItem(
                                    product = product,
                                    onClick = {}
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
    ProductListScreen()
}