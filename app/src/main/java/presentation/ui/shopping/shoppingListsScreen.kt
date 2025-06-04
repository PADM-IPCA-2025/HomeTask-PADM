package pt.ipca.hometask.presentation.ui.shopping

import modules.TopBar
import modules.BottomMenuBar
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pt.ipca.hometask.R
import pt.ipca.hometask.presentation.viewModel.shopping.ShoppingListTab
import pt.ipca.hometask.domain.model.ShoppingList

@Composable
fun ShoppingListsScreen(
    onBackClick: () -> Unit = {},
    onListClick: (Int) -> Unit = {},
    onHomeClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onClosestSupermarketClick: () -> Unit = {},
    inProgressLists: List<ShoppingList> = emptyList(),
    historyLists: List<ShoppingList> = emptyList(),
    selectedTab: ShoppingListTab = ShoppingListTab.IN_PROGRESS,
    onTabSelected: (ShoppingListTab) -> Unit = {},
    isLoading: Boolean = false
) {
    val currentLists = when (selectedTab) {
        ShoppingListTab.IN_PROGRESS -> inProgressLists
        ShoppingListTab.HISTORY -> historyLists
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(bottom = 70.dp)
        ) {
            TopBar(
                title = "Shopping Lists",
                onBackClick = onBackClick
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Tabs
            TabRow(
                selectedTabIndex = if (selectedTab == ShoppingListTab.IN_PROGRESS) 0 else 1,
                modifier = Modifier.fillMaxWidth(),
                containerColor = Color.Transparent,
                divider = {},
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[if (selectedTab == ShoppingListTab.IN_PROGRESS) 0 else 1]),
                        color = colorResource(id = R.color.secondary_blue),
                        height = 2.dp
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == ShoppingListTab.IN_PROGRESS,
                    onClick = { onTabSelected(ShoppingListTab.IN_PROGRESS) },
                    modifier = Modifier.padding(vertical = 12.dp)
                ) {
                    Text(
                        text = "In progress",
                        fontSize = 16.sp,
                        color = if (selectedTab == ShoppingListTab.IN_PROGRESS)
                            colorResource(id = R.color.secondary_blue)
                        else
                            colorResource(id = R.color.secondary_blue).copy(alpha = 0.6f),
                        fontWeight = if (selectedTab == ShoppingListTab.IN_PROGRESS) FontWeight.Bold else FontWeight.Normal
                    )
                }
                Tab(
                    selected = selectedTab == ShoppingListTab.HISTORY,
                    onClick = { onTabSelected(ShoppingListTab.HISTORY) },
                    modifier = Modifier.padding(vertical = 12.dp)
                ) {
                    Text(
                        text = "History",
                        fontSize = 16.sp,
                        color = if (selectedTab == ShoppingListTab.HISTORY)
                            colorResource(id = R.color.secondary_blue)
                        else
                            colorResource(id = R.color.secondary_blue).copy(alpha = 0.6f),
                        fontWeight = if (selectedTab == ShoppingListTab.HISTORY) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Content
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = colorResource(id = R.color.secondary_blue)
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    currentLists.forEach { shoppingList ->
                        ShoppingListCard(
                            shoppingList = shoppingList,
                            isHistory = selectedTab == ShoppingListTab.HISTORY,
                            onClick = { onListClick(shoppingList.id ?: 0) }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    if (currentLists.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = if (selectedTab == ShoppingListTab.IN_PROGRESS)
                                        "No lists in progress"
                                    else
                                        "No completed lists",
                                    color = colorResource(id = R.color.secondary_blue).copy(alpha = 0.6f),
                                    fontSize = 16.sp
                                )
                                if (selectedTab == ShoppingListTab.IN_PROGRESS) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Tap + to create your first list",
                                        color = colorResource(id = R.color.secondary_blue).copy(alpha = 0.4f),
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Closest Supermarket Button
            Button(
                onClick = onClosestSupermarketClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.secondary_blue)
                ),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text(
                    text = "Closest Supermarket",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
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
fun ShoppingListCard(
    shoppingList: ShoppingList,
    isHistory: Boolean = false,
    onClick: () -> Unit = {}
) {
    // Calcular total mockado (você substituirá pela lógica real)
    val mockTotal = when {
        shoppingList.title.contains("Biedronka", ignoreCase = true) -> 139.00
        shoppingList.title.contains("John Doe", ignoreCase = true) -> 15.96
        shoppingList.title.contains("Jane Doe", ignoreCase = true) -> 35.21
        shoppingList.title.contains("Weekly", ignoreCase = true) -> 42.50
        shoppingList.title.contains("Party", ignoreCase = true) -> 87.30
        else -> 25.75
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = shoppingList.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = colorResource(id = R.color.secondary_blue)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = if (isHistory)
                            "Concluded at ${shoppingList.endDate}"
                        else
                            "In Progress",
                        fontSize = 14.sp,
                        color = colorResource(id = R.color.secondary_blue).copy(alpha = 0.7f)
                    )
                }

                if (!isHistory) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Shopping Cart",
                        tint = colorResource(id = R.color.secondary_blue).copy(alpha = 0.7f),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider(
                color = colorResource(id = R.color.secondary_blue).copy(alpha = 0.2f),
                thickness = 1.dp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total :",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = colorResource(id = R.color.secondary_blue)
                )
                Text(
                    text = "$${String.format("%.2f", mockTotal)}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = R.color.secondary_blue)
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F5F5)
@Composable
fun ShoppingListsScreenPreview() {
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
            id = 2,
            title = "Supermarket John Doe",
            endDate = "31/12/2024",
            homeId = 1
        ),
        ShoppingList(
            id = 3,
            title = "Supermarket Jane Doe",
            endDate = "24/12/2024",
            homeId = 1
        )
    )

    ShoppingListsScreen(
        inProgressLists = mockInProgressLists,
        historyLists = mockHistoryLists,
        selectedTab = ShoppingListTab.IN_PROGRESS,
        onBackClick = { /* Voltar */ },
        onListClick = { listId -> /* Navegar para lista $listId */ },
        onHomeClick = { /* Home */ },
        onProfileClick = { /* Profile */ },
        onClosestSupermarketClick = { /* Supermercado mais próximo */ },
        onTabSelected = { /* Trocar aba */ }
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F5F5)
@Composable
fun ShoppingListsScreenHistoryPreview() {
    val mockHistoryLists = listOf(
        ShoppingList(
            id = 2,
            title = "Supermarket John Doe",
            endDate = "31/12/2024",
            homeId = 1
        ),
        ShoppingList(
            id = 3,
            title = "Supermarket Jane Doe",
            endDate = "24/12/2024",
            homeId = 1
        )
    )

    ShoppingListsScreen(
        inProgressLists = emptyList(),
        historyLists = mockHistoryLists,
        selectedTab = ShoppingListTab.HISTORY,
        onBackClick = { /* Voltar */ },
        onListClick = { listId -> /* Navegar para lista $listId */ },
        onHomeClick = { /* Home */ },
        onProfileClick = { /* Profile */ },
        onClosestSupermarketClick = { /* Supermercado mais próximo */ },
        onTabSelected = { /* Trocar aba */ }
    )
}