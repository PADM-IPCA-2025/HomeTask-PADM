package pages

import modules.TopBar
import modules.CustomTextBox
import modules.CustomButton
import modules.BottomMenuBar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pt.ipca.hometask.R

@Composable
fun InviteResidentScreen(
    onBackClick: () -> Unit = {},
    onSendClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    var emailOrPhone by remember { mutableStateOf("") }

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
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(start = 16.dp, end = 16.dp, bottom = 120.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CustomButton(
                text = "Send",
                onClick = onSendClick
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
    }
}

@Preview(showBackground = true)
@Composable
fun InviteResidentScreenPreview() {
    InviteResidentScreen(
        onBackClick = { /* Voltar */ },
        onSendClick = { /* Enviar convite */ },
        onHomeClick = { /* Home */ },
        onProfileClick = { /* Profile */ }
    )
}