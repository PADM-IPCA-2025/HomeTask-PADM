package pt.ipca.hometask.data.remote.model

import com.google.gson.annotations.SerializedName

data class ShoppingItemDto(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("description") val description: String,
    @SerializedName("quantity") val quantity: Float,
    @SerializedName("state") val state: String,
    @SerializedName("price") val price: Float,
    @SerializedName("shoppingListId") val shoppingListId: Int,
    @SerializedName("itemCategoryId") val itemCategoryId: Int
)
