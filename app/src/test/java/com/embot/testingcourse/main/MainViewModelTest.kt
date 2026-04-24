package com.embot.testingcourse.main

import app.cash.turbine.test
import com.embot.testingcourse.core.MainDispatcherRule
import com.embot.testingcourse.core.domain.model.ThemeMode
import com.embot.testingcourse.core.fakes.FakeSettingsRepository
import com.embot.testingcourse.productList.domain.repository.SettingsRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class MainViewModelTest {

    @get:Rule
    private val mainDispatcherRule: MainDispatcherRule = MainDispatcherRule()


    private fun createViewModel(
        settingsRepository: SettingsRepository = FakeSettingsRepository()
    ): MainViewModel {
        return MainViewModel(settingsRepository = settingsRepository)
    }

    @Test
    fun `give settingsRepository with dark theme mode when initialized then emits dark them mode`() = runTest(mainDispatcherRule.scheduler) {

        val themeMode = ThemeMode.DARK
        val fakeSettingsRepository = FakeSettingsRepository().apply { setThemeMode(ThemeMode.DARK) }

        val viewModel = createViewModel(fakeSettingsRepository)

        viewModel.themeMode.test {
            awaitItem()
            val state = awaitItem()

            assertEquals(themeMode, state)
            cancelAndIgnoreRemainingEvents()
        }

    }

    @Test
    fun `give default Repository when  when initialized change then emits system theme mode`() = runTest(mainDispatcherRule.scheduler) {
        val viewModel = createViewModel()

        viewModel.themeMode.test {
            val state = awaitItem()
            assertEquals(ThemeMode.SYSTEM, state)
            cancelAndIgnoreRemainingEvents()
        }
    }

}