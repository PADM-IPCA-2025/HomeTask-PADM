package pt.ipca.hometask.presentation.ui.shopping

import modules.TopBar
import modules.BottomMenuBar
import modules.CustomButton
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Refresh
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
import androidx.compose.ui.zIndex
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.foundation.layout.IntrinsicSize
import kotlin.math.roundToInt
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
    onAddClick: () -> Unit = {},
    onSlideAction: (ShoppingList, Boolean) -> Unit = { _, _ -> },
    inProgressLists: List<ShoppingList> = emptyList(),
    historyLists: List<ShoppingList> = emptyList(),
    selectedTab: ShoppingListTab = ShoppingListTab.IN_PROGRESS,
    onTabSelected: (ShoppingListTab) -> Unit = {},
    isLoading: Boolean = false,
    inProgressTotals: List<Double> = emptyList(),
    historyTotals: List<Double> = emptyList()
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
                .padding(bottom = 120.dp)
        ) {
            Spacer(modifier = Modifier.height(40.dp))
            Spacer(modifier = Modifier.height(24.dp))
            
            TopBar(
                title = "Shopping Lists",
                onBackClick = onBackClick,
                rightIcon = Icons.Default.Add,
                onRightIconClick = onAddClick
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Custom Tab Row
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                TabButton(
                    text = "In Progress",
                    isSelected = selectedTab == ShoppingListTab.IN_PROGRESS,
                    onClick = { onTabSelected(ShoppingListTab.IN_PROGRESS) },
                    modifier = Modifier.weight(1f)
                )
                TabButton(
                    text = "History",
                    isSelected = selectedTab == ShoppingListTab.HISTORY,
                    onClick = { onTabSelected(ShoppingListTab.HISTORY) },
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

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
                    val currentTotals = when (selectedTab) {
                        ShoppingListTab.IN_PROGRESS -> inProgressTotals
                        ShoppingListTab.HISTORY -> historyTotals
                    }
                    
                    currentLists.forEachIndexed { index, shoppingList ->
                        ShoppingListCard(
                            shoppingList = shoppingList,
                            isHistory = selectedTab == ShoppingListTab.HISTORY,
                            onClick = { onListClick(shoppingList.id ?: 0) },
                            onSlideAction = { list, moveToHistory -> onSlideAction(list, moveToHistory) },
                            total = if (index < currentTotals.size) currentTotals[index] else 0.0
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

            Spacer(modifier = Modifier.height(40.dp))
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Closest Supermarket Button
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(start = 16.dp, end = 16.dp, bottom = 80.dp),
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
fun ShoppingListCard(
    shoppingList: ShoppingList,
    isHistory: Boolean = false,
    onClick: () -> Unit = {},
    onSlideAction: (ShoppingList, Boolean) -> Unit = { _, _ -> },
    total: Double = 0.0
) {
    var offsetX by remember { mutableStateOf(0f) }
    val density = LocalDensity.current
    
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Background action buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .align(Alignment.CenterEnd)
        ) {
            // Button para mover para histórico (quando está em progresso)
            if (!isHistory) {
                Box(
                    modifier = Modifier
                        .background(
                            color = colorResource(id = R.color.secondary_blue),
                            shape = RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "Move to History",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "History",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            } else {
                // Button para mover para progresso (quando está no histórico)
                Box(
                    modifier = Modifier
                        .background(
                            color = colorResource(id = R.color.secondary_blue),
                            shape = RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Move to Progress",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "Progress",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
        
        // Main card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(offsetX.roundToInt(), 0) }
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragEnd = {
                            if (offsetX < -100) {
                                // Slide para a esquerda - executar ação
                                onSlideAction(shoppingList, !isHistory)
                            }
                            offsetX = 0f
                        }
                    ) { _, dragAmount ->
                        offsetX += dragAmount.x
                        // Limitar o slide para a esquerda
                        if (offsetX > 0) offsetX = 0f
                        if (offsetX < -150) offsetX = -150f
                    }
                }
                .clickable { onClick() }
                .zIndex(1f),
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
                            text = shoppingList.title ?: "Untitled List",
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
                        text = "$${String.format("%.2f", total)}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(id = R.color.secondary_blue)
                    )
                }
            }
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
        onAddClick = { /* Adicionar lista */ },
        onTabSelected = { /* Trocar aba */ },
        inProgressTotals = listOf(42.50, 87.30),
        historyTotals = listOf(139.00, 35.21)
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
        onAddClick = { /* Adicionar lista */ },
        onTabSelected = { /* Trocar aba */ },
        inProgressTotals = emptyList(),
        historyTotals = listOf(139.00, 35.21)
    )
}