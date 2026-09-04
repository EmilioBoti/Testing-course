package com.embot.testingcourse.core.fakes

import com.embot.testingcourse.core.domain.model.ThemeMode
import com.embot.testingcourse.productList.domain.model.SortOption
import com.embot.testingcourse.productList.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeSettingsRepository: SettingsRepository {

    private val _themeMode = MutableStateFlow<ThemeMode>(ThemeMode.SYSTEM)
    private val _inStockOnly = MutableStateFlow(false)
    private val _selectedCategory = MutableStateFlow<String?>(null)
    private val _filterVisible = MutableStateFlow(true)
    private val _sortOption = MutableStateFlow(SortOption.NONE)

    override val themeMode: Flow<ThemeMode> = _themeMode.asStateFlow()
    override val inStockOnly: Flow<Boolean> = _inStockOnly.asStateFlow()
    override val selectedCategory: Flow<String?> = _selectedCategory.asStateFlow()
    override val filterVisible: Flow<Boolean> = _filterVisible.asStateFlow()
    override val sortOption: Flow<SortOption> = _sortOption.asStateFlow()


    override suspend fun setThemeMode(mode: ThemeMode) {
        _themeMode.value = mode
    }

    override suspend fun setInStockOnly(value: Boolean) {
        _inStockOnly.value = value
    }

    override suspend fun setSelectedCategory(value: String?) {
        _selectedCategory.value = value
    }

    override suspend fun setFilterVisible(value: Boolean) {
        _filterVisible.value = value
    }

    override suspend fun setSortOption(value: SortOption) {
        _sortOption.value = value
    }
}