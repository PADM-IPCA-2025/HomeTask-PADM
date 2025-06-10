package presentation.ui.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import modules.TaskListItem
import modules.CustomButton
import modules.BottomMenuBar
import modules.ListItem
import pt.ipca.hometask.R
import pt.ipca.hometask.presentation.viewModel.main.HomeMenuViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun HomeMenuScreen(
    viewModel: HomeMenuViewModel,
    onProfile: () -> Unit = {},
    onAddHome: () -> Unit = {},
    onEditHome: (Int) -> Unit = {},
    onHomeClick: (Int) -> Unit = {}
) {
    val uiState = viewModel.uiState.value

    LaunchedEffect(Unit) {
        viewModel.loadUserHomes(uiState.currentUserId ?: 0)
        viewModel.loadUserTasks(uiState.currentUserId ?: 0)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)
                .padding(bottom = 70.dp)
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // Top Row: Hello, nome + botão adicionar casa
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
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
                        text = uiState.currentUserName ?: "User",
                        fontSize = 20.sp,
                        color = colorResource(id = R.color.secondary_blue),
                        fontFamily = FontFamily(Font(R.font.inter_bold)),
                        fontWeight = FontWeight.Bold
                    )
                }
                if (uiState.userRoles?.contains("Manager", ignoreCase = true) == true) {
                    Icon(
                        imageVector = Icons.Default.AddCircleOutline,
                        contentDescription = "Add Home",
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { onAddHome() },
                        tint = colorResource(id = R.color.secondary_blue)
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "My Homes:",
                fontSize = 20.sp,
                color = colorResource(id = R.color.secondary_blue),
                fontFamily = FontFamily(Font(R.font.inter_bold))
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .height(240.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                if (uiState.isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = colorResource(id = R.color.secondary_blue)
                        )
                    }
                } else if (uiState.homes.isEmpty()) {
                    Text(
                        text = "Você ainda não tem casas cadastradas",
                        color = colorResource(id = R.color.secondary_blue)
                    )
                } else {
                    uiState.homes.forEach { home ->
                        ListItem(
                            houseName = home.name,
                            address = home.address,
                            onEdit = { home.id?.let { onEditHome(it) } },
                            onDelete = {
                                if (uiState.userRoles?.contains("Manager", ignoreCase = true) == true) {
                                    home.id?.let { viewModel.deleteHome(it) }
                                }
                            },
                            onClick = { home.id?.let { onHomeClick(it) } }
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "My Tasks:",
                fontSize = 20.sp,
                color = colorResource(id = R.color.secondary_blue),
                fontFamily = FontFamily(Font(R.font.inter_bold))
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .height(160.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                if (uiState.isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = colorResource(id = R.color.secondary_blue)
                        )
                    }
                } else if (uiState.tasks.isEmpty()) {
                    Text(
                        text = "Nenhuma tarefa disponível",
                        color = colorResource(id = R.color.secondary_blue)
                    )
                } else {
                    val pendingTasks = uiState.tasks.filter { it.state == "Pendente" }
                    if (pendingTasks.isEmpty()) {
                        Text(
                            text = "Nenhuma tarefa pendente",
                            color = colorResource(id = R.color.secondary_blue)
                        )
                    } else {
                        pendingTasks.forEach { task ->
                            TaskListItem(
                                taskName = task.title,
                                taskDate = task.date,
                                imageRes = R.drawable.logotipo,
                                isCompleted = false,
                                onStatusChange = { isCompleted ->
                                    task.id?.let { id ->
                                        android.util.Log.d("HomeMenuScreen", "Atualizando tarefa $id para Concluida")
                                        viewModel.updateTaskState(id, "Concluida")
                                        // Aguardar um momento antes de recarregar para garantir que a atualização foi processada
                                        MainScope().launch {
                                            delay(500)
                                            viewModel.loadUserTasks(uiState.currentUserId ?: 0)
                                        }
                                    }
                                }
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }
                }
            }
        }

        // Bottom Menu
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            BottomMenuBar(
                onHomeClick = { /* Já estamos na tela home */ },
                onProfileClick = onProfile
            )
        }
    }
}