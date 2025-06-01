package pt.ipca.hometask.data.remote.model

import com.google.gson.annotations.SerializedName

data class ResidentDto(
    @SerializedName("homeId") val homeId: Int,
    @SerializedName("userId") val userId: Int
)
