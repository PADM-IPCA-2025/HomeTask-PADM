package Pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.KeyboardOptions
import Modules.TopBar
import Modules.ProfilePicture
import Modules.CustomTextBox
import Modules.CustomButton
import Modules.BottomMenuBar
import pt.ipca.hometask.R

@Composable
fun EditProfilePage(
    onBackClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onSaveClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onEditPhotoClick: () -> Unit = {}
) {
    // Estados para os campos de texto
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopBar(
                title = "Profile",
                onBackClick = onBackClick,
                rightIcon = Icons.Default.Settings,
                onRightIconClick = onSettingsClick
            )
        },
        containerColor = colorResource(id = R.color.background)    ,
        bottomBar={ BottomMenuBar(onHomeClick = { /* Navegação para home */ }, onProfileClick = { /* Navegação para perfil */ })}
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

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
                androidx.compose.material3.Text(
                    text = "Name",
                    color = colorResource(id = R.color.secondary_blue),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

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
                androidx.compose.material3.Text(
                    text = "E-mail",
                    color = colorResource(id = R.color.secondary_blue),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

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
                androidx.compose.material3.Text(
                    text = "Password",
                    color = colorResource(id = R.color.secondary_blue),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

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