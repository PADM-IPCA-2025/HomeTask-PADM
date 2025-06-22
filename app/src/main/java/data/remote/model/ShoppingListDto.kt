package pt.ipca.hometask.data.remote.model

import com.google.gson.annotations.SerializedName

data class ShoppingListDto(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("startDate") val startDate: String? = null,
    @SerializedName("endDate") val endDate: String? = null,
    @SerializedName("homeId") val homeId: Int,
    @SerializedName("shoppingItems") val shoppingItems: List<ShoppingItemDto>? = null,
    @SerializedName("total") val total: Double? = null
)