package pt.ipca.hometask.domain.model

data class ShoppingItem(
    val id: Int? = null,
    val description: String,
    val quantity: Float,
    val state: String,
    val price: Float,
    val shoppingListId: Int,
    val itemCategoryId: Int
)
