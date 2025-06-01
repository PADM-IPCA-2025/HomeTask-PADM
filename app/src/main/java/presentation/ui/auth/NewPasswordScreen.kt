package presentation.ui.auth

import modules.CustomButton
import modules.CustomTextBox
import modules.TopBar
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun NewPassword(
    onContinue: () -> Unit,
    onBackClick: () -> Unit
) {
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        // TopBar section
        Column {
            Spacer(modifier = Modifier.height(40.dp))

            TopBar(
                title = "Enter New Password",
                onBackClick = onBackClick
            )
        }

        // Input fields section
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(284.dp))

            CustomTextBox(
                value = newPassword,
                onValueChange = { newPassword = it },
                placeholder = "New Password",
                isPassword = true
            )

            CustomTextBox(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                placeholder = "Confirm Password",
                isPassword = true
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(start = 32.dp, end = 32.dp, bottom = 98.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CustomButton(
                text = "Continue",
                onClick = onContinue,
            )
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
