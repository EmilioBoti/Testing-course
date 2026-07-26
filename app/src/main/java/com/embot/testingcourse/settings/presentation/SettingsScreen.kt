package com.embot.testingcourse.settings.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.embot.testingcourse.R
import com.embot.testingcourse.core.domain.model.ThemeMode
import com.embot.testingcourse.core.presentation.components.MarketTopAppBar
import com.embot.testingcourse.core.testing.UiTestTag
import com.embot.testingcourse.core.testing.UiTestTag.SETTINGS_IN_STOCK_SWITCH
import com.embot.testingcourse.core.testing.UiTestTag.SETTINGS_SCREEN_CONTENT
import com.embot.testingcourse.core.testing.UiTestTag.SETTINGS_TAX_SWITCH

@Composable
fun SettingsScreen(
    onBack: () -> Unit = {},
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {

    val uiState by settingsViewModel.uiState.collectAsStateWithLifecycle()
    SettingsContent(
        onBack = onBack,
        uiState = uiState,
        onInStockOnly = settingsViewModel::setInStockOnly,
        onThemeMode = settingsViewModel::setThemeMode
    )
}

@Composable
fun SettingsContent(
    uiState: SettingsUiState,
    onBack: () -> Unit = {},
    onInStockOnly: (Boolean) -> Unit = {},
    onThemeMode: (ThemeMode) -> Unit = {}
) {
    Scaffold(
        topBar = {
            MarketTopAppBar(title = stringResource(R.string.settings_title), onBackClick = onBack)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize()
                .testTag(SETTINGS_SCREEN_CONTENT)
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 8.dp
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = stringResource(R.string.settings_filters_section),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                    }
                    HorizontalDivider()

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.settings_in_stock_only),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = stringResource(R.string.settings_in_stock_only_text),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Switch(
                            modifier = Modifier.testTag(SETTINGS_IN_STOCK_SWITCH),
                            checked = uiState.inStockOnly,
                            onCheckedChange = onInStockOnly
                        )

                    }

                    HorizontalDivider()

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.settings_filter_include_taxes_label),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = stringResource(R.string.settings_filter_include_taxes_text),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Switch(
                            modifier = Modifier.testTag(SETTINGS_TAX_SWITCH),
                            checked = true,
                            onCheckedChange = {}
                        )

                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 8.dp
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DarkMode,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = stringResource(R.string.settings_appearance_section),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                    }

                    HorizontalDivider()

                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.settings_theme_label),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = stringResource(R.string.settings_theme_text),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        SingleChoiceSegmentedButtonRow(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            SegmentedButton(
                                modifier = Modifier.testTag(UiTestTag.settingsThemeOption("system")),
                                shape = SegmentedButtonDefaults.itemShape(0, 3),
                                onClick = { onThemeMode(ThemeMode.SYSTEM) },
                                selected = uiState.themeMode == ThemeMode.SYSTEM,
                                label = { Text(
                                    text = stringResource(R.string.theme_system_mode)
                                )}
                            )
                            SegmentedButton(
                                modifier = Modifier.testTag(UiTestTag.settingsThemeOption("dark")),
                                shape = SegmentedButtonDefaults.itemShape(1, 3),
                                onClick = { onThemeMode(ThemeMode.DARK) },
                                selected = uiState.themeMode == ThemeMode.DARK,
                                label = { Text(
                                    text = stringResource(R.string.theme_dark_mode)
                                )}
                            )
                            SegmentedButton(
                                modifier = Modifier.testTag(UiTestTag.settingsThemeOption("light")),
                                shape = SegmentedButtonDefaults.itemShape(2, 3),
                                onClick = { onThemeMode(ThemeMode.LIGHT) },
                                selected = uiState.themeMode == ThemeMode.LIGHT,
                                label = { Text(
                                    text = stringResource(R.string.theme_light_mode)
                                )}
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    SettingsContent(
        uiState = SettingsUiState(),
        onBack = {},
        onInStockOnly = {},
        onThemeMode = {}
    )
}