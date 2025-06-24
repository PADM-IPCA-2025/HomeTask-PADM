package presentation.ui.home

import modules.TopBar
import modules.CustomTextBox
import modules.CustomButton
import modules.BottomMenuBar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.background
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import pt.ipca.hometask.presentation.viewModel.home.InviteResidentViewModel

@Composable
fun InviteResidentScreen(
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    homeId: Int,
    viewModel: InviteResidentViewModel = viewModel()
) {
    var emailOrPhone by remember { mutableStateOf("") }
    val isLoading = viewModel.isLoading.value
    val errorMessage = viewModel.errorMessage.value
    val inviteSuccess = viewModel.inviteSuccess.value

    // Carregar utilizadores quando a tela é exibida
    LaunchedEffect(Unit) {
        viewModel.loadAllUsers()
    }

    // Navegar de volta quando o convite for bem-sucedido
    LaunchedEffect(inviteSuccess) {
        if (inviteSuccess) {
            onBackClick()
            viewModel.clearInviteSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            TopBar(
                title = "Invite Resident",
                onBackClick = onBackClick
            )

            Spacer(modifier = Modifier.height(280.dp))

            CustomTextBox(
                value = emailOrPhone,
                onValueChange = { emailOrPhone = it },
                placeholder = "E-mail or Phone number",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email
                )
            )

            // Mostrar erro se houver
            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                androidx.compose.material3.Text(
                    text = errorMessage!!,
                    color = androidx.compose.ui.graphics.Color.Red,
                    fontSize = 14.sp
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(start = 16.dp, end = 16.dp, bottom = 120.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CustomButton(
                text = if (isLoading) "Sending..." else "Send",
                onClick = { 
                    if (!isLoading) {
                        viewModel.inviteResidentByEmail(emailOrPhone, homeId)
                    }
                }
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            BottomMenuBar(
                onHomeClick = onHomeClick,
                onProfileClick = onProfileClick
            )
        }

        // Loading overlay
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.material3.CircularProgressIndicator()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun InviteResidentScreenPreview() {
    InviteResidentScreen(
        onBackClick = { /* Voltar */ },
        onHomeClick = { /* Home */ },
        onProfileClick = { /* Profile */ },
        homeId = 0
    )
}