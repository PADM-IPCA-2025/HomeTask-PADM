package pt.ipca.hometask.data.remote.model

import com.google.gson.annotations.SerializedName

data class TaskDto(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("data") val data: String, // Date in string format
    @SerializedName("state") val state: String,
    @SerializedName("photo") val photo: String? = null,
    @SerializedName("homeId") val homeId: Int,
    @SerializedName("userId") val userId: Int,
    @SerializedName("taskCategoryId") val taskCategoryId: Int
)

