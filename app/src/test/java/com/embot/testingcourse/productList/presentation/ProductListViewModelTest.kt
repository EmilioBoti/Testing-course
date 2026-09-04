package com.embot.testingcourse.productList.presentation

import app.cash.turbine.test
import com.embot.testingcourse.core.MainDispatcherRule
import com.embot.testingcourse.core.builders.product
import com.embot.testingcourse.core.domain.utils.Clock
import com.embot.testingcourse.core.fakes.FakeProductRepository
import com.embot.testingcourse.core.fakes.FakePromotionRepository
import com.embot.testingcourse.core.fakes.FakeSettingsRepository
import com.embot.testingcourse.core.fakes.FakeSystemClock
import com.embot.testingcourse.core.stubs.FailingProductRepositoryStub
import com.embot.testingcourse.productList.domain.model.SortOption
import com.embot.testingcourse.productList.domain.repository.ProductRepository
import com.embot.testingcourse.productList.domain.usecase.GetProductsUseCase
import com.embot.testingcourse.productList.domain.usecase.GetPromotionForProduct
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.Instant

class ProductListViewModelTest {

    @get:Rule
    private val mainDispatcherRule: MainDispatcherRule = MainDispatcherRule()

    private lateinit var productRepository: FakeProductRepository
    private lateinit var promotionRepository: FakePromotionRepository
    private lateinit var settingsRepository: FakeSettingsRepository
    private val clock: Clock =
        FakeSystemClock().apply { setTime(Instant.parse("2026-04-03T10:00:00Z")) }

    @Before
    fun setUp() {
        productRepository = FakeProductRepository()
        promotionRepository = FakePromotionRepository()
        settingsRepository = FakeSettingsRepository()
    }

    private fun createViewModel(
        productRepository: ProductRepository = FakeProductRepository(),
        promotionRepository: FakePromotionRepository = FakePromotionRepository(),
        settingsRepository: FakeSettingsRepository = FakeSettingsRepository(),
        clock: FakeSystemClock = FakeSystemClock()
    ): ProductListViewModel {

        val getProductsUseCase = GetProductsUseCase(
            productRepository = productRepository,
            promotionRepository = promotionRepository,
            getPromotionForProduct = GetPromotionForProduct(),
            settingsRepository = settingsRepository,
            clock = clock
        )

        return ProductListViewModel(
            getProductsUseCase = getProductsUseCase,
            settingsRepository = settingsRepository
        )
    }

    @Test
    fun `given product when initialized then emits success state`() = runTest(mainDispatcherRule.scheduler) {
        // GIVEN
        val prodcutId = "id1"
        val p1 = product { withId(prodcutId) }

        val fakeProductRepository = FakeProductRepository().apply { setProducts(listOf(p1)) }

        // WHEN
        val viewModel = createViewModel(productRepository = fakeProductRepository)

        // THEN
        viewModel.uiState.test {
            awaitItem()

            val state = awaitItem()

            assertTrue(state is ProductListUiState.Success)
            assertEquals(1, (state as ProductListUiState.Success).productList.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `given selected category when set category then filters products`() = runTest(mainDispatcherRule.scheduler) {
        // GIVEN
        val p1 = product { withId("1"); withCategory("carne") }
        val p2 = product { withId("2"); withCategory("pasta") }

        val fakeProductRepository = FakeProductRepository().apply { setProducts(listOf(p1, p2)) }

        // WHEN
        val viewModel = createViewModel(productRepository = fakeProductRepository)

        // THEN
        viewModel.uiState.test {
            awaitItem()

            viewModel.setCategory("pasta")

            val state = awaitItem()

            assertTrue(state is ProductListUiState.Success)
            assertEquals(1, (state as ProductListUiState.Success).productList.size)
            assertEquals("pasta", state.selectedCategory)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `given price asc sort option when set sort option then sorts by efective price`() = runTest(mainDispatcherRule.scheduler) {
        // GIVEN
        val p1 = product { withId("2"); withPrice(30.0) }
        val p2 = product { withId("1"); withPrice(15.0) }

        val fakeProductRepository = FakeProductRepository().apply { setProducts(listOf(p1, p2)) }

        val viewModel = createViewModel(productRepository = fakeProductRepository)

        // THEN
        viewModel.uiState.test {
            awaitItem()

            viewModel.setSortOption(SortOption.PRICE_ASC)

            val state = awaitItem() as ProductListUiState.Success


            assertEquals(15.0, state.productList[0].product.price, 0.0)
            assertEquals(30.0, state.productList[1].product.price, 0.0)
            assertEquals(SortOption.PRICE_ASC, state.sortOption)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `given repository error when loading prodcuts then emits error state`() = runTest(mainDispatcherRule.scheduler) {
        val fakeProductRepository = FailingProductRepositoryStub(Exception("prueba test"))

        val viewModel = createViewModel(productRepository = fakeProductRepository)

        viewModel.uiState.test {
            awaitItem()

            val state = awaitItem()

            assertTrue(state is ProductListUiState.Error)
            assertTrue((state as ProductListUiState.Error).message == "prueba test")

            cancelAndIgnoreRemainingEvents()
        }
    }

}