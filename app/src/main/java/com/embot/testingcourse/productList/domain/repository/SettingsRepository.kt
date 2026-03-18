package com.embot.testingcourse.productList.domain.repository

import com.embot.testingcourse.core.domain.model.ThemeMode
import com.embot.testingcourse.productList.domain.model.SortOption
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {

    val themeMode: Flow<ThemeMode>
    val inStockOnly: Flow<Boolean>
    val selectedCategory: Flow<String?>
    val filterVisible: Flow<Boolean>
    val sortOption: Flow<SortOption>

    suspend fun setThemeMode(mode: ThemeMode)
    suspend fun setInStockOnly(value: Boolean)
    suspend fun setSelectedCategory(value: String?)
    suspend fun setFilterVisible(value: Boolean)
    suspend fun setSortOption(value: SortOption)
}