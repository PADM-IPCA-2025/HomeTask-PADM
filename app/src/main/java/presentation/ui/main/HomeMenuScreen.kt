package presentation.ui.main

import modules.BottomMenuBar
import modules.TaskListItem
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import modules.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pt.ipca.hometask.R
import pt.ipca.hometask.presentation.viewModel.main.HomeMenuViewModel

@Composable
fun HomeMenu(
    viewModel: HomeMenuViewModel,
    onProfile: () -> Unit,
    onShoppingLists: () -> Unit,
    onAddHome: () -> Unit,
    onEditHome: (Int) -> Unit
) {
    val uiState = viewModel.uiState.value

    LaunchedEffect(Unit) {
        viewModel.loadUserHomes(uiState.currentUserId!!)
        viewModel.loadUserTasks(uiState.currentUserId!!)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)
                .padding(bottom = 70.dp)
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Hello, ${uiState.currentUserName ?: "User"}",
                    fontSize = 20.sp,
                    color = colorResource(id = R.color.secondary_blue),
                    fontFamily = FontFamily(Font(R.font.inter_bold))
                )
                
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

            Spacer(modifier = Modifier.height(60.dp))

            Text(
                text = "Homes:",
                fontSize = 20.sp,
                color = colorResource(id = R.color.secondary_blue),
                fontFamily = FontFamily(Font(R.font.inter_bold))
            )

            Spacer(modifier = Modifier.height(22.dp))

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
                            onEdit = { onEditHome(home.id ?: 0) },
                            onDelete = { viewModel.deleteHome(home.id ?: 0) }
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(60.dp))

            Text(
                text = "My Tasks:",
                fontSize = 20.sp,
                color = colorResource(id = R.color.secondary_blue),
                fontFamily = FontFamily(Font(R.font.inter_bold))
            )

            Spacer(modifier = Modifier.height(22.dp))

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
                } else if (uiState.tasks.isEmpty()) {
                    Text(
                        text = "Nenhuma tarefa disponível",
                        color = colorResource(id = R.color.secondary_blue)
                    )
                } else {
                    uiState.tasks.forEach { task ->
                        TaskListItem(
                            taskName = task.title,
                            taskDate = task.date,
                            imageRes = R.drawable.logotipo,
                            isCompleted = task.state == "Completed",
                            onStatusChange = { /* Atualizar status da tarefa */ }
                        )
                        Spacer(modifier = Modifier.height(10.dp))
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
                onHomeClick = { },
                onProfileClick = onProfile
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeMenuPreview() {
    // Preview com dados mockados
    HomeMenu(
        viewModel = HomeMenuViewModel(),
        onProfile = {},
        onShoppingLists = {},
        onAddHome = {},
        onEditHome = {}
    )
}