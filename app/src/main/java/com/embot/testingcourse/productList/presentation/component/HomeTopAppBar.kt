package com.embot.testingcourse.productList.presentation.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopAppBar(
    filterVisible: Boolean = true,
    onFilterClick: (Boolean) -> Unit,
    onShoppingCartClick: () -> Unit,
    onSettingsClick: () -> Unit,
) {
    TopAppBar(
        title = {
            Text(
                text = "MiniMarket",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        actions = {
            IconButton(
                onClick = { onFilterClick(!filterVisible) }
            ) {
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = if (filterVisible) "Hide filters" else "Show filters",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            IconButton(
                onClick = { onSettingsClick() }
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            IconButton(
                onClick = { onShoppingCartClick() }
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = "Cart",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    )
}