package presentation.ui.auth

import modules.CustomButton
import modules.TopBar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import android.view.KeyEvent.KEYCODE_DEL
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.*
import kotlinx.coroutines.delay
import pt.ipca.hometask.R

@Composable
fun VerificationCode(
    onBackClick: () -> Unit,
    onSubmitClick: () -> Unit,
    onResendClick: () -> Unit
) {
    var timer by remember { mutableStateOf(30) }
    val code = remember { mutableStateListOf("", "", "", "", "", "") }
    val focusRequesters = List(6) { FocusRequester() }

    // Timer
    LaunchedEffect(Unit) {
        while (timer > 0) {
            delay(1000)
            timer--
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        Column{
            Spacer(modifier = Modifier.height(40.dp))

            TopBar(
                title = "Verification Code",
                onBackClick = onBackClick
            )

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "Enter the code from the SMS we sent you",
                fontSize = 16.sp,
                color = Color(0x990A1B1F),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Spacer(modifier = Modifier.height(305.dp))

            Text(
                text = "00:${if (timer < 10) "0$timer" else "$timer"}",
                fontSize = 20.sp,
                fontFamily = FontFamily(Font(R.font.inter_bold)),
                fontWeight = FontWeight.Bold,
                color = colorResource(id = R.color.secondary_blue)
            )

            Spacer(modifier = Modifier.height(15.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                repeat(6) { index ->
                    val currentFocusRequester = focusRequesters[index]
                    val previousFocusRequester = if (index > 0) focusRequesters[index - 1] else null

                    OutlinedTextField(
                        value = code[index],
                        onValueChange = { value ->
                            if (value.length <= 1 && value.all { it.isDigit() }) {
                                code[index] = value
                                if (value.isNotEmpty() && index < 5) {
                                    focusRequesters[index + 1].requestFocus()
                                }
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(60.dp)
                            .focusRequester(currentFocusRequester)
                            .onKeyEvent { event ->
                                if (event.nativeKeyEvent.keyCode == KEYCODE_DEL) {
                                    if (code[index].isEmpty() && index > 0) {
                                        previousFocusRequester?.requestFocus()
                                        code[index - 1] = ""
                                        true
                                    } else {
                                        false
                                    }
                                } else {
                                    false
                                }
                            },
                        singleLine = true,
                        textStyle = TextStyle(
                            textAlign = TextAlign.Center,
                            fontSize = 20.sp
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            }

            Spacer(modifier = Modifier.height(15.dp))
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(start = 32.dp, end = 32.dp, bottom = 98.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CustomButton(
                text = "Submit",
                onClick = onSubmitClick,
            )

            Spacer(modifier = Modifier.height(15.dp))

            ClickableText(
                text = buildAnnotatedString {
                    append("I didn't receive the code! ")
                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily(Font(R.font.inter_bold)),
                            textDecoration = TextDecoration.Underline,
                            color = colorResource(id = R.color.secondary_blue)
                        )
                    ) {
                        append("Resend")
                    }
                },
                style = TextStyle(
                    fontSize = 14.sp,
                    fontFamily = FontFamily(Font(R.font.inter_light)),
                    fontWeight = FontWeight.Normal,
                    color = Color(0x800A1B1F),
                    letterSpacing = 0.7.sp,
                    textAlign = TextAlign.Center
                ),
                onClick = { offset ->
                    if (offset >= 28) {
                        onResendClick()
                    }
                }
            )
        }
    }
}
