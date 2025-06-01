package pt.ipca.hometask.data.remote.model

import com.google.gson.annotations.SerializedName

data class TaskParticipantDto(
    @SerializedName("taskId") val taskId: Int,
    @SerializedName("userId") val userId: Int
)
