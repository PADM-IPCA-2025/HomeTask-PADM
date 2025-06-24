package pt.ipca.hometask.data.remote.model

import com.google.gson.annotations.SerializedName

data class UserDto(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String? = null,
    @SerializedName("roles") val roles: String? = null, // ← Para enviar (REQUEST)
    @SerializedName("role") val role: String? = null,   // ← Para receber (RESPONSE)
    @SerializedName("profilepicture") val profilePicture: String? = null,
    @SerializedName("token") val token: String? = null,
    @SerializedName("verificationcode") val verificationCode: String? = null,
    @SerializedName("codeexpiry") val codeExpiry: String? = null,
    @SerializedName("mobileToken") val mobileToken: String? = null
)

// DTO para a estrutura aninhada da resposta de listagem de utilizadores
data class UsersListDataDto(
    @SerializedName("status") val status: Int,
    @SerializedName("users") val users: List<UserDto>
)

// DTO para a resposta completa de listagem de utilizadores
data class UsersListResponseDto(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: UsersListDataDto
)

data class LoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("mobileToken") val mobileToken: String? = null
)

data class ForgotPasswordRequest(
    @SerializedName("email") val email: String
)

data class VerifyCodeRequest(
    @SerializedName("email") val email: String,
    @SerializedName("code") val code: String
)

data class ResetPasswordRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("confirmPassword") val confirmPassword: String
)