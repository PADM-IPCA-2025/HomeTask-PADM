package pt.ipca.hometask.presentation.ui.shopping

import modules.TopBar
import modules.CustomTextBox
import modules.CustomButton
import modules.BottomMenuBar
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
    val viewModel = remember { AddItemViewModel(ShoppingRepositoryImpl(context), context) }
    val uiState by viewModel.uiState.collectAsState()
    val itemName by viewModel.itemName.collectAsState()
    val quantity by viewModel.quantity.collectAsState()
    val pricePerUnit by viewModel.pricePerUnit.collectAsState()

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
                .statusBarsPadding()
                .padding(horizontal = 16.dp)
                .padding(bottom = 220.dp)
                .verticalScroll(rememberScrollState())
        ) {
            TopBar(
                title = "Add Item",
                onBackClick = onBackClick
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Item Name
            Text(
                text = "Item Name",
                fontSize = 14.sp,
                color = colorResource(id = R.color.secondary_blue).copy(alpha = 0.7f),
                modifier = Modifier.padding(start = 24.dp, bottom = 8.dp)
            )
            CustomTextBox(
                value = itemName,
                onValueChange = { viewModel.updateItemName(it) },
                placeholder = "Enter item name",
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(start = 24.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Quantity
            Text(
                text = "Quantity",
                fontSize = 14.sp,
                color = colorResource(id = R.color.secondary_blue).copy(alpha = 0.7f),
                modifier = Modifier.padding(start = 24.dp, bottom = 8.dp)
            )
            CustomTextBox(
                value = quantity,
                onValueChange = { viewModel.updateQuantity(it) },
                placeholder = "Enter quantity",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(start = 24.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Price per unit
            Text(
                text = "Price p/ unit",
                fontSize = 14.sp,
                color = colorResource(id = R.color.secondary_blue).copy(alpha = 0.7f),
                modifier = Modifier.padding(start = 24.dp, bottom = 8.dp)
            )
            CustomTextBox(
                value = pricePerUnit,
                onValueChange = { viewModel.updatePricePerUnit(it) },
                placeholder = "Enter price per unit",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(start = 24.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Mostrar informações do usuário se necessário
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
                .padding(start = 16.dp, end = 16.dp, bottom = 80.dp),
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
    // Preview using data mockados
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(top = 48.dp)
                .padding(bottom = 220.dp)
                .verticalScroll(rememberScrollState())
        ) {
            TopBar(
                title = "Add Item",
                onBackClick = { }
            )

            Spacer(modifier = Modifier.height(40.dp))
            Text("Item Name", fontSize = 14.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 8.dp))
            CustomTextBox(value = "Leite", onValueChange = { }, placeholder = "Enter item name")
            Spacer(modifier = Modifier.height(24.dp))
            Text("Quantity", fontSize = 14.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 8.dp))
            CustomTextBox(value = "2", onValueChange = { }, placeholder = "Enter quantity")
            Spacer(modifier = Modifier.height(24.dp))
            Text("Price p/ unit", fontSize = 14.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 8.dp))
            CustomTextBox(value = "1.50", onValueChange = { }, placeholder = "Enter price per unit")
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(start = 16.dp, end = 16.dp, bottom = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CustomButton(text = "Save", onClick = { })
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            BottomMenuBar(onHomeClick = { }, onProfileClick = { })
        }
    }
}