package pt.ipca.hometask.data.remote.model

import com.google.gson.annotations.SerializedName

data class ItemCategoryDto(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("description") val description: String
)
