package com.embot.testingcourse.settings.presentation

import app.cash.turbine.test
import com.embot.testingcourse.core.MainDispatcherRule
import com.embot.testingcourse.core.domain.model.ThemeMode
import com.embot.testingcourse.core.fakes.FakeSettingsRepository
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class SettingsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    /**
     * THIS IS JUST AN EXAMPLE INSTEAD OF USING TURBINE AS THE TEST BELOW
     * BOTH TEST ARE TESTING THE SAME CASE
     */
    @Test
    fun secondTest() = runTest(mainDispatcherRule.scheduler) {
        val settingsRepository = FakeSettingsRepository().apply { setInStockOnly(true) }

        val viewModel = SettingsViewModel(settingsRepository)

        val job = launch { viewModel.uiState.collect() }

        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.inStockOnly)
        job.cancel()
    }

    @Test
    fun `given repository with values when viewModel is initialized then ui state is updted`()
    = runTest(mainDispatcherRule.scheduler) {
        // GIVEN
        val settingsRepository = FakeSettingsRepository().apply { setInStockOnly(true) }
        // WHEN
        val viewModel = SettingsViewModel(settingsRepository)

        // THEN
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.inStockOnly)
            cancelAndIgnoreRemainingEvents()
        }

    }

    @Test
    fun `given viewModel when theme mode is changed then ui state and repository are updated`()
    = runTest(mainDispatcherRule.scheduler) {
        // GIVEN
        val settingsRepository = FakeSettingsRepository()
        val viewModel = SettingsViewModel(settingsRepository)

        viewModel.uiState.test {
            awaitItem()

            // WHEN
            viewModel.setThemeMode(ThemeMode.DARK)

            // THEN
            val updatedState = awaitItem()

            assertEquals(ThemeMode.DARK, updatedState.themeMode)
            assertEquals(ThemeMode.DARK, settingsRepository.themeMode.first())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `given viewModel when in stock only mode is changed then ui state and repository are updated`()
    = runTest(mainDispatcherRule.scheduler) {
        // GIVEN
        val settingsRepository = FakeSettingsRepository()
        val viewModel = SettingsViewModel(settingsRepository)

        viewModel.uiState.test {
            awaitItem()

            // WHEN
            viewModel.setInStockOnly(true)

            // THEN
            val updatedState = awaitItem()

            assertTrue(updatedState.inStockOnly)
            assertTrue(settingsRepository.inStockOnly.first())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `given viewModel when repository changed externally then ui state update automatically`()
    = runTest(mainDispatcherRule.scheduler) {
        // GIVEN
        val settingsRepository = FakeSettingsRepository()
        val viewModel = SettingsViewModel(settingsRepository)

        viewModel.uiState.test {
            awaitItem()

            settingsRepository.setInStockOnly(true)

            assertTrue(awaitItem().inStockOnly)
            cancelAndIgnoreRemainingEvents()

        }
    }

}