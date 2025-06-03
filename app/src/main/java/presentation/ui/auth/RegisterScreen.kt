package presentation.ui.auth

import modules.CustomButton
import modules.CustomTextBox
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import androidx.lifecycle.viewmodel.compose.viewModel
import pt.ipca.hometask.R
import pt.ipca.hometask.presentation.viewModel.auth.RegisterViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onNavigateToVerification: (String) -> Unit,
    viewModel: RegisterViewModel = viewModel()
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("Resident") }
    var profileImageUri by remember { mutableStateOf<String?>(null) }

    val uiState by viewModel.uiState

    // Launcher para selecionar imagem
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            profileImageUri = it.toString()
        }
    }

    // Navegar quando registro for bem-sucedido
    LaunchedEffect(uiState.isRegistrationSuccessful) {
        if (uiState.isRegistrationSuccessful) {
            onNavigateToVerification(email) // ← Mudança: passa o email
        }
    }

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

            Spacer(modifier = Modifier.height(80.dp))

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

            Spacer(modifier = Modifier.height(20.dp))

            // Foto de perfil
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .border(2.dp, colorResource(id = R.color.secondary_blue), CircleShape)
                    .clickable { imagePickerLauncher.launch("image/*") }
                    .background(Color.Gray.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                if (profileImageUri != null) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Photo Selected",
                            tint = Color.Green,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "Selected",
                            fontSize = 10.sp,
                            color = Color.Green
                        )
                    }
                } else {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Add Photo",
                        tint = colorResource(id = R.color.secondary_blue),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            CustomTextBox(
                value = name,
                onValueChange = { name = it },
                placeholder = "Full Name"
            )

            CustomTextBox(
                value = email,
                onValueChange = { email = it },
                placeholder = "Email"
            )

            // Dropdown Role fixo
            OutlinedTextField(
                value = selectedRole,
                onValueChange = { },
                label = { Text("Role") },
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colorResource(id = R.color.secondary_blue),
                    unfocusedBorderColor = Color.Gray
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            CustomTextBox(
                value = password,
                onValueChange = { password = it },
                placeholder = "Password",
                isPassword = true
            )

            // Mostrar erro se houver
            if (uiState.error != null) {
                Spacer(modifier = Modifier.height(8.dp))
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
                text = if (uiState.isLoading) "Creating Account..." else "Create Account",
                onClick = {
                    viewModel.register(
                        name = name,
                        email = email,
                        password = password,
                        roles = selectedRole,
                        profilePicture = profileImageUri
                    )
                }
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
                        onNavigateToVerification("") // ← Placeholder, vai para login normalmente
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