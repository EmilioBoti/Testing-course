package com.embot.testingcourse.productList.presentation

import com.embot.testingcourse.core.MainDispatcherRule
import com.embot.testingcourse.core.fakes.FakeProductRepository
import com.embot.testingcourse.core.fakes.FakePromotionRepository
import com.embot.testingcourse.core.fakes.FakeSettingsRepository
import com.embot.testingcourse.core.fakes.FakeSystemClock
import com.embot.testingcourse.productList.domain.model.SortOption
import com.embot.testingcourse.productList.domain.repository.ProductRepository
import com.embot.testingcourse.productList.domain.repository.PromotionRepository
import com.embot.testingcourse.productList.domain.repository.SettingsRepository
import com.embot.testingcourse.productList.domain.usecase.GetProductsUseCase
import com.embot.testingcourse.productList.domain.usecase.GetPromotionForProduct
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class ProductListViewModelMockTest {

    @get:Rule
    private val mainDispatcherRule: MainDispatcherRule = MainDispatcherRule()


    private val settingsRepository: SettingsRepository = mockk(relaxed = true) {
        every { selectedCategory } returns flowOf(null)
        every { sortOption } returns flowOf(SortOption.NONE)
        every { inStockOnly } returns flowOf(false)
        every { filterVisible } returns flowOf(true)
    }

    private fun createViewModel(
        productRepository: ProductRepository = FakeProductRepository(),
        promotionRepository: PromotionRepository = FakePromotionRepository(),
        fakeSettingsRepository: SettingsRepository = FakeSettingsRepository(),
        clock: FakeSystemClock = FakeSystemClock()
    ): ProductListViewModel {
        val getProductsUseCase = GetProductsUseCase(
            productRepository = productRepository,
            promotionRepository = promotionRepository,
            getPromotionForProduct = GetPromotionForProduct(),
            settingsRepository = fakeSettingsRepository,
            clock = clock
        )

        return ProductListViewModel(
            getProductsUseCase = getProductsUseCase,
            settingsRepository = settingsRepository
        )
    }

    @Test
    fun `given category when set category then delegate to settings repository`() = runTest(mainDispatcherRule.scheduler) {
        // GIVEN
        val viewModel = createViewModel()
        val category = "pasta"

        // WHEN
        viewModel.setCategory(category)

        // THEN
        coVerify(exactly = 1) { settingsRepository.setSelectedCategory(category) }
    }

    @Test
    fun `given sortOption when set sortOption then delegate to settings repository`() = runTest(mainDispatcherRule.scheduler) {
        // GIVEN
        val viewModel = createViewModel()
        val sortOption = SortOption.PRICE_DESC

        // WHEN
        viewModel.setSortOption(sortOption)

        // THEN
        coVerify(exactly = 1) { settingsRepository.setSortOption(sortOption) }
    }

    @Test
    fun `given filterVisible when set filterVisible then delegate to settings repository`() = runTest(mainDispatcherRule.scheduler) {
        // GIVEN
        val viewModel = createViewModel()
        val filterVisible = true

        // WHEN
        viewModel.setFilterVisible(filterVisible)

        // THEN
        coVerify(exactly = 1) { settingsRepository.setFilterVisible(filterVisible) }
    }

}