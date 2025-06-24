package pt.ipca.hometask.domain.model

data class ShoppingItem(
    val id: Int? = null,
    val description: String? = null,
    val quantity: Float = 0f,
    val state: String? = null,
    val price: Float = 0f,
    val shoppingListId: Int = 0,
    val itemCategoryId: Int = 0
)
