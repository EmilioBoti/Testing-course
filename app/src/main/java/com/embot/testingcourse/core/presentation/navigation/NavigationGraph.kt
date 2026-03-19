package com.embot.testingcourse.core.presentation.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.embot.testingcourse.productList.presentation.ProductListScreen
import com.embot.testingcourse.settings.presentation.SettingsScreen

@Composable
fun NavigationGraph(modifier: Modifier = Modifier) {
    val backStack: NavBackStack<NavKey> = rememberNavBackStack(Screen.ProductList)
    val entries = entryProvider<NavKey> {
        entry<Screen.ProductList> {
            ProductListScreen(
                navigatoToSettings = { backStack.add(Screen.Settings) }
            )
        }
        entry<Screen.Cart> {
            Text(
                text = "Cart",
                fontSize = 30.sp
            )
        }
        entry<Screen.ProductDetail> {
            Text(
                text = "ProductDetail",
                fontSize = 30.sp
            )
        }
        entry<Screen.Settings> {
            SettingsScreen(
                onBack = { backStack.removeLastOrNull() }
            )
        }
    }
    NavDisplay(
        backStack = backStack,
        entryProvider = entries,
        onBack = { backStack.removeLastOrNull() }
    )
}