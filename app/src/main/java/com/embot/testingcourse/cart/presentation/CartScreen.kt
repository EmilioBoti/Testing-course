package com.embot.testingcourse.cart.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.embot.testingcourse.R
import com.embot.testingcourse.cart.domain.model.CartSummary
import com.embot.testingcourse.cart.presentation.model.CartItemWithPromotion
import com.embot.testingcourse.core.presentation.components.MarketTopAppBar
import com.embot.testingcourse.core.presentation.components.QuantitySelector
import com.embot.testingcourse.core.testing.UiTestTag.CART_EMPTY
import com.embot.testingcourse.core.testing.UiTestTag.CART_ERROR_MESSAGE
import com.embot.testingcourse.core.testing.UiTestTag.CART_LOADING
import com.embot.testingcourse.core.testing.UiTestTag.CART_RETRY_BUTTON
import com.embot.testingcourse.core.testing.UiTestTag.PRODUCT_LIST_LOADING
import com.embot.testingcourse.productList.domain.model.ProductPromotion
import java.text.NumberFormat
import java.util.Currency

@Composable
fun CartScreen(
    cartViewModel: CartViewModel = hiltViewModel(),
    onBack: () -> Unit
) {

    val uiState by cartViewModel.uiState.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        cartViewModel.events.collect { event ->
            when (event) {
                is CartEvent.ShowMessage -> snackBarHostState.showSnackbar(event.message)
            }
        }
    }

    CartContentScreen(
        uiState = uiState,
        onBack = onBack,
        onRefresh = cartViewModel::refresh,
        onDecreaseQuantity = cartViewModel::decreaseQuantity,
        onIncreaseQuantity = cartViewModel::increaseQuantity,
        onRemove = cartViewModel::removeFromCart,
        snackBarHostState = snackBarHostState,
    )

}

@Composable
fun CartContentScreen(
    snackBarHostState: SnackbarHostState = remember { SnackbarHostState() },
    uiState: CartUiState,
    onRefresh: () -> Unit,
    onBack: () -> Unit,
    onDecreaseQuantity: (String, Int) -> Unit,
    onIncreaseQuantity: (String, Int) -> Unit,
    onRemove: (String) -> Unit,
) {
    Scaffold(
        topBar = { MarketTopAppBar(title = stringResource(R.string.cart_title), onBackClick = onBack) },
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (uiState) {
                CartUiState.Loading -> CartLoadingStateScreen(Modifier.fillMaxSize())
                is CartUiState.Error -> CartErrorStateScreen(
                    modifier = Modifier.fillMaxSize(),
                    error = uiState,
                    onRetryClick = onRefresh
                )

                is CartUiState.Success -> {
                    CartSuccessStateScreen(
                        modifier = Modifier.fillMaxSize(),
                        state = uiState,
                        onDecreaseQuantity = onDecreaseQuantity,
                        onIncreaseQuantity = onIncreaseQuantity,
                        onRemove = onRemove
                    )
                }
            }
        }
    }
}

@Composable
fun CartSuccessStateScreen(
    modifier: Modifier = Modifier,
    state: CartUiState.Success,
    onDecreaseQuantity: (String, Int) -> Unit,
    onIncreaseQuantity: (String, Int) -> Unit,
    onRemove: (String) -> Unit
) {
    val currencyFormatter = remember {
        NumberFormat.getCurrencyInstance().apply {
            currency = Currency.getInstance("USD")
        }
    }

    Column(
        modifier = modifier.padding(16.dp)
    ) {
        AnimatedContent(state.cartItems.isEmpty()) { isEmpty ->
            if (isEmpty) {
                Column(
                    modifier = modifier.fillMaxSize().testTag(CART_EMPTY),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "🛒",
                        style = MaterialTheme.typography.displayLarge,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(12.dp)
                    )
                    Text(
                        text = stringResource(R.string.add_products),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    items(state.cartItems, key = { it.cartItem.productId }) { itemWithProduct ->
                        CartItemCard(
                            modifier = Modifier.animateItem(),
                            itemWithProduct = itemWithProduct,
                            currencyFormatter = currencyFormatter,
                            onIncreaseQuantity = onIncreaseQuantity,
                            onDecreaseQuantity = onDecreaseQuantity,
                            onRemove = onRemove
                        )
                    }
                }
            }
        }

        if (state.cartItems.isNotEmpty() && state.summary != null) {
            CartSummaryCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                summary = state.summary,
                currencyFormatter = currencyFormatter
            )
        }
    }
}

