package pt.ipca.hometask.data.remote.api

import pt.ipca.hometask.data.remote.model.*
import retrofit2.Response
import retrofit2.http.*

interface UserAuthApi {

    @POST("api/auth/user")
    suspend fun register(@Body user: UserDto): Response<ApiResponse<UserDto>>

    @POST("api/auth/user/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<ApiResponse<UserDto>> // Com ApiResponse

    @POST("api/auth/user/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): Response<Unit>

    @POST("api/auth/user/verify-code")
    suspend fun verifyCode(@Body request: VerifyCodeRequest): Response<Unit>

    @POST("api/auth/user/verify-code-fp")
    suspend fun verifyCodeForgotPassword(@Body request: VerifyCodeRequest): Response<Unit>

    @POST("api/auth/user/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<Unit>

    @GET("api/auth/user")
    suspend fun getAllUsers(): Response<List<UserDto>>

    @PUT("api/auth/user/{id}")
    suspend fun updateUser(@Path("id") id: Int, @Body user: UserDto): Response<UserDto>

    @DELETE("api/auth/user/{id}")
    suspend fun deleteUser(@Path("id") id: Int): Response<Unit>
}
