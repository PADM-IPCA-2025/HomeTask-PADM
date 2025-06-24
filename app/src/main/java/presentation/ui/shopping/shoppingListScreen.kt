package pt.ipca.hometask.presentation.ui.shopping

import androidx.compose.foundation.background
import modules.TopBar
import modules.BottomMenuBar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pt.ipca.hometask.R
import pt.ipca.hometask.data.repository.ShoppingRepositoryImpl
import pt.ipca.hometask.domain.model.ShoppingItem
import pt.ipca.hometask.presentation.viewModel.shopping.ShoppingListViewModel
import modules.ShoppingItem
import modules.CustomButton

@Composable
fun ShoppingListScreen(
    listId: Int,
    onBackClick: () -> Unit = {},
    onAddClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onLoginRequired: () -> Unit = {}
) {
    val context = LocalContext.current
    val viewModel = remember { ShoppingListViewModel(ShoppingRepositoryImpl(context), context) }
    val uiState by viewModel.uiState.collectAsState()

    var showErrorDialog by remember { mutableStateOf(false) }

    // Carregar lista quando componente for criado
    LaunchedEffect(listId) {
        viewModel.loadShoppingList(listId)
    }

    // Fecha o ecrã quando o save for concluído
    LaunchedEffect(uiState.saveCompleted) {
        if (uiState.saveCompleted) {
            onBackClick()
            viewModel.onSaveCompleted()
        }
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
            if (uiState.errorMessage!!.contains("permissão") ||
                uiState.errorMessage!!.contains("não está logado")) {
                onBackClick()
            } else {
                showErrorDialog = true
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 16.dp)
                .padding(bottom = 170.dp)
        ) {
            TopBar(
                title = uiState.shoppingList?.title ?: "Shopping List",
                onBackClick = onBackClick,
                rightIcon = Icons.Default.Add,
                onRightIconClick = onAddClick
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Store info
            Column {
                Text(
                    text = uiState.shoppingList?.title ?: "Loading...",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = R.color.secondary_blue)
                )

                val progressText = if (uiState.totalItems > 0) {
                    "Progress: ${uiState.completedItems}/${uiState.totalItems} items"
                } else {
                    "No items yet"
                }

                Text(
                    text = progressText,
                    fontSize = 14.sp,
                    color = colorResource(id = R.color.secondary_blue).copy(alpha = 0.7f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Loading state
            if (uiState.isLoading) {
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
                // Items list
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    if (uiState.shoppingItems.isEmpty()) {
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
                                    text = "No items in this list",
                                    fontSize = 16.sp,
                                    color = colorResource(id = R.color.secondary_blue).copy(alpha = 0.6f)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Tap + to add your first item",
                                    fontSize = 14.sp,
                                    color = colorResource(id = R.color.secondary_blue).copy(alpha = 0.4f)
                                )
                            }
                        }
                    } else {
                        uiState.shoppingItems.forEach { item ->
                            ShoppingItem(
                                itemName = item.description ?: "Sem descrição",
                                quantity = item.quantity.toInt(),
                                price = item.price.toDouble(),
                                isCompleted = item.state == "comprado",
                                onStatusChange = { completed ->
                                    viewModel.updateItemStatus(item.id!!, completed)
                                },
                                onQuantityChange = { newQuantity ->
                                    viewModel.updateItemQuantity(item.id!!, newQuantity.toFloat())
                                },
                                onRemoveItem = {
                                    viewModel.removeItem(item.id!!)
                                }
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Summary
            if (!uiState.isLoading && uiState.shoppingItems.isNotEmpty()) {
                HorizontalDivider(
                    color = colorResource(id = R.color.secondary_blue).copy(alpha = 0.3f),
                    thickness = 1.dp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Items:",
                        fontSize = 16.sp,
                        color = colorResource(id = R.color.secondary_blue)
                    )
                    Text(
                        text = uiState.totalItems.toString(),
                        fontSize = 16.sp,
                        color = colorResource(id = R.color.secondary_blue)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Total:",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(id = R.color.secondary_blue)
                    )
                    Text(
                        text = "$${String.format("%.2f", uiState.totalPrice)}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(id = R.color.secondary_blue)
                    )
                }
                
                Spacer(modifier = Modifier.height(20.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Save Button
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(start = 16.dp, end = 16.dp, bottom = 90.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!uiState.isLoading) {
                 Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxWidth().height(70.dp) // Match button height
                ) {
                    if (uiState.isSaving) {
                        CircularProgressIndicator(
                            color = colorResource(id = R.color.secondary_blue)
                        )
                    } else {
                        val buttonEnabled = !uiState.isLoading
                        Box(modifier = Modifier.alpha(if (buttonEnabled) 1f else 0.5f)) {
                            CustomButton(
                                text = "Guardar Alterações",
                                onClick = {
                                    if (buttonEnabled) {
                                        viewModel.saveChanges()
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            BottomMenuBar(
                onHomeClick = onHomeClick,
                onProfileClick = onProfileClick
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

@Composable
fun ShoppingItemCard(
    item: ShoppingItem,
    onStatusChange: (Boolean) -> Unit = {},
    onQuantityChange: (Float) -> Unit = {},
    onRemoveItem: () -> Unit = {}
) {
    val cardColor = if (item.state == "comprado") {
        colorResource(id = R.color.main_blue)
    } else {
        colorResource(id = R.color.main_blue)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min), // To make the divider cover the full height
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox part
            Box(
                modifier = Modifier
                    .background(
                        color = if (item.state == "comprado")
                            colorResource(id = R.color.main_blue)
                        else
                            colorResource(id = R.color.secondary_blue),
                        shape = RoundedCornerShape(
                            topStart = 12.dp,
                            bottomStart = 12.dp
                        )
                    )
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                Checkbox(
                    checked = item.state == "comprado",
                    onCheckedChange = onStatusChange,
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color.White,
                        uncheckedColor = Color.White,
                        checkmarkColor = colorResource(id = R.color.main_blue)
                    )
                )
            }

            // Item details part
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = item.description ?: "Sem descrição",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { onQuantityChange(item.quantity - 1) }) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Decrease quantity",
                            tint = Color.White
                        )
                    }
                    Text(
                        text = item.quantity.toInt().toString(),
                        fontSize = 16.sp,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    IconButton(onClick = { onQuantityChange(item.quantity + 1) }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Increase quantity",
                            tint = Color.White
                        )
                    }
                }
            }

            // Price and Remove button
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.padding(end = 16.dp)
            ) {
                IconButton(onClick = onRemoveItem) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remove item",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "$${String.format("%.2f", item.price)}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

// Container que gerencia o ViewModel
@Composable
fun ShoppingListScreenContainer(
    listId: Int,
    onBackClick: () -> Unit = {},
    onAddClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onLoginRequired: () -> Unit = {}
) {
    ShoppingListScreen(
        listId = listId,
        onBackClick = onBackClick,
        onAddClick = onAddClick,
        onHomeClick = onHomeClick,
        onProfileClick = onProfileClick,
        onLoginRequired = onLoginRequired
    )
}

@Preview(showBackground = true)
@Composable
fun ShoppingListScreenPreview() {
    val mockItems = listOf(
        ShoppingItem(1, "Onions", 3f, "comprado", 13.0f, 1, 1),
        ShoppingItem(2, "Carrots", 1f, "pendente", 4.0f, 1, 1),
        ShoppingItem(3, "Rice", 6f, "comprado", 25.0f, 1, 2)
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(bottom = 70.dp)
        ) {
            TopBar(
                title = "Shopping List",
                onBackClick = { },
                rightIcon = Icons.Default.Add,
                onRightIconClick = { }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Column {
                Text(
                    text = "Weekly Shopping",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = R.color.secondary_blue)
                )
                Text(
                    text = "Progress: 2/3 items",
                    fontSize = 14.sp,
                    color = colorResource(id = R.color.secondary_blue).copy(alpha = 0.7f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                mockItems.forEach { item ->
                    ShoppingItemCard(item = item)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            HorizontalDivider(
                color = colorResource(id = R.color.secondary_blue).copy(alpha = 0.3f),
                thickness = 1.dp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Total:",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = R.color.secondary_blue)
                )
                Text(
                    text = "$42.00",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = R.color.secondary_blue)
                )
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            BottomMenuBar(
                onHomeClick = { },
                onProfileClick = { }
            )
        }
    }
}