package pages

import android.util.Log
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pt.ipca.hometask.R

@Composable
fun SplashScreen(
    modifier: Modifier = Modifier,
    onNavigateToLogin: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    var selected by remember { mutableStateOf("register") }
    Log.d("SplashScreen", "SplashScreen composable called with selected: $selected")

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.background))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(266.dp))

                Image(
                    painter = painterResource(id = R.drawable.logotipo),
                    contentDescription = "Logo da app",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(250.dp)
                )

                Spacer(modifier = Modifier.height(169.dp))

                ToggleButtonSwitch(
                    selected = selected,
                    onSelect = { 
                        Log.d("SplashScreen", "ToggleButtonSwitch onSelect called with: $it")
                        selected = it 
                    },
                    onLoginClick = { 
                        Log.d("SplashScreen", "Login button clicked")
                        onNavigateToLogin() 
                    },
                    onRegisterClick = { 
                        Log.d("SplashScreen", "Register button clicked")
                        onNavigateToRegister() 
                    }
                )

                Spacer(modifier = Modifier.height(32.dp))
            }

            Button(
                onClick = {
                    Log.d("SplashScreen", "Continue button clicked with selected: $selected")
                    if (selected == "login") onNavigateToLogin()
                    else onNavigateToRegister()
                },
                shape = RoundedCornerShape(15.dp),
                border = BorderStroke(2.dp, colorResource(id = R.color.secondary_blue)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.washed_blue),
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .padding(bottom = 98.dp)
            ) {
            }
        }
    }
}


@Composable
fun ToggleButtonSwitch(
    selected: String,
    onSelect: (String) -> Unit,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    val buttonWidth = 328.dp
    val itemWidth = buttonWidth / 2

    val transition = updateTransition(targetState = selected, label = "toggleTransition")

    val offsetX by transition.animateDp(
        label = "offsetAnimation",
        transitionSpec = {
            tween(durationMillis = 300, easing = FastOutSlowInEasing)
        }
    ) { state ->
        if (state == "register") 0.dp else itemWidth
    }

    val registerTextColor by transition.animateColor(
        label = "registerTextColor",
        transitionSpec = { tween(300) }
    ) { state ->
        if (state == "register") Color.White else colorResource(id = R.color.secondary_blue)
    }

    val loginTextColor by transition.animateColor(
        label = "loginTextColor",
        transitionSpec = { tween(300) }
    ) { state ->
        if (state == "login") Color.White else colorResource(id = R.color.secondary_blue)
    }

    Box(
        modifier = Modifier
            .width(buttonWidth)
            .height(70.dp)
            .border(BorderStroke(1.dp, colorResource(id = R.color.secondary_blue)), RoundedCornerShape(15.dp))
            .clip(RoundedCornerShape(15.dp))
            .background(Color(0xFFEAF2F4))
    ) {
        Box(
            modifier = Modifier
                .offset(x = offsetX)
                .width(itemWidth)
                .fillMaxHeight()
                .background(colorResource(id = R.color.secondary_blue), RoundedCornerShape(15.dp))
        )

        Row(Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable {
                        if (selected == "register") {
                            onRegisterClick()
                        } else {
                            onSelect("register")
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Register",
                    color = registerTextColor
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable {
                        if (selected == "login") {
                            onLoginClick()
                        } else {
                            onSelect("login")
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Login",
                    color = loginTextColor
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
}
