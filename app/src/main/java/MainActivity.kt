package pt.ipca.hometask

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
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
import presentation.ui.main.HomeMenuScreen
import presentation.ui.profile.EditProfilePage
import pt.ipca.hometask.presentation.ui.shopping.AddItemScreen
import pt.ipca.hometask.presentation.ui.shopping.ShoppingListScreenContainer
import pt.ipca.hometask.presentation.ui.shopping.ShoppingListsScreenContainer
import presentation.ui.splash.SplashScreen
import pt.ipca.hometask.presentation.viewModel.main.HomeMenuViewModel
import presentation.ui.home.AddEditHouseScreen
import presentation.ui.task.TasksMenuScreen
import presentation.ui.task.AddEditTaskScreen
import presentation.ui.home.InviteResidentScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Log para debug
        Log.d("MainActivity", "Aplicação iniciada")

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
    val isLoggedIn = authRepository.isLoggedIn()
    val startDestination = if (isLoggedIn) {
        Log.d("NavigationRouter", "Usuário logado, iniciando no homeMenu")
        "homeMenu"
    } else {
        Log.d("NavigationRouter", "Usuário não logado, iniciando no splash")
        "splash"
    }

    NavHost(navController = navController, startDestination = startDestination) {

        // ========== AUTH ROUTES ==========
        composable("splash") {
            Log.d("NavigationRouter", "Renderizando SplashScreen")
            SplashScreen(
                onNavigateToLogin = {
                    Log.d("NavigationRouter", "Navegando para login")
                    navController.navigate("login")
                },
                onNavigateToRegister = {
                    Log.d("NavigationRouter", "Navegando para register")
                    navController.navigate("register")
                }
            )
        }

        composable("login") {
            Log.d("NavigationRouter", "Renderizando LoginScreen")
            LoginScreen(
                onNavigateToRegister = {
                    Log.d("NavigationRouter", "Navegando para register desde login")
                    navController.navigate("register")
                },
                onNavigateToRecover = {
                    Log.d("NavigationRouter", "Navegando para recover")
                    navController.navigate("recover")
                },
                onNavigateToMenu = {
                    Log.d("NavigationRouter", "Login bem-sucedido, navegando para homeMenu")
                    navController.navigate("homeMenu") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("register") {
            Log.d("NavigationRouter", "Renderizando RegisterScreen")
            RegisterScreen(
                onNavigateToVerification = { email ->
                    Log.d("NavigationRouter", "Navegando para verification com email: $email")
                    navController.navigate("verification/$email")
                }
            )
        }

        composable("recover") {
            Log.d("NavigationRouter", "Renderizando RecoverPassword")
            RecoverPassword(
                onBackClick = {
                    Log.d("NavigationRouter", "Voltando de recover")
                    navController.popBackStack()
                },
                onContinueClick = { email ->
                    Log.d("NavigationRouter", "Navegando para verification_forgot_password com email: $email")
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
            Log.d("NavigationRouter", "Renderizando VerificationCode para email: $email")
            VerificationCode(
                email = email,
                onBackClick = {
                    Log.d("NavigationRouter", "Voltando de verification")
                    navController.popBackStack()
                },
                onVerificationSuccess = {
                    Log.d("NavigationRouter", "Verificação bem-sucedida, navegando para login")
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
            Log.d("NavigationRouter", "Renderizando VerificationCodeForgotPassword para email: $email")
            VerificationCodeForgotPassword(
                email = email,
                onBackClick = {
                    Log.d("NavigationRouter", "Voltando de verification_forgot_password")
                    navController.popBackStack()
                },
                onVerificationSuccess = {
                    Log.d("NavigationRouter", "Verificação de senha bem-sucedida, navegando para newPassword")
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
            Log.d("NavigationRouter", "Renderizando NewPassword para email: $email")
            NewPassword(
                email = email,
                onContinue = {
                    Log.d("NavigationRouter", "Nova senha definida, navegando para login")
                    navController.navigate("login") {
                        popUpTo("recover") { inclusive = true }
                    }
                },
                onBackClick = {
                    Log.d("NavigationRouter", "Voltando de newPassword")
                    navController.popBackStack()
                }
            )
        }

        // ========== MAIN APP ROUTES ==========
        composable("homeMenu") {
            Log.d("NavigationRouter", "Renderizando HomeMenu")
            val viewModel: HomeMenuViewModel = viewModel()
            
            // Atualizar o estado do usuário no ViewModel
            viewModel.updateUserState(
                isLoggedIn = authRepository.isLoggedIn(),
                userId = authRepository.getUserId(),
                userName = authRepository.getUserName(),
                roles = authRepository.getUserRoles()
            )

            HomeMenuScreen(
                viewModel = viewModel,
                onProfile = {
                    Log.d("NavigationRouter", "Navegando para editProfile")
                    navController.navigate("editProfile")
                },
                onAddHome = {
                    Log.d("NavigationRouter", "Navegando para addHome")
                    navController.navigate("addHome")
                },
                onEditHome = { homeId ->
                    Log.d("NavigationRouter", "Navegando para editHome com ID: $homeId")
                    navController.navigate("editHome/$homeId")
                },
                onHomeClick = { homeId ->
                    Log.d("NavigationRouter", "Navegando para tasks com ID da casa: $homeId")
                    navController.navigate("tasks/$homeId")
                }
            )
        }

        composable("editProfile") {
            Log.d("NavigationRouter", "Renderizando EditProfilePage")
            EditProfilePage(
                onBackClick = {
                    Log.d("NavigationRouter", "Voltando de editProfile")
                    navController.popBackStack()
                },
                onSettingsClick = {
                    // TODO: Implementar configurações
                },
                onSaveClick = {
                    // TODO: Implementar salvamento
                },
                onLogoutClick = {
                    Log.d("NavigationRouter", "Logout realizado, navegando para splash")
                    navController.navigate("splash") {
                        popUpTo("homeMenu") { inclusive = true }
                    }
                },
                onEditPhotoClick = {
                    // TODO: Implementar edição de foto
                },
                onHomeClick = {
                    Log.d("NavigationRouter", "Navegando para homeMenu desde editProfile")
                    navController.navigate("homeMenu") {
                        popUpTo("editProfile") { inclusive = true }
                    }
                }
            )
        }

        composable("tasks/{homeId}") { backStackEntry ->
            val homeId = backStackEntry.arguments?.getString("homeId")?.toIntOrNull() ?: 0
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry("homeMenu")
            }
            val homeMenuViewModel: HomeMenuViewModel = viewModel(parentEntry)
            Log.d("NavigationRouter", "Renderizando TasksMenuScreen para casa ID: $homeId")
            TasksMenuScreen(
                homeId = homeId,
                homeMenuViewModel = homeMenuViewModel,
                navController = navController,
                onShoppingCartClick = {
                    Log.d("NavigationRouter", "Navegando para shoppingLists/$homeId")
                    navController.navigate("shoppingLists/$homeId") {
                        launchSingleTop = true
                    }
                },
                onAddTaskClick = {
                    Log.d("NavigationRouter", "Navegando para add task")
                    navController.navigate("addTask/$homeId")
                },
                onInviteResidentClick = {
                    Log.d("NavigationRouter", "Navegando para invite resident")
                    navController.navigate("inviteResident/$homeId")
                },
                onHomeClick = {
                    Log.d("NavigationRouter", "Voltando para homeMenu")
                    navController.navigate("homeMenu") {
                        popUpTo("tasks/{homeId}") { inclusive = true }
                    }
                },
                onProfileClick = {
                    Log.d("NavigationRouter", "Navegando para editProfile desde tasks")
                    navController.navigate("editProfile")
                }
            )
        }

        // ========== SHOPPING ROUTES ==========
        composable(
            "shoppingLists/{homeId}",
            arguments = listOf(navArgument("homeId") { type = NavType.IntType })
        ) { backStackEntry ->
            val homeId = backStackEntry.arguments?.getInt("homeId") ?: 0
            Log.d("NavigationRouter", "Renderizando ShoppingListsScreenContainer para casa ID: $homeId")
            ShoppingListsScreenContainer(
                homeId = homeId,
                onBackClick = {
                    Log.d("NavigationRouter", "Voltando de shoppingLists")
                    navController.popBackStack()
                },
                onListClick = { listId ->
                    Log.d("NavigationRouter", "Navegando para shopping_list com ID: $listId")
                    navController.navigate("shopping_list/$listId")
                },
                onHomeClick = {
                    Log.d("NavigationRouter", "Navegando para homeMenu desde shoppingLists")
                    navController.navigate("homeMenu") {
                        popUpTo("shoppingLists/$homeId") { inclusive = true }
                    }
                },
                onProfileClick = {
                    Log.d("NavigationRouter", "Navegando para editProfile desde shoppingLists")
                    navController.navigate("editProfile")
                },
                onClosestSupermarketClick = {
                    Log.d("NavigationRouter", "Funcionalidade de supermercado mais próximo ainda não implementada")
                },
                onCreateListClick = {
                    Log.d("NavigationRouter", "Criar lista será feito via dialog no próprio screen")
                },
                onLoginRequired = {
                    Log.w("NavigationRouter", "Login necessário em shoppingLists, redirecionando")
                    navController.navigate("login") {
                        popUpTo("shoppingLists/$homeId") { inclusive = true }
                    }
                }
            )
        }

        composable("shoppingLists") {
            Log.d("NavigationRouter", "Renderizando ShoppingListsScreenContainer")
            ShoppingListsScreenContainer(
                homeId = 0, // Fallback para compatibilidade
                onBackClick = {
                    Log.d("NavigationRouter", "Voltando de shoppingLists")
                    navController.popBackStack()
                },
                onListClick = { listId ->
                    Log.d("NavigationRouter", "Navegando para shopping_list com ID: $listId")
                    navController.navigate("shopping_list/$listId")
                },
                onHomeClick = {
                    Log.d("NavigationRouter", "Navegando para homeMenu desde shoppingLists")
                    navController.navigate("homeMenu") {
                        popUpTo("shoppingLists") { inclusive = true }
                    }
                },
                onProfileClick = {
                    Log.d("NavigationRouter", "Navegando para editProfile desde shoppingLists")
                    navController.navigate("editProfile")
                },
                onClosestSupermarketClick = {
                    Log.d("NavigationRouter", "Funcionalidade de supermercado mais próximo ainda não implementada")
                },
                onCreateListClick = {
                    Log.d("NavigationRouter", "Criar lista será feito via dialog no próprio screen")
                },
                onLoginRequired = {
                    Log.w("NavigationRouter", "Login necessário em shoppingLists, redirecionando")
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
            Log.d("NavigationRouter", "Renderizando ShoppingListScreenContainer para lista ID: $listId")

            if (listId == 0) {
                Log.e("NavigationRouter", "ID da lista inválido (0), redirecionando para shoppingLists")
                navController.navigate("shoppingLists") {
                    popUpTo("shopping_list/$listId") { inclusive = true }
                }
                return@composable
            }

            ShoppingListScreenContainer(
                listId = listId,
                onBackClick = {
                    Log.d("NavigationRouter", "Voltando de shopping_list")
                    navController.popBackStack()
                },
                onAddClick = {
                    Log.d("NavigationRouter", "Navegando para add_item com listId: $listId")
                    navController.navigate("add_item/$listId")
                },
                onHomeClick = {
                    Log.d("NavigationRouter", "Navegando para homeMenu desde shopping_list")
                    navController.navigate("homeMenu") {
                        popUpTo("shopping_list/$listId") { inclusive = true }
                    }
                },
                onProfileClick = {
                    Log.d("NavigationRouter", "Navegando para editProfile desde shopping_list")
                    navController.navigate("editProfile")
                },
                onLoginRequired = {
                    Log.w("NavigationRouter", "Login necessário em shopping_list, redirecionando")
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
            Log.d("NavigationRouter", "Renderizando AddItemScreen para lista ID: $shoppingListId")

            if (shoppingListId == 0) {
                Log.e("NavigationRouter", "ID da lista inválido (0) em add_item, redirecionando para shoppingLists")
                navController.navigate("shoppingLists") {
                    popUpTo("add_item/$shoppingListId") { inclusive = true }
                }
                return@composable
            }

            AddItemScreen(
                shoppingListId = shoppingListId,
                onBackClick = {
                    Log.d("NavigationRouter", "Voltando de add_item")
                    navController.popBackStack()
                },
                onItemSaved = {
                    Log.d("NavigationRouter", "Item salvo, voltando para shopping_list")
                    navController.popBackStack() // Voltar para a lista após salvar
                },
                onHomeClick = {
                    Log.d("NavigationRouter", "Navegando para homeMenu desde add_item")
                    navController.navigate("homeMenu") {
                        popUpTo("add_item/$shoppingListId") { inclusive = true }
                    }
                },
                onProfileClick = {
                    Log.d("NavigationRouter", "Navegando para editProfile desde add_item")
                    navController.navigate("editProfile")
                },
                onLoginRequired = {
                    Log.w("NavigationRouter", "Login necessário em add_item, redirecionando")
                    navController.navigate("login") {
                        popUpTo("add_item/$shoppingListId") { inclusive = true }
                    }
                }
            )
        }

        composable("addHome") {
            // Obtenha o mesmo viewModel usado no HomeMenu
            val viewModel = viewModel<HomeMenuViewModel>()
            AddEditHouseScreen(
                isEditMode = false,
                onBackClick = { navController.popBackStack() },
                onSaveClick = { name, address, zipCode ->
                    viewModel.createHome(name, address, zipCode)
                    navController.popBackStack() // Volta para o menu após criar
                },
                onHomeClick = { navController.navigate("homeMenu") },
                onProfileClick = { navController.navigate("editProfile") }
            )
        }

        composable("addTask/{homeId}") { backStackEntry ->
            val homeId = backStackEntry.arguments?.getString("homeId")?.toIntOrNull() ?: 0
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry("homeMenu")
            }
            val homeMenuViewModel: HomeMenuViewModel = viewModel(parentEntry)
            val userId = homeMenuViewModel.uiState.value.currentUserId
            val addTaskViewModel: pt.ipca.hometask.presentation.viewModel.task.AddTaskViewModel = viewModel()
            AddEditTaskScreen(
                isEditMode = false,
                onBackClick = { navController.popBackStack() },
                onSaveClick = { title, description, group, status, date ->
                    addTaskViewModel.createTask(
                        title = title,
                        description = description,
                        group = group,
                        status = status,
                        date = date,
                        homeId = homeId,
                        userId = userId!!
                    )
                    navController.popBackStack()
                },
                onHomeClick = { navController.navigate("homeMenu") },
                onProfileClick = { navController.navigate("editProfile") }
            )
        }

        composable("inviteResident/{homeId}") { backStackEntry ->
            val homeId = backStackEntry.arguments?.getString("homeId")?.toIntOrNull() ?: 0
            Log.d("InviteResident", "Entrou na tela de inviteResident com homeId=$homeId")
            val inviteResidentViewModel: pt.ipca.hometask.presentation.viewModel.home.InviteResidentViewModel = viewModel()
            LaunchedEffect(Unit) { inviteResidentViewModel.loadAllUsers() }
            InviteResidentScreen(
                onBackClick = { navController.popBackStack() },
                onSendClick = { email -> inviteResidentViewModel.inviteResidentByEmail(email, homeId) },
                onHomeClick = { navController.navigate("homeMenu") },
                onProfileClick = { navController.navigate("editProfile") }
            )
        }
    }
}