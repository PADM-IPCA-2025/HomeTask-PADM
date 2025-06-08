package pt.ipca.hometask.data.remote.dto

data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: List<T>?
)

data class ZipCodeDto(
    val id: Int?,
    val postalCode: String,
    val city: String,
    val createdAt: String,
    val updatedAt: String
) 