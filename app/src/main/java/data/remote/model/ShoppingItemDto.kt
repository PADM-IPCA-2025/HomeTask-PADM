package pt.ipca.hometask.data.remote.model

import com.google.gson.annotations.SerializedName

data class ShoppingItemDto(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("quantity") val quantity: Int = 0,
    @SerializedName("state") val state: String? = null,
    @SerializedName("price") val price: String? = null,
    @SerializedName("shoppingListId") val shoppingListId: Int = 0,
    @SerializedName("itemCategoryId") val itemCategoryId: Int = 0
)
