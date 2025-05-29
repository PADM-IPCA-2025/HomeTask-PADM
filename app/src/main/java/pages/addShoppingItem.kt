package pt.ipca.hometask.pages

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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pt.ipca.hometask.R

@Composable
fun AddItemScreen(
    onBackClick: () -> Unit = {},
    onSaveClick: (String, String, String, String) -> Unit = { _, _, _, _ -> },
    onHomeClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    var itemName by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var pricePerUnit by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("") }
    var showCategoryDialog by remember { mutableStateOf(false) }

    val categories = listOf("Fruits", "Vegetables", "Dairy", "Meat", "Bakery", "Other")

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(bottom = 140.dp)
        ) {
            TopBar(
                title = "Item Name",
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
                    onValueChange = { itemName = it },
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
                    onValueChange = { quantity = it },
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
                    onValueChange = { pricePerUnit = it },
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
                        .clickable { showCategoryDialog = true }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (selectedCategory.isEmpty()) "Category" else selectedCategory,
                            fontSize = 16.sp,
                            color = if (selectedCategory.isEmpty())
                                colorResource(id = R.color.secondary_blue).copy(alpha = 0.6f)
                            else
                                colorResource(id = R.color.secondary_blue)
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
                text = "Save",
                onClick = {
                    onSaveClick(itemName, quantity, pricePerUnit, selectedCategory)
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

        // Category Selection Dialog
        if (showCategoryDialog) {
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
                        categories.forEach { category ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedCategory = category
                                        showCategoryDialog = false
                                    }
                                    .padding(vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedCategory == category,
                                    onClick = {
                                        selectedCategory = category
                                        showCategoryDialog = false
                                    },
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = colorResource(id = R.color.secondary_blue)
                                    )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = category,
                                    color = colorResource(id = R.color.secondary_blue)
                                )
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
    }
}

@Preview(showBackground = true)
@Composable
fun AddItemScreenPreview() {
    AddItemScreen(
        onBackClick = { /* Voltar */ },
        onSaveClick = { name, qty, price, category ->
            /* Salvar item */
        },
        onHomeClick = { /* Home */ },
        onProfileClick = { /* Profile */ }
    )
}