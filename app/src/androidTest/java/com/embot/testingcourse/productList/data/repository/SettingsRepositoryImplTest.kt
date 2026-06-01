package com.embot.testingcourse.productList.data.repository

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.embot.testingcourse.core.domain.model.ThemeMode
import com.embot.testingcourse.core.mockwebserver.MockWebServerUrlHolder
import com.embot.testingcourse.productList.domain.model.SortOption
import com.embot.testingcourse.productList.domain.repository.SettingsRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class SettingsRepositoryImplTest {

    @get:Rule
    val hilt: HiltAndroidRule = HiltAndroidRule(this)

    @Inject
    lateinit var settingsRepository: SettingsRepository


    @Before
    fun setUp() = runTest {
        hilt.inject()
        (settingsRepository as? SettingsRepositoryImpl)?.clear()
    }

    @Test
    fun givenNoDataSaved_whenInStockOnlyIsRead_thenReturnsDefaultFalse() = runTest {
        val result = settingsRepository.inStockOnly.first()
        assertTrue(!result)
    }

    @Test
    fun givenNoDataSaved_whenFilterVisibleIsRead_thenReturnsDefaultFalse() = runTest {
        val result = settingsRepository.filterVisible.first()
        assertTrue(!result)
    }

    @Test
    fun givenNoDataSaved_whenSelectedCategoryIsRead_thenReturnsDefaultNull() = runTest {
        val result = settingsRepository.selectedCategory.first()
        assertNull(result)
    }

    @Test
    fun givenNoDataSaved_whenThemeModeIsRead_thenReturnsDefaultSystem() = runTest {
        val result = settingsRepository.themeMode.first()
        assertEquals(ThemeMode.SYSTEM, result)
    }

    @Test
    fun givenNoDataSaved_whenSortOptionIsRead_thenReturnsDefaultNONE() = runTest {
        val result = settingsRepository.sortOption.first()
        assertEquals(SortOption.NONE, result)
    }

    @Test
    fun givenNoDataSaved_whenSetThemeModeToDark_thenPersistValue() = runTest {
        settingsRepository.setThemeMode(ThemeMode.DARK)
        val result = settingsRepository.themeMode.first()
        assertEquals(ThemeMode.DARK, result)
    }

    @Test
    fun givenNoDataSaved_whenSetInStockOnlyToTrue_thenPersistValue() = runTest {
        settingsRepository.setInStockOnly(true)
        val result = settingsRepository.inStockOnly.first()
        assertTrue(result)
    }

    @Test
    fun givenRepository_whenSetFilterVisibleToTrue_thenPersistValue() = runTest {
        settingsRepository.setFilterVisible(true)
        val result = settingsRepository.filterVisible.first()
        assertTrue(result)
    }

    @Test
    fun givenNoDataSaved_whenSetSelectedCategoryToMilk_thenPersistValue() = runTest {
        settingsRepository.setSelectedCategory("milk")
        val result = settingsRepository.selectedCategory.first()
        assertEquals("milk", result)
    }

    @Test
    fun givenNoDataSaved_whenSetSortOptionToPRICE_ASC_thenPersistValue() = runTest {
        settingsRepository.setSortOption(SortOption.PRICE_ASC)
        val result = settingsRepository.sortOption.first()
        assertEquals(SortOption.PRICE_ASC, result)
    }

    /**
     * This is another way to test the seters
     */
    @Test
    fun givenMultipleSettingsChanges_whenReadAll_thenStateIsConsistent() = runTest {
        settingsRepository.setThemeMode(ThemeMode.DARK)
        settingsRepository.setInStockOnly(true)
        settingsRepository.setFilterVisible(true)
        settingsRepository.setSelectedCategory("milk")
        settingsRepository.setSortOption(SortOption.PRICE_ASC)

        assertEquals(ThemeMode.DARK, settingsRepository.themeMode.first())
        assertTrue(settingsRepository.inStockOnly.first())
        assertTrue(settingsRepository.filterVisible.first())
        assertEquals("milk",settingsRepository.selectedCategory.first())
        assertEquals(SortOption.PRICE_ASC, settingsRepository.sortOption.first())
    }


}