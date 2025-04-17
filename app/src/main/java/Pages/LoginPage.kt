package Pages

import Modules.CustomButton
import Modules.CustomTextBox
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
fun LoginScreen(
    onNavigateToRegister: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(56.dp))

        Image(
            painter = painterResource(id = R.drawable.hometask_slogan),
            contentDescription = "Slogan da app",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .padding(0.dp)
                .width(195.dp)
                .height(48.dp)
        )

        Spacer(modifier = Modifier.height(129.dp))

        Text(
            text = "Login",
            style = TextStyle(
                fontSize = 30.sp,
                fontFamily = FontFamily(Font(R.font.inter_bold)),
                fontWeight = FontWeight(700),
                color = colorResource(id = R.color.secondary_blue),
                letterSpacing = 0.6.sp,
            )
        )

        Spacer(modifier = Modifier.height(56.dp))

        Spacer(modifier = Modifier.height(40.dp))

        CustomTextBox(
            value = email,
            onValueChange = { email = it },
            placeholder = "Email"
        )

        Spacer(modifier = Modifier.height(16.dp))

        CustomTextBox(
            value = password,
            onValueChange = { password = it },
            placeholder = "Password",
            isPassword = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .width(328.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            Text(
                text = "Recovery Password",
                style = TextStyle(
                    fontSize = 14.sp,
                    fontFamily = FontFamily(Font(R.font.inter_bold)),
                    fontWeight = FontWeight(400),
                    color = colorResource(id = R.color.secondary_blue),
                    letterSpacing = 0.28.sp,
                    textDecoration = TextDecoration.Underline
                )
            )
        }

        Spacer(modifier = Modifier.height(243.dp))

        CustomButton(
            text = "Login",
            onClick = { /* sua ação aqui */ }
        )

        Spacer(modifier = Modifier.height(32.dp))

        ClickableText(
            text = buildAnnotatedString {
                append("Not a member? ")
                withStyle(
                    style = SpanStyle(
                        fontFamily = FontFamily(Font(R.font.inter_bold)),
                        fontWeight = FontWeight(700),
                        textDecoration = TextDecoration.Underline,
                        color = colorResource(id = R.color.secondary_blue),
                    )
                ) {
                    append("Register now")
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
                if (offset >= 13) {
                    onNavigateToRegister()
                }
            }
        )
    }
}