package com.embot.testingcourse.settings.presentation

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.embot.testingcourse.R
import com.embot.testingcourse.core.domain.model.ThemeMode
import com.embot.testingcourse.core.testing.UiTestTag
import com.embot.testingcourse.core.testing.UiTestTag.SETTINGS_IN_STOCK_SWITCH
import com.embot.testingcourse.core.testing.UiTestTag.SETTINGS_SCREEN_CONTENT
import com.embot.testingcourse.core.testing.UiTestTag.SETTINGS_TAX_SWITCH
import com.embot.testingcourse.core.testing.UiTestTag.SETTINGS_TOP_APP_BAR_BACK
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SettingsScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()


    @Test
    fun givenDefaultSettingsState_when_Rendered_thenShowsFilterAndAppearanceSections() {
        createSettingScreen(uiState = SettingsUiState())

        composeRule.onNodeWithText(getString(R.string.settings_title)).assertIsDisplayed()
        composeRule.onNodeWithText(getString(R.string.settings_in_stock_only)).assertIsDisplayed()
        composeRule.onNodeWithText(getString(R.string.settings_filters_section)).assertIsDisplayed()
        composeRule.onNodeWithText(getString(R.string.settings_appearance_section)).assertIsDisplayed()
        composeRule.onNodeWithText(getString(R.string.settings_theme_label)).assertIsDisplayed()
        composeRule.onNodeWithTag(SETTINGS_SCREEN_CONTENT).assertIsDisplayed()
        composeRule.onNodeWithTag(SETTINGS_IN_STOCK_SWITCH).assertIsOff()
        composeRule.onNodeWithTag(SETTINGS_TAX_SWITCH).assertIsOn()
    }

    @Test
    fun givenInStockOnlyFalse_whenRendered_thenSwitchIsOff() {
        createSettingScreen(uiState = SettingsUiState(
            inStockOnly = false
        ))
        composeRule.onNodeWithTag(SETTINGS_IN_STOCK_SWITCH).assertIsOff()
    }

    @Test
    fun givenInStockOnlyTrue_whenRendered_thenSwitchIsOn() {
        createSettingScreen(uiState = SettingsUiState(
            inStockOnly = true
        ))
        composeRule.onNodeWithTag(SETTINGS_IN_STOCK_SWITCH).assertIsOn()
    }

    @Test
    fun givenSystemTheme_whenRendered_thenSystemOptionIsSelected() {
        createSettingScreen(uiState = SettingsUiState(themeMode = ThemeMode.SYSTEM))
        composeRule.onNodeWithTag(UiTestTag.settingsThemeOption("System")).assertIsSelected()
    }

    @Test
    fun givenDarkTheme_whenRendered_thenDarkOptionIsSelected() {
        createSettingScreen(uiState = SettingsUiState(themeMode = ThemeMode.DARK))
        composeRule.onNodeWithTag(UiTestTag.settingsThemeOption("Dark")).assertIsSelected()
    }

    @Test
    fun givenLightTheme_whenRendered_thenLightOptionIsSelected() {
        createSettingScreen(uiState = SettingsUiState(themeMode = ThemeMode.LIGHT))
        composeRule.onNodeWithTag(UiTestTag.settingsThemeOption("Light")).assertIsSelected()
    }

    @Test
    fun givenSettingsRendered_whenBackClicked_thenEmitBackCallBack() {
        var backClicked = false
        createSettingScreen(onBack = { backClicked = true})

        composeRule.onNodeWithTag(SETTINGS_TOP_APP_BAR_BACK).performClick()
        assertTrue(backClicked)
    }

    @Test
    fun givenSettingsRendered_whenOnInStockOnlySwitch_thenEmitOnInStockOnlyCallBack() {
        var callBackEmitted = false
        createSettingScreen(
            uiState = SettingsUiState(inStockOnly = false),
            onInStockOnly = { newState -> callBackEmitted = newState }
        )

        composeRule.onNodeWithTag(SETTINGS_IN_STOCK_SWITCH).performClick()
        assertTrue(callBackEmitted)
    }

    @Test
    fun givenLightTheme_whenDarkClicked_thenEmitDarkTheme() {
        var callBackEmitted: ThemeMode? = null
        createSettingScreen(
            uiState = SettingsUiState(inStockOnly = false, themeMode = ThemeMode.LIGHT),
            onThemeMode = { newMode -> callBackEmitted = newMode }
        )

        composeRule.onNodeWithTag(UiTestTag.settingsThemeOption("Dark")).performClick()
        assertEquals(ThemeMode.DARK, callBackEmitted)
    }

    private fun createSettingScreen(
        uiState: SettingsUiState = SettingsUiState(),
        onBack: () -> Unit = {},
        onInStockOnly: (Boolean) -> Unit = {},
        onThemeMode: (ThemeMode) -> Unit = {}
    ) {
        composeRule.setContent {
            SettingsContent(
                uiState = uiState,
                onBack = onBack,
                onInStockOnly = onInStockOnly,
                onThemeMode = onThemeMode
            )
        }
    }

    private fun getString(resourceId: Int) : String = composeRule.activity.getString(resourceId)

}