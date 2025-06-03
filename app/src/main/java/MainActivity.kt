package pt.ipca.hometask

import presentation.ui.main.HomeMenu
import presentation.ui.splash.SplashScreen
import presentation.ui.auth.LoginScreen
import presentation.ui.auth.NewPassword
import presentation.ui.auth.RecoverPassword
import presentation.ui.auth.RegisterScreen
import presentation.ui.auth.VerificationCode
import presentation.ui.auth.VerificationCodeForgotPassword
import presentation.ui.profile.EditProfilePage
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import pt.ipca.hometask.ui.theme.HomeTaskTheme

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
                onNavigateToMenu = { navController.navigate("homeMenu") }
            )
        }

        composable("register") {
            Log.d("NavigationRouter", "Rendering register screen")
            RegisterScreen(
                onNavigateToVerification = { email ->
                    navController.navigate("verification/$email")
                }
            )
        }

        composable("recover") {
            RecoverPassword(
                onBackClick = { navController.popBackStack() },
                onContinueClick = { email ->
                    navController.navigate("verification_forgot_password/$email")
                }
            )
        }

        // Verificação para registro normal
        composable(
            "verification/{email}",
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            VerificationCode(
                email = email,
                onBackClick = { navController.popBackStack() },
                onVerificationSuccess = {
                    navController.navigate("login") {
                        popUpTo("register") { inclusive = true }
                    }
                }
            )
        }

        // Verificação para recuperação de senha
        composable(
            "verification_forgot_password/{email}",
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            VerificationCodeForgotPassword(
                email = email,
                onBackClick = { navController.popBackStack() },
                onVerificationSuccess = {
                    navController.navigate("newPassword/$email")
                }
            )
        }

        // NewPassword recebe email como parâmetro
        composable(
            "newPassword/{email}",
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            NewPassword(
                email = email,
                onContinue = {
                    navController.navigate("login") {
                        popUpTo("recover") { inclusive = true }
                    }
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("homeMenu") {
            HomeMenu(
                onProfile = { navController.navigate("editProfile") }
            )
        }

        composable("editProfile") {
            EditProfilePage(
                onHomeClick = { navController.navigate("homeMenu") },
                onBackClick = { navController.navigate("homeMenu") }
            )
        }
    }
}