package presentation.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.lifecycle.viewmodel.compose.viewModel
import modules.TopBar
import modules.ProfilePicture
import modules.CustomTextBox
import modules.CustomButton
import modules.BottomMenuBar
import pt.ipca.hometask.R
import pt.ipca.hometask.presentation.viewModel.profile.EditProfileViewModel

@Composable
fun EditProfilePage(
    onBackClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onSaveClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onEditPhotoClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    viewModel: EditProfileViewModel = viewModel()
) {
    val uiState = viewModel.uiState.value
    var name by remember { mutableStateOf(uiState.currentUser?.name ?: "") }
    var email by remember { mutableStateOf(uiState.currentUser?.email ?: "") }
    var password by remember { mutableStateOf("") }

    // Observar mudanças no estado de logout
    LaunchedEffect(uiState.isLogoutSuccessful) {
        if (uiState.isLogoutSuccessful) {
            onLogoutClick()
            viewModel.clearLogoutSuccess()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            TopBar(
                title = "Preferences",
                onBackClick = onBackClick
            )
            Spacer(modifier = Modifier.height(40.dp))

            // Foto de perfil com botão de editar
            ProfilePicture(
                imageRes = R.drawable.ic_launcher_background,
                size = 120.dp,
                showEditButton = true,
                onEditClick = onEditPhotoClick
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Campo Nome
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                CustomTextBox(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = "Enter your name"
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Campo E-mail
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                CustomTextBox(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = "Enter your email",
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Campo Password
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                CustomTextBox(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = "Enter your password",
                    isPassword = true
                )
            }

            Spacer(modifier = Modifier.height(60.dp))

            // Botão Save Changes
            CustomButton(
                text = "Save Changes",
                onClick = {
                    viewModel.updateProfile(name, email, password)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Botão Logout
            CustomButton(
                text = "Logout",
                onClick = { viewModel.logout() },
                isDanger = true
            )
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            BottomMenuBar(
                onHomeClick = onHomeClick,
                onProfileClick = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditProfilePagePreview() {
    EditProfilePage(
        onBackClick = { },
        onSettingsClick = { },
        onSaveClick = { },
        onLogoutClick = { },
        onEditPhotoClick = { },
        onHomeClick = { }
    )
}