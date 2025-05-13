package pt.ipca.hometask

import Pages.HomeMenu
import Pages.SplashScreen
import Pages.LoginScreen
import Pages.NewPassword
import Pages.RecoverPassword
import Pages.RegisterScreen
import Pages.VerificationCode
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import pt.ipca.hometask.ui.theme.HomeTaskTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HomeTaskTheme {
                NavigationRouter()
            }
        }
    }
}

@Composable
fun NavigationRouter() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate("login") 
                },
                onNavigateToRegister = {
                    navController.navigate("register") 
                }
            )
        }
        composable("login") {
            LoginScreen(
                onNavigateToRegister = { navController.navigate("register") },
                onNavigateToRecover = { navController.navigate("recover") },
                onNavigateToMenu = {navController.navigate("homeMenu")}
            )
        }
        composable("register") {
            Log.d("NavigationRouter", "Rendering register screen")
            RegisterScreen(
                onNavigateToLogin = {
                    navController.navigate("login")
                }
            )
        }
        composable("recover") {
            RecoverPassword(
                onBackClick = { navController.popBackStack() },
                onContinueClick = {navController.navigate("verification")}
            )
        }
        composable("verification") {
            VerificationCode(
                onBackClick = { navController.popBackStack() },
                onSubmitClick = {navController.navigate("newPassword")},
                onResendClick = {}
            )
        }
        composable("newPassword") {
            NewPassword(
                onContinue = {navController.navigate("login")},
                onBackClick = { navController.popBackStack() }
            )
        }
        composable("homeMenu") {
            HomeMenu(
            )
        }
    }
}
