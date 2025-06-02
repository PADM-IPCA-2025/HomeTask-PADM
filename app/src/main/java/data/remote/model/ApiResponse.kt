package pt.ipca.hometask.data.remote.model

import com.google.gson.annotations.SerializedName


data class ApiResponse<T>(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: T,
    @SerializedName("pendingTasks") val pendingTasks: List<Any>? = null
)