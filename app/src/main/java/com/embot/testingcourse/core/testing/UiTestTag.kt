package com.embot.testingcourse.core.testing

object UiTestTag {

    const val SETTINGS_TOP_APP_BAR_BACK = "SETTINGS_TOP_APP_BAR_BACK"

    //SETTINGS
    const val SETTINGS_SCREEN_CONTENT = "SETTINGS_SCREEN_CONTENT"
    const val SETTINGS_IN_STOCK_SWITCH = "SETTINGS_IN_STOCK_SWITCH"
    const val SETTINGS_TAX_SWITCH = "SETTINGS_TAX_SWITCH"

    fun settingsThemeOption(themeModeName: String) = "settings_theme_${themeModeName.lowercase()}"

}