@Composable
fun CartSummaryCard(
    modifier: Modifier,
    summary: CartSummary,
    currencyFormatter: NumberFormat
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = "Cart summary",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Bold,
            )
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "SubTotal",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = currencyFormatter.format(summary.subTotal),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
            if (summary.discountTotal > 0) {
                Row(
                    modifier = modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Discount",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error,
                    )
                    Text(
                        text = currencyFormatter.format(summary.discountTotal),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }

            HorizontalDivider(
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f)
            )

            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Total",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = currencyFormatter.format(summary.finalTotal),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold
                )
            }

        }
    }
}

@Composable
fun CartItemCard(
    modifier: Modifier,
    itemWithProduct: CartItemWithPromotion,
    currencyFormatter: NumberFormat,
    onDecreaseQuantity: (String, Int) -> Unit,
    onIncreaseQuantity: (String, Int) -> Unit,
    onRemove: (String) -> Unit
) {
    val product = itemWithProduct.item.product
    val promotion = itemWithProduct.item.promotion
    val cartItem = itemWithProduct.cartItem

    val unitPrice = when (promotion) {
        is ProductPromotion.BuyXPayY -> product.price
        is ProductPromotion.Percent -> promotion.discountPrice
        null -> product.price
    }

    val hasDiscount = promotion is ProductPromotion.Percent
    val itemTotal = unitPrice * cartItem.quantity

    val dismissState = rememberSwipeToDismissBoxState()

    LaunchedEffect(dismissState.currentValue) {
        if (dismissState.currentValue == SwipeToDismissBoxValue.StartToEnd) {
            onRemove(cartItem.productId)
            dismissState.snapTo(SwipeToDismissBoxValue.Settled)
        }
    }

    SwipeToDismissBox(
        modifier = modifier,
        state = dismissState,
        enableDismissFromEndToStart = false,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(shape = RoundedCornerShape(16.dp))
                    .background(color = MaterialTheme.colorScheme.error),
                contentAlignment = Alignment.CenterStart,
            ) {
                Icon(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = "Delete Item from Cart",
                    tint = MaterialTheme.colorScheme.surface
                )
            }
        }
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 2.dp
            ),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Max)
                    .padding(8.dp)
            ) {
                AsyncImage(
                    modifier = Modifier
                        .weight(1.5f)
                        .fillMaxHeight()
                        .clip(shape = RoundedCornerShape(16.dp)),
                    model = product.imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                )
                Column(
                    modifier = Modifier
                        .weight(3f)
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (hasDiscount) {
                            Text(
                                text = "Total: ${currencyFormatter.format(product.price)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textDecoration = TextDecoration.LineThrough
                            )
                            Text(
                                text = "Total: ${currencyFormatter.format(unitPrice)} c/u",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            Text(
                                text = "Total: ${currencyFormatter.format(unitPrice)} c/u",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                    Text(
                        text = "Total: ${currencyFormatter.format(itemTotal)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    QuantitySelector(
                        modifier = Modifier.background(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(8.dp)
                        ),
                        quantity = cartItem.quantity.toString(),
                        canDecrease = cartItem.quantity > 1,
                        canIncrease = cartItem.quantity < product.stock,
                        onDecreaseClick = { onDecreaseQuantity(product.id, cartItem.quantity) },
                        onIncreaseClick = { onIncreaseQuantity(product.id, cartItem.quantity) }
                    )
                }
            }
        }

    }

}


@Composable
fun CartLoadingStateScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(modifier = Modifier.testTag(CART_LOADING))
    }
}

@Composable
fun CartErrorStateScreen(
    modifier: Modifier = Modifier,
    error: CartUiState.Error,
    onRetryClick: () -> Unit
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            modifier = Modifier.testTag(CART_ERROR_MESSAGE),
            text = "Error: ${error.message}",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            modifier = Modifier.testTag(CART_RETRY_BUTTON),
            onClick = onRetryClick
        ) {
            Text(text = "Retry")
        }
    }
}
