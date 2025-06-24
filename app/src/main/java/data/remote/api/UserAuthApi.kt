package pt.ipca.hometask.data.remote.api

import pt.ipca.hometask.data.remote.model.*
import retrofit2.Response
import retrofit2.http.*

interface UserAuthApi {

    @POST("auth/user")
    suspend fun register(@Body user: UserDto): Response<ApiResponse<UserDto>>

    @POST("auth/user/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<ApiResponse<UserDto>> // Com ApiResponse

    @POST("auth/user/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): Response<Unit>

    @POST("auth/user/verify-code")
    suspend fun verifyCode(@Body request: VerifyCodeRequest): Response<Unit>

    @POST("auth/user/verify-code-fp")
    suspend fun verifyCodeForgotPassword(@Body request: VerifyCodeRequest): Response<Unit>

    @POST("auth/user/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<Unit>

    @GET("auth/user")
    suspend fun getAllUsers(): Response<ApiResponse<UsersListDataDto>>

    @PUT("auth/user/{id}")
    suspend fun updateUser(@Path("id") id: Int, @Body user: UserDto): Response<UserDto>

    @DELETE("auth/user/{id}")
    suspend fun deleteUser(@Path("id") id: Int): Response<Unit>
}
