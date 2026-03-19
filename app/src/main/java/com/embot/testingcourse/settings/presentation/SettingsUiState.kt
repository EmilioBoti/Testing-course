package com.embot.testingcourse.settings.presentation

import com.embot.testingcourse.core.domain.model.ThemeMode

data class SettingsUiState(
    val inStockOnly: Boolean = false,
    val themeMode: ThemeMode = ThemeMode.SYSTEM
)