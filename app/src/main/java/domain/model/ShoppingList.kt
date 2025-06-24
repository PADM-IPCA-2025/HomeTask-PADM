package pt.ipca.hometask.domain.model

data class ShoppingList(
    val id: Int? = null,
    val title: String? = null,
    val startDate: String? = null,
    val endDate: String? = null,
    val homeId: Int,
    val userId: Int? = null,
    val shoppingItems: List<ShoppingItem>? = null,
    val total: Double? = null
)