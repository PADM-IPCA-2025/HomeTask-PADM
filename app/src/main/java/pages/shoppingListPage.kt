package pt.ipca.hometask.pages

import modules.TopBar
import modules.ShoppingListItem
import modules.CustomButton
import modules.BottomMenuBar
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pt.ipca.hometask.R

data class ShoppingListData(
    val id: Int,
    val storeName: String,
    val status: String,
    val total: Double,
    val isCompleted: Boolean
)

@Composable
fun ShoppingListsScreen(
    onBackClick: () -> Unit = {},
    onListClick: (ShoppingListData) -> Unit = {},
    onClosestSupermarketClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(0) }

    val progressLists = listOf(
        ShoppingListData(1, "Supermarket Biedronka", "In Progress", 139.0, false)
    )

    val historyLists = listOf(
        ShoppingListData(2, "Supermarket John Doe", "Concluded at 31/12/2024", 15.96, true),
        ShoppingListData(3, "Supermarket Jane Doe", "Concluded at 24/12/2024", 35.21, true)
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(bottom = 140.dp)
        ) {
            TopBar(
                title = "Shopping Lists",
                onBackClick = onBackClick
            )

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

            Spacer(modifier = Modifier.height(24.dp))

            // Content based on selected tab
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                when (selectedTab) {
                    0 -> {
                        // In Progress lists
                        progressLists.forEach { list ->
                            ShoppingListItem(
                                storeName = list.storeName,
                                status = list.status,
                                total = list.total,
                                onClick = { onListClick(list) },
                                showCartIcon = true
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                    1 -> {
                        // History lists
                        historyLists.forEach { list ->
                            ShoppingListItem(
                                storeName = list.storeName,
                                status = list.status,
                                total = list.total,
                                onClick = { onListClick(list) }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }

        // Closest Supermarket Button
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(start = 16.dp, end = 16.dp, bottom = 140.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CustomButton(
                text = "Closest Supermarket",
                onClick = onClosestSupermarketClick
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
fun ShoppingListsScreenPreview() {
    ShoppingListsScreen(
        onBackClick = { /* Voltar */ },
        onListClick = { list -> /* Abrir lista */ },
        onClosestSupermarketClick = { /* Encontrar supermercado */ },
        onHomeClick = { /* Home */ },
        onProfileClick = { /* Profile */ }
    )
}