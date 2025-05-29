package pt.ipca.hometask.pages

import modules.TopBar
import modules.ShoppingItem
import modules.BottomMenuBar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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

data class ShoppingItemData(
    val id: Int,
    val name: String,
    var quantity: Int,
    val price: Double,
    var isCompleted: Boolean
)

@Composable
fun ShoppingListScreen(
    onBackClick: () -> Unit = {},
    onAddClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    var shoppingItems by remember {
        mutableStateOf(
            listOf(
                ShoppingItemData(1, "Onions", 3, 13.0, true),
                ShoppingItemData(2, "Carrots", 1, 4.0, false),
                ShoppingItemData(3, "Rice", 6, 25.0, true)
            )
        )
    }

    val totalItems = shoppingItems.size
    val totalPrice = shoppingItems.sumOf { it.quantity * it.price }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(bottom = 70.dp)
        ) {
            TopBar(
                title = "Shopping List",
                onBackClick = onBackClick,
                rightIcon = Icons.Default.Add,
                onRightIconClick = onAddClick
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Store info
            Column {
                Text(
                    text = "Nome da Lista",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = R.color.secondary_blue)
                )
                Text(
                    text = "In progress",
                    fontSize = 14.sp,
                    color = colorResource(id = R.color.secondary_blue).copy(alpha = 0.7f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Items list
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                shoppingItems.forEach { item ->
                    ShoppingItem(
                        itemName = item.name,
                        quantity = item.quantity,
                        price = item.price,
                        isCompleted = item.isCompleted,
                        onStatusChange = { completed ->
                            shoppingItems = shoppingItems.map {
                                if (it.id == item.id) it.copy(isCompleted = completed) else it
                            }
                        },
                        onQuantityChange = { newQuantity ->
                            shoppingItems = shoppingItems.map {
                                if (it.id == item.id) it.copy(quantity = newQuantity) else it
                            }
                        },
                        onRemoveItem = {
                            shoppingItems = shoppingItems.filter { it.id != item.id }
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Summary
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
                    text = totalItems.toString(),
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
                    text = "$${String.format("%.2f", totalPrice)}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = R.color.secondary_blue)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

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

@Preview(showBackground = true)
@Composable
fun ShoppingListScreenPreview() {
    ShoppingListScreen(
        onBackClick = { /* Voltar */ },
        onAddClick = { /* Adicionar item */ },
        onHomeClick = { /* Home */ },
        onProfileClick = { /* Profile */ }
    )
}