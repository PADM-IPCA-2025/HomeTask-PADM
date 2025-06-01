package presentation.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import modules.TopBar
import modules.PreferenceItem
import modules.BottomMenuBar
import modules.LanguageSelector
import pt.ipca.hometask.R

@Composable
fun PreferencesPage(
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    var notificationsEnabled by remember { mutableStateOf(true) }
    var darkModeEnabled by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var selectedLanguage by remember { mutableStateOf("English") }

    Scaffold(
        topBar = {
            TopBar(
                title = "Preferences",
                onBackClick = onBackClick
            )
        },
        bottomBar = {
            BottomMenuBar(
                onHomeClick = onHomeClick,
                onProfileClick = onProfileClick
            )
        },
        containerColor = colorResource(id = R.color.background)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(top = 32.dp)
        ) {
            PreferenceItem(
                icon = Icons.Default.Notifications,
                title = "Notifications",
                hasSwitch = true,
                isChecked = notificationsEnabled,
                onCheckedChange = { notificationsEnabled = it }
            )

            PreferenceItem(
                icon = Icons.Default.DarkMode,
                title = "Dark Mode",
                hasSwitch = true,
                isChecked = darkModeEnabled,
                onCheckedChange = { darkModeEnabled = it }
            )

            PreferenceItem(
                icon = Icons.Default.Language,
                title = "Language",
                hasDropdown = true,
                onDropdownClick = { showLanguageDialog = true }
            )
        }

        LanguageSelector(
            isVisible = showLanguageDialog,
            selectedLanguage = selectedLanguage,
            onLanguageSelected = { selectedLanguage = it },
            onDismiss = { showLanguageDialog = false }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreferencesPagePreview() {
    PreferencesPage(
        onBackClick = { /* Voltar */ },
        onHomeClick = { /* Home */ },
        onProfileClick = { /* Profile */ }
    )
}