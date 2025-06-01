package pt.ipca.hometask.data.remote.model

import com.google.gson.annotations.SerializedName

data class ZipCodeDto(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("postalCode") val postalCode: String,
    @SerializedName("city") val city: String
)
