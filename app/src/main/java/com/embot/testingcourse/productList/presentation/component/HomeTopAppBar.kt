package com.embot.testingcourse.productList.presentation.component

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.embot.testingcourse.core.testing.UiTestTag.TOP_APP_BAR_BADGE
import com.embot.testingcourse.core.testing.UiTestTag.TOP_APP_BAR_BADGE_FILTER
import com.embot.testingcourse.core.testing.UiTestTag.TOP_APP_BAR_CART
import com.embot.testingcourse.core.testing.UiTestTag.TOP_APP_BAR_SETTINGS

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopAppBar(
    filterVisible: Boolean = true,
    cartItemCount: Int,
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
                modifier = Modifier.testTag(TOP_APP_BAR_BADGE_FILTER),
                onClick = { onFilterClick(!filterVisible) }
            ) {
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = if (filterVisible) "Hide filters" else "Show filters",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            IconButton(
                modifier = Modifier.testTag(TOP_APP_BAR_SETTINGS),
                onClick = { onSettingsClick() }
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            BadgedBox(
                modifier = Modifier.padding(end = 6.dp),
                badge = {
                    val badgeCount = if (cartItemCount > 99) "99+" else cartItemCount.toString()
                    if (cartItemCount > 0) {
                        Badge(
                            modifier = Modifier.testTag(TOP_APP_BAR_BADGE)
                        ) {
                            Text(
                                text = badgeCount,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            ) {
                IconButton(
                    modifier = Modifier.testTag(TOP_APP_BAR_CART),
                    onClick = { onShoppingCartClick() }
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Cart",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    )
}