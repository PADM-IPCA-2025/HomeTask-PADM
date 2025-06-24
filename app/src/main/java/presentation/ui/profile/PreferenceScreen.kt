package presentation.ui.profile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import modules.TopBar
import modules.PreferenceItem
import modules.BottomMenuBar
import modules.LanguageSelector
import pt.ipca.hometask.R
import android.util.Log
import androidx.compose.material3.Icon

@Composable
fun PreferencesPage(
    currentPhotoUri: String? = null,
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onPhotoChange: (String?) -> Unit = {}
) {
    var notificationsEnabled by remember { mutableStateOf(true) }
    var darkModeEnabled by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var selectedLanguage by remember { mutableStateOf("English") }
    var selectedPhotoUri by remember { mutableStateOf(currentPhotoUri) }

    // Launcher para selecionar imagem da galeria
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            selectedPhotoUri = it.toString()
            onPhotoChange(selectedPhotoUri)
            Log.d("PreferenceScreen", "Foto de perfil selecionada: $selectedPhotoUri")
        }
    }

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
                .verticalScroll(rememberScrollState())
        ) {
            // Profile Photo Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Profile Photo",
                    fontSize = 18.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    color = colorResource(id = R.color.secondary_blue),
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(colorResource(id = R.color.listitem_blue))
                        .clickable { 
                            imagePickerLauncher.launch("image/*")
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (selectedPhotoUri != null) {
                        AsyncImage(
                            model = selectedPhotoUri,
                            contentDescription = "Profile Photo",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.user),
                            contentDescription = "Default Profile Photo",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    
                    // Overlay com ícone de câmara
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(colorResource(id = R.color.secondary_blue)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoCamera,
                            contentDescription = "Change Photo",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                
                Text(
                    text = "Tap to change photo",
                    fontSize = 14.sp,
                    color = colorResource(id = R.color.secondary_blue).copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Other Preferences
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
            
            Spacer(modifier = Modifier.height(40.dp))
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
        currentPhotoUri = null,
        onBackClick = { /* Voltar */ },
        onHomeClick = { /* Home */ },
        onProfileClick = { /* Profile */ },
        onPhotoChange = { /* Mudar foto */ }
    )
}