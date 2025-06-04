package pt.ipca.hometask.presentation.ui.auth

import modules.CustomButton
import modules.TopBar
import androidx.compose.foundation.background
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
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import pt.ipca.hometask.R
import pt.ipca.hometask.presentation.viewModel.auth.VerifyCodeForgotPasswordViewModel

@Composable
fun VerificationCodeForgotPassword(
    email: String,
    onBackClick: () -> Unit,
    onVerificationSuccess: () -> Unit,
    viewModel: VerifyCodeForgotPasswordViewModel = viewModel()
) {
    var timer by remember { mutableStateOf(30) }
    val code = remember { mutableStateListOf("", "", "", "", "", "") }
    val focusRequesters = List(6) { FocusRequester() }
    val uiState by viewModel.uiState

    // Navegar quando verificação for bem-sucedida
    LaunchedEffect(uiState.isVerificationSuccessful) {
        if (uiState.isVerificationSuccessful) {
            onVerificationSuccess()
        }
    }

    // Resetar timer quando código for reenviado
    LaunchedEffect(uiState.codeResent) {
        if (uiState.codeResent) {
            timer = 30
            viewModel.clearCodeResent()
        }
    }

    // Timer
    LaunchedEffect(timer) {
        if (timer > 0) {
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
                text = "Enter the recovery code we sent to\n$email",
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
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colorResource(id = R.color.secondary_blue),
                            unfocusedBorderColor = Color.Gray,
                            errorBorderColor = Color.Red
                        ),
                        isError = uiState.error != null
                    )
                }
            }

            Spacer(modifier = Modifier.height(15.dp))

            // Mostrar erro se houver
            if (uiState.error != null) {
                Text(
                    text = uiState.error!!,
                    color = Color.Red,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
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
                text = if (uiState.isLoading) "Verifying..." else "Submit",
                onClick = {
                    val fullCode = code.joinToString("")
                    viewModel.verifyForgotPasswordCode(email, fullCode)
                }
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
                            color = if (timer > 0) Color.Gray else colorResource(id = R.color.secondary_blue)
                        )
                    ) {
                        append(if (timer > 0) "Resend (${timer}s)" else "Resend")
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
                    if (offset >= 28 && timer == 0) {
                        viewModel.resendCode(email)
                    }
                }
            )
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