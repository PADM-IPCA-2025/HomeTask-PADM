package pt.ipca.hometask.presentation.ui.auth

import modules.CustomButton
import modules.CustomTextBox
import modules.TopBar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import pt.ipca.hometask.R
import pt.ipca.hometask.presentation.viewModel.auth.NewPasswordViewModel

@Composable
fun NewPassword(
    email: String,
    onBackClick: () -> Unit,
    onContinue: () -> Unit,
    viewModel: NewPasswordViewModel = viewModel()
) {
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    val uiState by viewModel.uiState

    // Navegar quando reset for bem-sucedido
    LaunchedEffect(uiState.isPasswordResetSuccessful) {
        if (uiState.isPasswordResetSuccessful) {
            onContinue()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        Column {
            Spacer(modifier = Modifier.height(40.dp))

            TopBar(
                title = "Enter new password",
                onBackClick = onBackClick
            )

            Spacer(modifier = Modifier.height(60.dp))

            Text(
                text = "Create a new password for your account",
                fontSize = 16.sp,
                color = Color(0x990A1B1F),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(280.dp))

            CustomTextBox(
                value = newPassword,
                onValueChange = { newPassword = it },
                placeholder = "New password",
                isPassword = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            CustomTextBox(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                placeholder = "Confirm password",
                isPassword = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Mostrar erro se houver
            if (uiState.error != null) {
                Text(
                    text = uiState.error!!,
                    color = Color.Red,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(start = 32.dp, end = 32.dp, bottom = 98.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CustomButton(
                text = if (uiState.isLoading) "Updating..." else "Continue",
                onClick = {
                    viewModel.resetPassword(
                        email = email,
                        password = newPassword,
                        confirmPassword = confirmPassword
                    )
                }
            )
            Spacer(modifier = Modifier.height(32.dp))
        }

        // Loading overlay
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = colorResource(id = R.color.secondary_blue)
                )
            }
        }
    }
}