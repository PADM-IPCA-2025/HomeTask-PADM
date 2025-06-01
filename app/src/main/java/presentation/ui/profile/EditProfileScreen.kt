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
import modules.TopBar
import modules.ProfilePicture
import modules.CustomTextBox
import modules.CustomButton
import modules.BottomMenuBar
import pt.ipca.hometask.R

@Composable
fun EditProfilePage(
    onBackClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onSaveClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onEditPhotoClick: () -> Unit = {},
    onHomeClick:()-> Unit={}
) {
    // Estados para os campos de texto
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

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
                imageRes = R.drawable.ic_launcher_background, // Substitua pela imagem da Teresa
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
                onClick = onSaveClick
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Botão Logout
            CustomButton(
                text = "Logout",
                onClick = onLogoutClick,
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
        onBackClick = { /* Navegar para trás */ },
        onSettingsClick = { /* Abrir configurações */ },
        onSaveClick = { /* Guardar alterações */ },
        onLogoutClick = { /* Fazer logout */ },
        onEditPhotoClick = { /* Editar foto de perfil */ }
    )
}