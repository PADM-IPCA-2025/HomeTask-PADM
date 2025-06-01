package presentation.ui.auth

import modules.CustomButton
import modules.CustomTextBox
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pt.ipca.hometask.R

@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(56.dp))

            Image(
                painter = painterResource(id = R.drawable.hometask_slogan),
                contentDescription = "Slogan da app",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .width(195.dp)
                    .height(48.dp)
            )

            Spacer(modifier = Modifier.height(129.dp))

            Text(
                text = "Create Account",
                style = TextStyle(
                    fontSize = 30.sp,
                    fontFamily = FontFamily(Font(R.font.inter_bold)),
                    fontWeight = FontWeight(700),
                    color = colorResource(id = R.color.secondary_blue),
                    letterSpacing = 0.6.sp,
                )
            )

            Spacer(modifier = Modifier.height(15.dp))

            CustomTextBox(
                value = username,
                onValueChange = { username = it },
                placeholder = "Username"
            )

            CustomTextBox(
                value = email,
                onValueChange = { email = it },
                placeholder = "Email"
            )

            CustomTextBox(
                value = password,
                onValueChange = { password = it },
                placeholder = "Password",
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
                text = "Create Account",
                onClick = { /* ação de criação de conta */ }
            )

            Spacer(modifier = Modifier.height(15.dp))

            ClickableText(
                text = buildAnnotatedString {
                    append("Already have an account? ")
                    withStyle(
                        style = SpanStyle(
                            fontFamily = FontFamily(Font(R.font.inter_bold)),
                            fontWeight = FontWeight(700),
                            textDecoration = TextDecoration.Underline,
                            color = colorResource(id = R.color.secondary_blue),
                        )
                    ) {
                        append("Login")
                    }
                },
                style = TextStyle(
                    fontSize = 14.sp,
                    fontFamily = FontFamily(Font(R.font.inter_light)),
                    fontWeight = FontWeight(400),
                    color = Color(0x800A1B1F),
                    letterSpacing = 0.7.sp
                ),
                onClick = { offset ->
                    if (offset >= 26) {
                        onNavigateToLogin()
                    }
                }
            )
        }
    }
}
