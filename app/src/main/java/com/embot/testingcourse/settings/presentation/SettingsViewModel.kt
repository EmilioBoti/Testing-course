package com.embot.testingcourse.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.embot.testingcourse.core.domain.model.ThemeMode
import com.embot.testingcourse.productList.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
): ViewModel() {

    private val _uiState: MutableStateFlow<SettingsUiState> = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> =  _uiState.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        combine(
            settingsRepository.inStockOnly,
            settingsRepository.themeMode
        ) { inStockOnly, themeMode ->
            _uiState.update {
                SettingsUiState(
                    inStockOnly = inStockOnly,
                    themeMode = themeMode
                )
            }
        }.launchIn(viewModelScope)
    }

    fun setInStockOnly(isOnlyStock: Boolean) {
        viewModelScope.launch {
            settingsRepository.setInStockOnly(isOnlyStock)
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            settingsRepository.setThemeMode(mode)
        }
    }

}