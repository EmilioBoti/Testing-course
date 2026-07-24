package com.embot.testingcourse.productList.data.repository

import androidx.annotation.VisibleForTesting
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.embot.testingcourse.core.domain.model.ThemeMode
import com.embot.testingcourse.productList.domain.model.SortOption
import com.embot.testingcourse.productList.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : SettingsRepository {


    companion object {
        private val THEME_MODE_KEY = intPreferencesKey("THEME_MODE_KEY")
        private val IN_STOCK_ONLY_KEY = booleanPreferencesKey("IN_STOCK_ONLY_KEY")
        private val SELECTED_CATEGORY_KEY = stringPreferencesKey("SELECTED_CATEGORY_KEY")
        private val FILTER_VISIBLE_KEY = booleanPreferencesKey("FILTER_VISIBLE_KEY")
        private val SORT_OPTION_KEY = stringPreferencesKey("SORT_OPTION_KEY")
    }

    private val dataStoreFlow: Flow<Preferences> = dataStore.data
        .catch { exception: Throwable ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }

    override val themeMode: Flow<ThemeMode> = dataStoreFlow.map { preferences ->
        when (preferences[THEME_MODE_KEY]) {
            ThemeMode.SYSTEM.id -> ThemeMode.SYSTEM
            ThemeMode.DARK.id -> ThemeMode.DARK
            ThemeMode.LIGHT.id -> ThemeMode.LIGHT
            else -> ThemeMode.SYSTEM
        }
    }
    override val inStockOnly: Flow<Boolean> =
        dataStoreFlow.map { preferences -> preferences[IN_STOCK_ONLY_KEY] ?: false }
    override val selectedCategory: Flow<String?> =
        dataStoreFlow.map { preferences -> preferences[SELECTED_CATEGORY_KEY] }
    override val filterVisible: Flow<Boolean> =
        dataStoreFlow.map { preferences -> preferences[FILTER_VISIBLE_KEY] ?: false }
    override val sortOption: Flow<SortOption> = dataStoreFlow.map { preferences ->
        val raw = preferences[SORT_OPTION_KEY]
        runCatching {
            SortOption.valueOf(raw ?: SortOption.NONE.name)
        }.getOrDefault(SortOption.NONE)
    }


    override suspend fun setThemeMode(mode: ThemeMode) {
        dataStore.edit { preferences ->
            when (mode) {
                ThemeMode.SYSTEM -> preferences[THEME_MODE_KEY] = ThemeMode.SYSTEM.id
                ThemeMode.DARK -> preferences[THEME_MODE_KEY] = ThemeMode.DARK.id
                ThemeMode.LIGHT -> preferences[THEME_MODE_KEY] = ThemeMode.LIGHT.id
            }
        }
    }

    override suspend fun setInStockOnly(value: Boolean) {
        dataStore.edit { preferences ->
            preferences[IN_STOCK_ONLY_KEY] = value
        }
    }

    override suspend fun setSelectedCategory(value: String?) {
        dataStore.edit { preferences ->
            if (value == null) {
                preferences.remove(SELECTED_CATEGORY_KEY)
            } else {
                preferences[SELECTED_CATEGORY_KEY] = value
            }
        }
    }

    override suspend fun setFilterVisible(value: Boolean) {
        dataStore.edit { preferences ->
            preferences[FILTER_VISIBLE_KEY] = value
        }
    }

    override suspend fun setSortOption(value: SortOption) {
        dataStore.edit { preferences ->
            preferences[SORT_OPTION_KEY] = value.name
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    suspend fun clear() {
        dataStore.edit { it.clear() }
        dataStore.data.first { it.asMap().isEmpty() }
    }

}