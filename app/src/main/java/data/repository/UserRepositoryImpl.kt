package pt.ipca.hometask.data.repository

import pt.ipca.hometask.data.remote.api.UserAuthApi
import pt.ipca.hometask.data.remote.model.*
import pt.ipca.hometask.domain.model.User
import pt.ipca.hometask.domain.repository.UserRepository
import pt.ipca.hometask.network.RetrofitClient

class UserRepositoryImpl : UserRepository {
    private val api: UserAuthApi = RetrofitClient.userAuthApi

    override suspend fun register(user: User, password: String): Result<User> {
        return try {
            val userDto = UserDto(
                name = user.name,
                email = user.email,
                password = password,
                roles = user.roles, // ← Envia "roles" para a API
                profilePicture = user.profilePicture
            )

            val response = api.register(userDto)
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success) {
                    val dto = apiResponse.data
                    Result.success(User(
                        id = dto.id,
                        name = dto.name,
                        email = dto.email,
                        roles = dto.role ?: dto.roles ?: "",
                        profilePicture = dto.profilePicture,
                        token = dto.token
                    ))
                } else {
                    Result.failure(Exception(apiResponse.message))
                }
            } else {
                Result.failure(Exception("Registration failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            val response = api.login(LoginRequest(email, password))
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!

                if (apiResponse.success) {
                    val dto = apiResponse.data
                    Result.success(User(
                        id = dto.id,
                        name = dto.name,
                        email = dto.email,
                        roles = dto.role ?: dto.roles ?: "",
                        profilePicture = dto.profilePicture,
                        token = dto.token
                    ))
                } else {
                    Result.failure(Exception(apiResponse.message))
                }
            } else {
                Result.failure(Exception("Login failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun forgotPassword(email: String): Result<Unit> {
        return try {
            val response = api.forgotPassword(ForgotPasswordRequest(email))
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Forgot password failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun verifyCode(email: String, code: String): Result<Unit> {
        return try {
            val response = api.verifyCode(VerifyCodeRequest(email, code))
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Code verification failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun verifyCodeForgotPassword(email: String, code: String): Result<Unit> {
        return try {
            val response = api.verifyCodeForgotPassword(VerifyCodeRequest(email, code))
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Code verification failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun resetPassword(email: String, password: String, confirmPassword: String): Result<Unit> {
        return try {
            val response = api.resetPassword(ResetPasswordRequest(email, password, confirmPassword))
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Password reset failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAllUsers(): Result<List<User>> {
        return try {
            val response = api.getAllUsers()
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success && apiResponse.data != null) {
                    val user = User(
                        id = apiResponse.data.id,
                        name = apiResponse.data.name,
                        email = apiResponse.data.email,
                        roles = apiResponse.data.role ?: apiResponse.data.roles ?: "",
                        profilePicture = apiResponse.data.profilePicture,
                        token = apiResponse.data.token
                    )
                    Result.success(listOf(user))
                } else {
                    Result.failure(Exception(apiResponse.message ?: "Get users failed"))
                }
            } else {
                Result.failure(Exception("Get users failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateUser(id: Int, user: User): Result<User> {
        return try {
            val userDto = UserDto(
                id = user.id,
                name = user.name,
                email = user.email,
                roles = user.roles,
                profilePicture = user.profilePicture
            )
            val response = api.updateUser(id, userDto)
            if (response.isSuccessful && response.body() != null) {
                val dto = response.body()!!
                Result.success(User(
                    id = dto.id,
                    name = dto.name,
                    email = dto.email,
                    roles = dto.role ?: dto.roles ?: "",
                    profilePicture = dto.profilePicture,
                    token = dto.token
                ))
            } else {
                Result.failure(Exception("Update user failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteUser(id: Int): Result<Unit> {
        return try {
            val response = api.deleteUser(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Delete user failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}