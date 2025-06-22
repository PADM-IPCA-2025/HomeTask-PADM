package pt.ipca.hometask.presentation.ui.shopping

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pt.ipca.hometask.R
import pt.ipca.hometask.data.repository.ShoppingRepositoryImpl
import pt.ipca.hometask.domain.model.ShoppingList
import pt.ipca.hometask.presentation.viewModel.shopping.ShoppingListsViewModel
import pt.ipca.hometask.presentation.viewModel.shopping.ShoppingListTab
import pt.ipca.hometask.presentation.ui.shopping.ShoppingListsScreen

@Composable
fun ShoppingListsScreenContainer(
    homeId: Int,
    onBackClick: () -> Unit = {},
    onListClick: (Int) -> Unit = {},
    onHomeClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onClosestSupermarketClick: () -> Unit = {},
    onCreateListClick: () -> Unit = {},
    onLoginRequired: () -> Unit = {}
) {
    val context = LocalContext.current
    val viewModel = remember { ShoppingListsViewModel(ShoppingRepositoryImpl(context), context) }
    val uiState by viewModel.uiState.collectAsState()

    var showErrorDialog by remember { mutableStateOf(false) }
    var showCreateListDialog by remember { mutableStateOf(false) }
    var newListTitle by remember { mutableStateOf("") }

    // Carregar listas da casa específica quando o componente for criado
    LaunchedEffect(homeId) {
        viewModel.loadShoppingListsByHome(homeId)
    }

    // Verificar se usuário está logado
    if (!uiState.isUserLoggedIn) {
        LaunchedEffect(Unit) {
            onLoginRequired()
        }
        return
    }

    // Mostrar erro se houver
    LaunchedEffect(uiState.errorMessage) {
        if (uiState.errorMessage != null) {
            if (uiState.errorMessage!!.contains("não está logado")) {
                onLoginRequired()
            } else {
                showErrorDialog = true
            }
        }
    }

    // Quando lista for criada com sucesso
    LaunchedEffect(uiState.isListCreated) {
        if (uiState.isListCreated) {
            viewModel.resetCreatedState()
            showCreateListDialog = false
            newListTitle = ""
            // Mostrar mensagem de sucesso (opcional)
            android.util.Log.d("ShoppingListsScreenContainer", "Shopping list created successfully!")
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        ShoppingListsScreen(
            onBackClick = onBackClick,
            onListClick = onListClick,
            onHomeClick = onHomeClick,
            onProfileClick = onProfileClick,
            onClosestSupermarketClick = onClosestSupermarketClick,
            onAddClick = { showCreateListDialog = true },
            inProgressLists = uiState.inProgressLists.map { it.shoppingList },
            historyLists = uiState.historyLists.map { it.shoppingList },
            selectedTab = uiState.selectedTab,
            onTabSelected = { tab -> viewModel.selectTab(tab) },
            isLoading = uiState.isLoading,
            inProgressTotals = uiState.inProgressLists.map { it.totalPrice.toDouble() },
            historyTotals = uiState.historyLists.map { it.totalPrice.toDouble() }
        )


        // Loading overlay
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = colorResource(id = R.color.secondary_blue)
                )
            }
        }

        // Create List Dialog
        if (showCreateListDialog) {
            AlertDialog(
                onDismissRequest = {
                    showCreateListDialog = false
                    newListTitle = ""
                },
                title = {
                    Text(
                        text = "Create New List",
                        color = colorResource(id = R.color.secondary_blue)
                    )
                },
                text = {
                    Column {
                        Text(
                            text = "Enter a name for your shopping list:",
                            color = colorResource(id = R.color.secondary_blue).copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = newListTitle,
                            onValueChange = { newListTitle = it },
                            label = { Text("List Name") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = colorResource(id = R.color.secondary_blue),
                                focusedLabelColor = colorResource(id = R.color.secondary_blue)
                            )
                        )

                        // Mostrar info do usuário
                        Spacer(modifier = Modifier.height(8.dp))
                        uiState.currentUserName?.let { userName ->
                            Text(
                                text = "Creating for: $userName",
                                fontSize = 12.sp,
                                color = colorResource(id = R.color.secondary_blue).copy(alpha = 0.6f)
                            )
                        }
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (newListTitle.isNotBlank()) {
                                viewModel.createShoppingList(newListTitle.trim(), homeId)
                            }
                        },
                        enabled = newListTitle.isNotBlank() && !uiState.isLoading
                    ) {
                        Text(
                            text = if (uiState.isLoading) "Creating..." else "Create",
                            color = colorResource(id = R.color.secondary_blue)
                        )
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showCreateListDialog = false
                            newListTitle = ""
                        }
                    ) {
                        Text(
                            text = "Cancel",
                            color = colorResource(id = R.color.secondary_blue).copy(alpha = 0.7f)
                        )
                    }
                },
                containerColor = colorResource(id = R.color.background)
            )
        }

        // Error Dialog
        if (showErrorDialog && uiState.errorMessage != null) {
            AlertDialog(
                onDismissRequest = {
                    showErrorDialog = false
                    viewModel.clearError()
                },
                title = {
                    Text(
                        text = "Error",
                        color = colorResource(id = R.color.secondary_blue)
                    )
                },
                text = {
                    Text(
                        text = uiState.errorMessage!!,
                        color = colorResource(id = R.color.secondary_blue)
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showErrorDialog = false
                            viewModel.clearError()
                        }
                    ) {
                        Text(
                            text = "OK",
                            color = colorResource(id = R.color.secondary_blue)
                        )
                    }
                },
                containerColor = colorResource(id = R.color.background)
            )
        }
    }
}

// Função helper para mostrar informações do usuário
@Composable
fun UserInfoHeader(
    userName: String?,
    userEmail: String?,
    onLogout: () -> Unit = {}
) {
    if (userName != null) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = colorResource(id = R.color.secondary_blue).copy(alpha = 0.1f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Welcome, $userName!",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(id = R.color.secondary_blue)
                    )
                    if (userEmail != null) {
                        Text(
                            text = userEmail,
                            fontSize = 12.sp,
                            color = colorResource(id = R.color.secondary_blue).copy(alpha = 0.7f)
                        )
                    }
                }

                TextButton(onClick = onLogout) {
                    Text(
                        text = "Logout",
                        color = colorResource(id = R.color.secondary_blue)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ShoppingListsScreenContainerPreview() {
    // Para preview, usar dados mockados
    val mockInProgressLists = listOf(
        ShoppingList(
            id = 1,
            title = "Weekly Shopping",
            homeId = 1
        ),
        ShoppingList(
            id = 2,
            title = "Party Supplies",
            homeId = 1
        )
    )

    val mockHistoryLists = listOf(
        ShoppingList(
            id = 3,
            title = "Last Week Shopping",
            endDate = "25/01/2025",
            homeId = 1
        )
    )

    Box(modifier = Modifier.fillMaxSize()) {
        pt.ipca.hometask.presentation.ui.shopping.ShoppingListsScreen(
            inProgressLists = mockInProgressLists,
            historyLists = mockHistoryLists,
            selectedTab = ShoppingListTab.IN_PROGRESS,
            onBackClick = { },
            onListClick = { },
            onHomeClick = { },
            onProfileClick = { },
            onClosestSupermarketClick = { },
            onAddClick = { },
            onTabSelected = { },
            isLoading = false
        )
    }
}