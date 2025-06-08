package pt.ipca.hometask.data.remote.model

import com.google.gson.annotations.SerializedName
import pt.ipca.hometask.domain.model.ZipCode

data class HomeDto(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("name") val name: String,
    @SerializedName("address") val address: String,
    @SerializedName("zipCodeId") val zipCodeId: Int,
    @SerializedName("userId") val userId: Int
)


data class CreateHome(
    @SerializedName("name") val name: String,
    @SerializedName("address") val address: String,
    @SerializedName("zipCodeId") val zipCode:  Int,
    @SerializedName("userId") val userId: Int
)