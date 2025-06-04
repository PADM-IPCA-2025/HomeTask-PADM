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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import pt.ipca.hometask.R
import pt.ipca.hometask.presentation.viewModel.auth.RecoverPasswordViewModel

@Composable
fun RecoverPassword(
    onBackClick: () -> Unit,
    onContinueClick: (String) -> Unit,
    viewModel: RecoverPasswordViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    val uiState by viewModel.uiState

    // Navegar quando código for enviado
    LaunchedEffect(uiState.isCodeSent) {
        if (uiState.isCodeSent) {
            onContinueClick(email)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        Column {
            Spacer(modifier = Modifier.height(40.dp))

            TopBar(
                title = "Recover Password",
                onBackClick = onBackClick
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(345.dp))

            CustomTextBox(
                value = email,
                onValueChange = { email = it },
                placeholder = "Email or Phone Number"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Mostrar erro se houver
            if (uiState.error != null) {
                Text(
                    text = uiState.error!!,
                    color = Color.Red,
                    fontSize = 14.sp,
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
                text = if (uiState.isLoading) "Sending..." else "Continue",
                onClick = {
                    viewModel.sendRecoveryCode(email)
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