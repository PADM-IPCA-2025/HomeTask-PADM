package pt.ipca.hometask.domain.model

data class ShoppingList(
    val id: Int? = null,
    val title: String,
    val startDate: String? = null,
    val endDate: String? = null,
    val homeId: Int
)