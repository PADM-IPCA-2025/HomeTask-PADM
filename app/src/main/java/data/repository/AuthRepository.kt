package pt.ipca.hometask.data.repository

import android.content.Context
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await
import pt.ipca.hometask.data.local.AuthPreferences
import pt.ipca.hometask.data.remote.api.UserAuthApi
import pt.ipca.hometask.data.remote.model.LoginRequest
import pt.ipca.hometask.domain.model.User
import pt.ipca.hometask.network.RetrofitClient
import android.util.Log

class AuthRepository(private val context: Context) {
    private val api: UserAuthApi = RetrofitClient.userAuthApi
    private val authPreferences = AuthPreferences(context)

    suspend fun login(email: String, password: String): Result<User> {
        return try {
            // Obter o token FCM
            val fcmToken = FirebaseMessaging.getInstance().token.await()
            Log.d("AuthRepository", "FCM Token obtido: $fcmToken")
            // Fazer login com o token FCM
            val response = api.login(LoginRequest(email, password, fcmToken))
            
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success) {
                    val dto = apiResponse.data
                    val user = User(
                        id = dto.id,
                        name = dto.name,
                        email = dto.email,
                        roles = dto.role ?: dto.roles ?: "",
                        profilePicture = dto.profilePicture,
                        token = dto.token,
                        mobileToken = fcmToken
                    )
                    Log.d("AuthRepository", fcmToken)
                    authPreferences.saveUserData(user)
                    Result.success(user)
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

    fun getCurrentUser(): User? {
        return authPreferences.getUser()
    }

    fun getUserId(): Int? {
        return authPreferences.getUserId()
    }

    fun getUserEmail(): String? {
        return authPreferences.getUserEmail()
    }

    fun getUserName(): String? {
        return authPreferences.getUserName()
    }

    fun getUserRoles(): String? {
        return authPreferences.getUserRoles()
    }

    fun getProfilePicture(): String? {
        return authPreferences.getProfilePicture()
    }

    fun isLoggedIn(): Boolean {
        return authPreferences.isLoggedIn()
    }

    fun logout() {
        authPreferences.logout()
    }

    fun saveUser(user: User) {
        authPreferences.saveUserData(user)
    }

    fun updateUser(
        name: String? = null,
        email: String? = null,
        roles: String? = null,
        profilePicture: String? = null
    ) {
        authPreferences.updateUserData(name, email, roles, profilePicture)
    }

    // Método útil para verificar se é manager/admin
    fun isAdmin(): Boolean {
        return getUserRoles()?.contains("Manager", ignoreCase = true) == true ||
                getUserRoles()?.contains("Admin", ignoreCase = true) == true
    }

    // Método para obter dados essenciais do usuário
    fun getUserInfo(): Triple<Int?, String?, String?> {
        return Triple(getUserId(), getUserName(), getUserEmail())
    }

    companion object {
        // Métodos estáticos para facilitar uso em ViewModels que precisam de Application
        fun getInstance(context: Context): AuthRepository {
            return AuthRepository(context)
        }

        fun isUserLoggedIn(context: Context): Boolean {
            return AuthPreferences(context).isLoggedIn()
        }

        fun getCurrentUserId(context: Context): Int? {
            return AuthPreferences(context).getUserId()
        }

        fun getCurrentUserName(context: Context): String? {
            return AuthPreferences(context).getUserName()
        }
    }
}