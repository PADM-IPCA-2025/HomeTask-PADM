package pt.ipca.hometask.presentation.ui.shopping

import modules.TopBar
import modules.CustomTextBox
import modules.CustomButton
import modules.BottomMenuBar
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pt.ipca.hometask.R
import pt.ipca.hometask.data.repository.ShoppingRepositoryImpl
import pt.ipca.hometask.presentation.viewModel.shopping.AddItemViewModel

@Composable
fun AddItemScreen(
    shoppingListId: Int,
    onBackClick: () -> Unit = {},
    onItemSaved: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onLoginRequired: () -> Unit = {}
) {
    val context = LocalContext.current
    val viewModel = remember { AddItemViewModel(ShoppingRepositoryImpl(), context) }
    val uiState by viewModel.uiState.collectAsState()
    val itemName by viewModel.itemName.collectAsState()
    val quantity by viewModel.quantity.collectAsState()
    val pricePerUnit by viewModel.pricePerUnit.collectAsState()

    var showCategoryDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }

    // Verificar se usuário está logado
    if (!uiState.isUserLoggedIn) {
        LaunchedEffect(Unit) {
            onLoginRequired()
        }
        return
    }

    // Navegar de volta quando item for salvo
    LaunchedEffect(uiState.isItemSaved) {
        if (uiState.isItemSaved) {
            onItemSaved()
        }
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

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(bottom = 140.dp)
        ) {
            TopBar(
                title = "Add Item",
                onBackClick = onBackClick
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Item Name
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                CustomTextBox(
                    value = itemName,
                    onValueChange = { viewModel.updateItemName(it) },
                    placeholder = "Item Name"
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Quantity
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                CustomTextBox(
                    value = quantity,
                    onValueChange = { viewModel.updateQuantity(it) },
                    placeholder = "Quantity",
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Price per unit
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                CustomTextBox(
                    value = pricePerUnit,
                    onValueChange = { viewModel.updatePricePerUnit(it) },
                    placeholder = "Price p/ unit",
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Category Dropdown
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Box(
                    modifier = Modifier
                        .width(328.dp)
                        .height(60.dp)
                        .clickable {
                            if (!uiState.isLoading && uiState.categories.isNotEmpty()) {
                                showCategoryDialog = true
                            }
                        }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = uiState.selectedCategory?.description ?: "Category",
                            fontSize = 16.sp,
                            color = if (uiState.selectedCategory == null)
                                colorResource(id = R.color.secondary_blue).copy(alpha = 0.6f)
                            else
                                colorResource(id = R.color.secondary_blue)
                        )

                        if (uiState.isLoading && uiState.categories.isEmpty()) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = colorResource(id = R.color.secondary_blue),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Dropdown",
                                tint = colorResource(id = R.color.secondary_blue),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    HorizontalDivider(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth(),
                        color = colorResource(id = R.color.secondary_blue),
                        thickness = 1.dp
                    )
                }
            }

            // Mostrar informações do usuário se necessário
            Spacer(modifier = Modifier.height(16.dp))

            uiState.currentUserName?.let { userName ->
                Text(
                    text = "Adding item for: $userName",
                    fontSize = 12.sp,
                    color = colorResource(id = R.color.secondary_blue).copy(alpha = 0.7f),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }

        // Save Button
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(start = 16.dp, end = 16.dp, bottom = 140.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CustomButton(
                text = if (uiState.isLoading) "Saving..." else "Save",
                onClick = {
                    if (!uiState.isLoading && uiState.isFormValid) {
                        viewModel.saveItem(shoppingListId)
                    }
                }
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

        // Loading Overlay
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = colorResource(id = R.color.secondary_blue)
                )
            }
        }

        // Category Selection Dialog
        if (showCategoryDialog && !uiState.isLoading) {
            AlertDialog(
                onDismissRequest = { showCategoryDialog = false },
                title = {
                    Text(
                        text = "Select Category",
                        color = colorResource(id = R.color.secondary_blue)
                    )
                },
                text = {
                    Column {
                        if (uiState.categories.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No categories available",
                                    color = colorResource(id = R.color.secondary_blue).copy(alpha = 0.6f)
                                )
                            }
                        } else {
                            uiState.categories.forEach { category ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            viewModel.selectCategory(category)
                                            showCategoryDialog = false
                                        }
                                        .padding(vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = uiState.selectedCategory?.id == category.id,
                                        onClick = {
                                            viewModel.selectCategory(category)
                                            showCategoryDialog = false
                                        },
                                        colors = RadioButtonDefaults.colors(
                                            selectedColor = colorResource(id = R.color.secondary_blue)
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = category.description,
                                        color = colorResource(id = R.color.secondary_blue)
                                    )
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = { showCategoryDialog = false }
                    ) {
                        Text(
                            text = "Cancel",
                            color = colorResource(id = R.color.secondary_blue)
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

// Container que gerencia o ViewModel
@Composable
fun AddItemScreenContainer(
    shoppingListId: Int,
    onBackClick: () -> Unit = {},
    onItemSaved: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onLoginRequired: () -> Unit = {}
) {
    AddItemScreen(
        shoppingListId = shoppingListId,
        onBackClick = onBackClick,
        onItemSaved = onItemSaved,
        onHomeClick = onHomeClick,
        onProfileClick = onProfileClick,
        onLoginRequired = onLoginRequired
    )
}

@Preview(showBackground = true)
@Composable
fun AddItemScreenPreview() {
    // Preview usando dados mockados
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(bottom = 140.dp)
        ) {
            TopBar(
                title = "Add Item",
                onBackClick = { }
            )

            Spacer(modifier = Modifier.height(40.dp))

            CustomTextBox(
                value = "Leite",
                onValueChange = { },
                placeholder = "Item Name"
            )

            Spacer(modifier = Modifier.height(24.dp))

            CustomTextBox(
                value = "2",
                onValueChange = { },
                placeholder = "Quantity"
            )

            Spacer(modifier = Modifier.height(24.dp))

            CustomTextBox(
                value = "1.50",
                onValueChange = { },
                placeholder = "Price p/ unit"
            )

            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .width(328.dp)
                    .height(60.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Laticínios",
                        fontSize = 16.sp,
                        color = colorResource(id = R.color.secondary_blue)
                    )

                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Dropdown",
                        tint = colorResource(id = R.color.secondary_blue),
                        modifier = Modifier.size(24.dp)
                    )
                }

                HorizontalDivider(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth(),
                    color = colorResource(id = R.color.secondary_blue),
                    thickness = 1.dp
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(start = 16.dp, end = 16.dp, bottom = 140.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CustomButton(
                text = "Save",
                onClick = { }
            )
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