package pt.ipca.hometask

import Pages.SplashScreen
import Pages.LoginScreen
import Pages.RecoverPassword
import Pages.RegisterScreen
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
        Log.d("MainActivity", "onCreate called")

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
    Log.d("NavigationRouter", "NavigationRouter composable called")

    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            Log.d("NavigationRouter", "Rendering splash screen")
            SplashScreen(
                onNavigateToLogin = { 
                    Log.d("NavigationRouter", "Navigating to login")
                    navController.navigate("login") 
                },
                onNavigateToRegister = { 
                    Log.d("NavigationRouter", "Navigating to register")
                    navController.navigate("register") 
                }
            )
        }
        composable("login") {
            LoginScreen(
                onNavigateToRegister = { navController.navigate("register") },
                onNavigateToRecover = { navController.navigate("recover") }
            )
        }
        composable("register") {
            Log.d("NavigationRouter", "Rendering register screen")
            RegisterScreen(
                onNavigateToLogin = {
                    Log.d("NavigationRouter", "Navigating to login from register")
                    navController.navigate("login")
                }
            )
        }
        composable("recover") {
            RecoverPassword(
                onBackClick = { navController.popBackStack() },
                onContinueClick = { /* ação de envio de email ou código */ }
            )
        }


    }
}
