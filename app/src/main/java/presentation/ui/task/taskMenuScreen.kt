package presentation.ui.task

import modules.TaskListItem
import modules.CustomButton
import modules.BottomMenuBar
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pt.ipca.hometask.R
import pt.ipca.hometask.presentation.viewModel.task.TaskMenuViewModel
import pt.ipca.hometask.presentation.viewModel.task.TaskMenuUiState
import androidx.lifecycle.viewmodel.compose.viewModel
import android.util.Log
import pt.ipca.hometask.presentation.viewModel.main.HomeMenuViewModel
import androidx.navigation.NavController

data class TaskData(
    val id: Int,
    val taskName: String,
    val taskDate: String,
    val imageRes: Int,
    var isCompleted: Boolean
)

@Composable
fun TasksMenuScreen(
    homeId: Int,
    homeMenuViewModel: HomeMenuViewModel,
    viewModel: TaskMenuViewModel = viewModel(),
    navController: NavController? = null,
    onShoppingCartClick: () -> Unit = {},
    onAddTaskClick: () -> Unit = {},
    onInviteResidentClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(0) }
    val uiState = viewModel.uiState.value
    val userName = homeMenuViewModel.uiState.value.currentUserName

    // Carregar tasks da casa ao abrir a tela
    LaunchedEffect(homeId) {
        viewModel.loadTasksByHome(homeId)
    }

    val progressTasks = uiState.tasks.filter { it.state == "Pendente" }
    val historyTasks = uiState.tasks.filter { it.state != "Pendente" }

    Log.d("TasksMenuScreen", "progressTasks: " + progressTasks.joinToString { it.title + " (" + it.state + ")" })
    Log.d("TasksMenuScreen", "historyTasks: " + historyTasks.joinToString { it.title + " (" + it.state + ")" })



    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(bottom = 120.dp)
        ) {
            Spacer(modifier = Modifier.height(40.dp)) // Spacer no início para a ilha
            Spacer(modifier = Modifier.height(24.dp))
            // Header com nome e ícone carrinho
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Hello,",
                        fontSize = 16.sp,
                        color = colorResource(id = R.color.secondary_blue),
                        fontFamily = FontFamily(Font(R.font.inter_light))
                    )
                    Text(
                        text = userName ?: "User",
                        fontSize = 20.sp,
                        color = colorResource(id = R.color.secondary_blue),
                        fontFamily = FontFamily(Font(R.font.inter_bold)),
                        fontWeight = FontWeight.Bold
                    )
                }
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = "Shopping Cart",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onShoppingCartClick() },
                    tint = colorResource(id = R.color.secondary_blue)
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            // Custom Tab Row
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                TabButton(
                    text = "In progress",
                    isSelected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    modifier = Modifier.weight(1f)
                )
                TabButton(
                    text = "History",
                    isSelected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            // Home Tasks header com ícone +
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Home Tasks:",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = R.color.secondary_blue)
                )
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Task",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            navController?.navigate("addTask/$homeId")
                        },
                    tint = colorResource(id = R.color.secondary_blue)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            // Tasks list based on selected tab
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(color = colorResource(id = R.color.secondary_blue))
                } else {
                    when (selectedTab) {
                        0 -> {
                            if (progressTasks.isEmpty()) {
                                Text("No tasks in progress", color = colorResource(id = R.color.secondary_blue))
                            } else {
                                progressTasks.forEach { task ->
                                    TaskListItem(
                                        taskName = task.title,
                                        taskDate = task.date,
                                        imageRes = R.drawable.ic_launcher_background,
                                        isCompleted = false,
                                        onStatusChange = {
                                            val newState = if (task.state == "Pendente") "Concluida" else "Pendente"
                                            viewModel.updateTaskState(task.id!!, newState, homeId)
                                        }
                                    )
                                }
                            }
                        }
                        1 -> {
                            if (historyTasks.isEmpty()) {
                                Text("No tasks in history", color = colorResource(id = R.color.secondary_blue))
                            } else {
                                historyTasks.forEach { task ->
                                    TaskListItem(
                                        taskName = task.title,
                                        taskDate = task.date,
                                        imageRes = R.drawable.ic_launcher_background,
                                        isCompleted = true,
                                        onStatusChange = {
                                            val newState = if (task.state == "Concluida") "Pendente" else "Concluida"
                                            viewModel.updateTaskState(task.id!!, newState, homeId)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(40.dp)) // Spacer extra para não ficar por cima da ilha
            }
        }
        // Invite Resident Button
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(start = 16.dp, end = 16.dp, bottom = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Só mostra o botão se o usuário atual for o dono da casa
            val currentUserId = homeMenuViewModel.uiState.value.currentUserId
            val home = homeMenuViewModel.uiState.value.homes.find { it.id == homeId }
            if (currentUserId == home?.userId) {
                CustomButton(
                    text = "Invite Resident",
                    onClick = {
                        Log.d("InviteResident", "Navegando para inviteResident/$homeId")
                        navController?.navigate("inviteResident/$homeId")
                    }
                )
            }
        }
        // Bottom Menu
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            BottomMenuBar(
                onHomeClick = onHomeClick,
                onProfileClick = onProfileClick
            )
        }
    }
}

@Composable
private fun TabButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = colorResource(id = R.color.secondary_blue)
        )

        Spacer(modifier = Modifier.height(8.dp))

        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            color = if (isSelected)
                colorResource(id = R.color.secondary_blue)
            else
                colorResource(id = R.color.secondary_blue).copy(alpha = 0.3f),
            thickness = if (isSelected) 2.dp else 1.dp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TasksMenuScreenPreview() {
    TasksMenuScreen(
        homeId = 1,
        homeMenuViewModel = viewModel(),
        viewModel = viewModel(),
        onShoppingCartClick = { /* Shopping Cart */ },
        onAddTaskClick = { /* Add Task */ },
        onInviteResidentClick = { /* Invite Resident */ },
        onHomeClick = { /* Home */ },
        onProfileClick = { /* Profile */ }
    )
}