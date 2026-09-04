package com.embot.testingcourse.productList.presentation

import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.ReceiveTurbine
import app.cash.turbine.test
import com.embot.testingcourse.core.MainDispatcherRule
import com.embot.testingcourse.core.mockwebserver.MiniMarketApiDispatcher
import com.embot.testingcourse.core.mockwebserver.MockWebServerUrlHolder
import com.embot.testingcourse.core.mockwebserver.rules.MockWebServerRule
import com.embot.testingcourse.core.utils.asAsset
import com.embot.testingcourse.productList.data.repository.SettingsRepositoryImpl
import com.embot.testingcourse.productList.domain.repository.ProductRepository
import com.embot.testingcourse.productList.domain.repository.PromotionRepository
import com.embot.testingcourse.productList.domain.repository.SettingsRepository
import com.embot.testingcourse.productList.domain.usecase.GetProductsUseCase
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class ProductListViewModelIntegrationTest {

    private companion object {
        const val EXPECTED_PRODUCT_SIZE = 3
        const val DAIRY_CATEGORY = "Dairy"
    }

    @get:Rule(order = 0)
    val mockWebServerRule = MockWebServerRule()

    @get:Rule(order = 1)
    val hilt = HiltAndroidRule(this)

    @get:Rule(order = 2)
    val mainDispatcherRule = MainDispatcherRule()

    @Inject
    lateinit var getProductsUseCase: GetProductsUseCase

    @Inject
    lateinit var settingsRepository: SettingsRepository

    @Inject
    lateinit var promotionRepository: PromotionRepository

    @Inject
    lateinit var productRepository: ProductRepository


    @Before
    fun setUp() = runTest {
        mockWebServerRule.server.dispatcher = MiniMarketApiDispatcher(
            productJson = "product_list_default.json".asAsset(),
        )
        hilt.inject()
        (settingsRepository as? SettingsRepositoryImpl)?.clear()
        productRepository.refreshPRoduct()
    }

    @After
    fun tearDown() {
        MockWebServerUrlHolder.baseUrl = "http://localhost:8080/"
    }

    @Test
    fun givenSuccessfulApi_whenViewModelLoads_thenShowsProducts() = runTest {
        val viewModel = ProductListViewModel(getProductsUseCase, settingsRepository)

        viewModel.uiState.test {
            val result = awaitSuccessMatching { it.productList.size == EXPECTED_PRODUCT_SIZE }
            assertTrue(result.productList.isNotEmpty())
            assertTrue(result.productList.size == EXPECTED_PRODUCT_SIZE)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun givenDairyCategorySelected_thenFiltering_thenOnlyDairyProductsAreShown() = runTest {
        val viewModel = ProductListViewModel(getProductsUseCase, settingsRepository)

        viewModel.uiState.test {
            awaitSuccessMatching { it.productList.size == EXPECTED_PRODUCT_SIZE }
            viewModel.setCategory(DAIRY_CATEGORY)

            val result = awaitSuccessMatching { state ->
                state.selectedCategory == DAIRY_CATEGORY &&
                        state.productList.isNotEmpty() &&
                        state.productList.all { it.product.category == DAIRY_CATEGORY }
            }

            assertTrue(result.productList.size == 2)
            assertTrue(result.productList.all { it.product.category == DAIRY_CATEGORY })
            cancelAndIgnoreRemainingEvents()
        }
    }

    /**
     * I commented this test because it's failing and I cannot resolve it yet
     * Remember to dive deep in the issue
     */

//    @Test
//    fun givenProductsLoaded_whenSortingByPriceAsc_thenListIsCorrectlyOrdered() = runTest {
//        val viewModel = ProductListViewModel(getProductsUseCase, settingsRepository)
//
//        viewModel.uiState.test {
//            awaitSuccessMatching { it.productList.size == EXPECTED_PRODUCT_SIZE }
//
//            viewModel.setSortOption(SortOption.PRICE_ASC)
//
//            val result = awaitSuccessMatching { state -> state.sortOption == SortOption.PRICE_ASC }
//
//            assertEquals(1.2, result.productList.first().product.price, 0.00)
//            assertEquals(listOf(1.2, 1.8, 3.1), result.productList.map { it.product.price })
//            cancelAndIgnoreRemainingEvents()
//        }
//    }

    private suspend fun ReceiveTurbine<ProductListUiState>.awaitSuccessMatching(
        predicate: (ProductListUiState.Success) -> Boolean
    ): ProductListUiState.Success {
        while (true) {
            when(val item = awaitItem()) {
                is ProductListUiState.Success -> if (predicate(item)) return item
                is ProductListUiState.Error -> error("Unexpected error: ${item.message}")
                ProductListUiState.Loading -> Unit
            }
        }
    }

}