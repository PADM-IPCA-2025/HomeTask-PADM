package pt.ipca.hometask

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import pt.ipca.hometask.data.repository.AuthRepository
import pt.ipca.hometask.ui.theme.HomeTaskTheme
import pt.ipca.hometask.presentation.ui.auth.LoginScreen
import pt.ipca.hometask.presentation.ui.auth.NewPassword
import pt.ipca.hometask.presentation.ui.auth.RecoverPassword
import pt.ipca.hometask.presentation.ui.auth.RegisterScreen
import pt.ipca.hometask.presentation.ui.auth.VerificationCode
import pt.ipca.hometask.presentation.ui.auth.VerificationCodeForgotPassword
import presentation.ui.main.HomeMenu
import presentation.ui.profile.EditProfilePage
import pt.ipca.hometask.presentation.ui.shopping.AddItemScreen
import pt.ipca.hometask.presentation.ui.shopping.ShoppingListScreenContainer
import pt.ipca.hometask.presentation.ui.shopping.ShoppingListsScreenContainer
import presentation.ui.splash.SplashScreen

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
    val context = LocalContext.current
    val authRepository = remember { AuthRepository(context) }
    val navController = rememberNavController()

    // Verificar se usuário está logado no início
    val startDestination = if (authRepository.isLoggedIn()) "homeMenu" else "splash"

    NavHost(navController = navController, startDestination = startDestination) {

        // ========== AUTH ROUTES ==========
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
                onNavigateToMenu = {
                    navController.navigate("homeMenu") {
                        popUpTo("login") { inclusive = true }
                    }
                }
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

        // ========== MAIN APP ROUTES ==========
        composable("homeMenu") {
            HomeMenu(
                onProfile = { navController.navigate("editProfile") },
                onShoppingLists = { navController.navigate("shoppingLists") }
            )
        }

        composable("editProfile") {
            EditProfilePage(
                onHomeClick = { navController.navigate("homeMenu") },
                onBackClick = { navController.navigate("homeMenu") }
            )
        }

        // ========== SHOPPING ROUTES ==========
        composable("shoppingLists") {
            ShoppingListsScreenContainer(
                onBackClick = { navController.popBackStack() },
                onListClick = { listId ->
                    navController.navigate("shopping_list/$listId")
                },
                onHomeClick = {
                    navController.navigate("homeMenu") {
                        popUpTo("shoppingLists") { inclusive = true }
                    }
                },
                onProfileClick = { navController.navigate("editProfile") },
                onClosestSupermarketClick = {
                    // TODO: Implementar funcionalidade do supermercado mais próximo
                    // navController.navigate("nearbyStores")
                },
                onCreateListClick = {
                    // Criar lista será feito via dialog no próprio screen
                },
                onLoginRequired = {
                    navController.navigate("login") {
                        popUpTo("shoppingLists") { inclusive = true }
                    }
                }
            )
        }

        composable(
            "shopping_list/{listId}",
            arguments = listOf(navArgument("listId") { type = NavType.IntType })
        ) { backStackEntry ->
            val listId = backStackEntry.arguments?.getInt("listId") ?: 0
            ShoppingListScreenContainer(
                listId = listId,
                onBackClick = { navController.popBackStack() },
                onAddClick = { navController.navigate("add_item/$listId") },
                onHomeClick = {
                    navController.navigate("homeMenu") {
                        popUpTo("shopping_list/$listId") { inclusive = true }
                    }
                },
                onProfileClick = { navController.navigate("editProfile") },
                onLoginRequired = {
                    navController.navigate("login") {
                        popUpTo("shopping_list/$listId") { inclusive = true }
                    }
                }
            )
        }

        composable(
            "add_item/{shoppingListId}",
            arguments = listOf(navArgument("shoppingListId") { type = NavType.IntType })
        ) { backStackEntry ->
            val shoppingListId = backStackEntry.arguments?.getInt("shoppingListId") ?: 0
            AddItemScreen(
                shoppingListId = shoppingListId,
                onBackClick = { navController.popBackStack() },
                onItemSaved = {
                    navController.popBackStack() // Voltar para a lista após salvar
                },
                onHomeClick = {
                    navController.navigate("homeMenu") {
                        popUpTo("add_item/$shoppingListId") { inclusive = true }
                    }
                },
                onProfileClick = { navController.navigate("editProfile") },
                onLoginRequired = {
                    navController.navigate("login") {
                        popUpTo("add_item/$shoppingListId") { inclusive = true }
                    }
                }
            )
        }
    }
}