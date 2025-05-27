package pages

import modules.CustomButton
import modules.CustomTextBox
import modules.TopBar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pt.ipca.hometask.R

@Composable
fun RecoverPassword(
    onBackClick: () -> Unit,
    onContinueClick: () -> Unit
) {
    var input by remember { mutableStateOf("") }

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
                value = input,
                onValueChange = { input = it },
                placeholder = "Email or Phone Number"
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
                onClick = onContinueClick
            )
            Spacer(modifier = Modifier.height(32.dp))

        }
    }
}
