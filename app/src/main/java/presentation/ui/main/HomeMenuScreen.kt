package presentation.ui.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ShoppingCart
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
import pt.ipca.hometask.R
import pt.ipca.hometask.presentation.viewModel.main.HomeMenuViewModel

@Composable
fun HomeMenuScreen(
    viewModel: HomeMenuViewModel,
    onShoppingCartClick: () -> Unit = {},
    onAddTaskClick: () -> Unit = {},
    onInviteResidentClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(bottom = 220.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Home Menu",
                    fontSize = 24.sp,
                    fontFamily = FontFamily(Font(R.font.poppins_regular)),
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = R.color.main_blue)
                )

                Row {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Shopping Cart",
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { onShoppingCartClick() },
                        tint = colorResource(id = R.color.secondary_blue)
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Task",
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { onAddTaskClick() },
                        tint = colorResource(id = R.color.secondary_blue)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Tasks Section
            Text(
                text = "Tasks",
                fontSize = 20.sp,
                fontFamily = FontFamily(Font(R.font.poppins_regular)),
                fontWeight = FontWeight.Bold,
                color = colorResource(id = R.color.main_blue)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Tasks List
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
                } else {
                    val pendingTasks = uiState.tasks.filter { it.state != "Concluida" }
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
                                isCompleted = task.state == "Concluida",
                                onStatusChange = { isCompleted ->
                                    task.id?.let { id ->
                                        viewModel.updateTaskState(id, if (isCompleted) "Concluida" else "Pendente")
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
                onHomeClick = onHomeClick,
                onProfileClick = onProfileClick
            )
        }
    }
}