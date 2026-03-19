package com.embot.testingcourse.datail.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.embot.testingcourse.core.presentation.components.MarketTopAppBar

@Composable
fun ProductDetailScreen(
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            MarketTopAppBar(
                title = "Product Detail",
                onBackClick = onBack
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(paddingValues)
                .padding(8.dp)
        ) {

        }
    }
}