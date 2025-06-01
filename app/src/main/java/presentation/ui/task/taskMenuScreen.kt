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

data class TaskData(
    val id: Int,
    val taskName: String,
    val taskDate: String,
    val imageRes: Int,
    var isCompleted: Boolean
)

@Composable
fun TasksMenuScreen(
    onShoppingCartClick: () -> Unit = {},
    onAddTaskClick: () -> Unit = {},
    onInviteResidentClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(0) }

    var progressTasks by remember {
        mutableStateOf(
            listOf(
                TaskData(1, "Wash the dishes", "Date: 15/02/2025", R.drawable.ic_launcher_background, false),
                TaskData(2, "Vaccum the floor", "Date: 17/02/2025", R.drawable.ic_launcher_background, false)
            )
        )
    }

    var historyTasks by remember {
        mutableStateOf(
            listOf(
                TaskData(3, "Wash the dishes", "Date: 02/02/2025", R.drawable.ic_launcher_background, true),
                TaskData(4, "Vaccum the floor", "Date: 02/02/2025", R.drawable.ic_launcher_background, true),
                TaskData(5, "Clean the room", "Date: 05/02/2025", R.drawable.ic_launcher_background, true),
                TaskData(6, "Take out the trash", "Date: 07/02/2025", R.drawable.ic_launcher_background, true)
            )
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(bottom = 140.dp)
        ) {
            Spacer(modifier = Modifier.height(60.dp))

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
                        text = "Teresa Lopes",
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

            Spacer(modifier = Modifier.height(32.dp))

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

            Spacer(modifier = Modifier.height(24.dp))

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
                        .clickable { onAddTaskClick() },
                    tint = colorResource(id = R.color.secondary_blue)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tasks list based on selected tab
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                when (selectedTab) {
                    0 -> {
                        // In Progress tasks
                        progressTasks.forEach { task ->
                            TaskListItem(
                                taskName = task.taskName,
                                taskDate = task.taskDate,
                                imageRes = task.imageRes,
                                isCompleted = task.isCompleted,
                                onStatusChange = { completed ->
                                    if (completed) {
                                        // Move to history
                                        progressTasks = progressTasks.filter { it.id != task.id }
                                        historyTasks = historyTasks + task.copy(isCompleted = true)
                                    }
                                }
                            )
                        }
                    }
                    1 -> {
                        // History tasks
                        historyTasks.forEach { task ->
                            TaskListItem(
                                taskName = task.taskName,
                                taskDate = task.taskDate,
                                imageRes = task.imageRes,
                                isCompleted = task.isCompleted,
                                onStatusChange = { completed ->
                                    if (!completed) {
                                        // Move back to progress
                                        historyTasks = historyTasks.filter { it.id != task.id }
                                        progressTasks = progressTasks + task.copy(isCompleted = false)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }

        // Invite Resident Button
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(start = 16.dp, end = 16.dp, bottom = 140.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CustomButton(
                text = "Invite Resident",
                onClick = onInviteResidentClick
            )
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
        onShoppingCartClick = { /* Shopping Cart */ },
        onAddTaskClick = { /* Add Task */ },
        onInviteResidentClick = { /* Invite Resident */ },
        onHomeClick = { /* Home */ },
        onProfileClick = { /* Profile */ }
    )
}