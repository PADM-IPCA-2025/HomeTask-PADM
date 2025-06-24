package pt.ipca.hometask.data.repository

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await
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
                roles = user.roles,
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
            Log.d("UserRepositoryImpl", "🔐 Iniciando login para email: $email")
            
            // Obter o token FCM com tratamento de erro
            val fcmToken = try {
                FirebaseMessaging.getInstance().token.await()
            } catch (e: Exception) {
                Log.w("UserRepositoryImpl", "⚠️ Erro ao obter FCM token, continuando sem token", e)
                null
            }
            Log.d("UserRepositoryImpl", "📱 FCM Token obtido: $fcmToken")

            // Criar request
            val loginRequest = LoginRequest(email, password, fcmToken)
            Log.d("UserRepositoryImpl", "📤 Enviando request: $loginRequest")

            // Fazer login com email, password e token FCM
            val response = api.login(loginRequest)
            Log.d("UserRepositoryImpl", "📥 Response recebida - Status: ${response.code()}, Message: ${response.message()}")
            
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                Log.d("UserRepositoryImpl", "✅ Response body: $apiResponse")

                if (apiResponse.success) {
                    val dto = apiResponse.data
                    Log.d("UserRepositoryImpl", "👤 User DTO: $dto")
                    
                    val user = User(
                        id = dto.id,
                        name = dto.name,
                        email = dto.email,
                        roles = dto.role ?: dto.roles ?: "",
                        profilePicture = dto.profilePicture,
                        token = dto.token,
                        mobileToken = fcmToken
                    )
                    Log.d("UserRepositoryImpl", "🎉 Login bem-sucedido para usuário: ${user.name}")
                    Result.success(user)
                } else {
                    Log.e("UserRepositoryImpl", "❌ API retornou success=false: ${apiResponse.message}")
                    Result.failure(Exception(apiResponse.message))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("UserRepositoryImpl", "❌ Response não bem-sucedida - Status: ${response.code()}, Error Body: $errorBody")
                Result.failure(Exception("Login failed: ${response.message()} - $errorBody"))
            }
        } catch (e: Exception) {
            Log.e("UserRepositoryImpl", "💥 Erro ao fazer login", e)
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
                    val users = apiResponse.data.users.map { dto ->
                        User(
                            id = dto.id,
                            name = dto.name ?: "Sem nome",
                            email = dto.email ?: "sem@email.com",
                            roles = dto.role ?: dto.roles ?: "",
                            profilePicture = dto.profilePicture,
                            token = dto.token
                        )
                    }
                    Result.success(users)
                } else {
                    Result.failure(Exception(apiResponse.message))
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
