package com.embot.testingcourse.cart.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.embot.testingcourse.cart.presentation.model.CartItemWithPromotion
import com.embot.testingcourse.core.presentation.components.MarketTopAppBar
import com.embot.testingcourse.core.presentation.components.QuantitySelector
import java.text.NumberFormat
import java.util.Currency

@Composable
fun CartScreen(
    cartViewModel: CartViewModel = hiltViewModel(),
    onBack: () -> Unit
) {

    val uiState by cartViewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        cartViewModel.events.collect { event ->
            when (event) {
                is CartEvent.ShowMessage -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    Scaffold(
        topBar = { MarketTopAppBar(title = "Cart", onBackClick = onBack) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            val modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
            when (val state = uiState) {
                CartUiState.Loading -> CartLoadingStateScreen(Modifier.fillMaxSize())
                is CartUiState.Error -> CartErroStateScreen(
                    modifier = modifier,
                    error = state,
                    onRetryClick = { cartViewModel.loadCart() }
                )
                is CartUiState.Success -> {
                    CartContentScreen(
                        modifier = modifier,
                        state =  state,
                        onDecreaseQuantity = cartViewModel::decreaseQuantity,
                        onIncreaseQuantity = cartViewModel::increaseQuantity
                    )
                }
            }
        }
    }

}

@Composable
fun CartContentScreen(
    modifier: Modifier = Modifier,
    state: CartUiState.Success,
    onDecreaseQuantity: (String, Int) -> Unit,
    onIncreaseQuantity: (String, Int) -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (state.cartItems.isEmpty()) {
            Column(
                modifier = modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "🛒",
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Add products",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
            ) {
                items(state.cartItems) { itemWithProduct ->
                    CartItemCard(
                        itemWithProduct = itemWithProduct,
                        onIncreaseQuantity = onIncreaseQuantity,
                        onDecreaseQuantity = onDecreaseQuantity,
                        onRemove = { }
                    )
                }
            }
        }
    }
}

@Composable
fun CartItemCard(
    itemWithProduct: CartItemWithPromotion,
    onDecreaseQuantity: (String, Int) -> Unit,
    onIncreaseQuantity: (String, Int) -> Unit,
    onRemove: () -> Unit
) {
    val product = itemWithProduct.product
    val cartItem = itemWithProduct.cartItem

    val currencyFormatter = remember {
        NumberFormat.getCurrencyInstance().apply {
            currency = Currency.getInstance("USD")
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth()
            .padding(2.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            AsyncImage(
                modifier = Modifier.weight(1f),
                model = product.imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
            )
            Column(
                modifier = Modifier.weight(3f)
                    .padding(horizontal = 12.dp, vertical = 4.dp),
            ) {
                Text(text = product.name)
                // PROMO
                Text(text = "Total: ${currencyFormatter.format(product.price)}")
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


@Composable
fun CartLoadingStateScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun CartErroStateScreen(
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
            text = "Error: ${error.message}",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onRetryClick
        ) {
            Text(text = "Retry")
        }
    }
}
