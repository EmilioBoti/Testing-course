package com.embot.testingcourse.core.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.embot.testingcourse.cart.presentation.CartScreen
import com.embot.testingcourse.datail.presentation.ProductDetailScreen
import com.embot.testingcourse.productList.presentation.ProductListScreen
import com.embot.testingcourse.settings.presentation.SettingsScreen

@Composable
fun NavigationGraph() {
    val backStack: NavBackStack<NavKey> = rememberNavBackStack(Screen.ProductList)
    val entries = entryProvider<NavKey> {
        entry<Screen.ProductList> {
            ProductListScreen(
                navigatoToSettings = { backStack.add(Screen.Settings) },
                navigatoToCart = { backStack.add(Screen.Cart) },
                navigatoToProductDetail = { productId ->
                    backStack.add(
                        Screen.ProductDetail(
                            productId
                        )
                    )
                }
            )
        }
        entry<Screen.Cart> {
            CartScreen(
                onBack = { backStack.removeLastOrNull() }
            )
        }
        entry<Screen.ProductDetail> { route ->
            ProductDetailScreen(
                productId = route.productId,
                onBack = { backStack.removeLastOrNull() }
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