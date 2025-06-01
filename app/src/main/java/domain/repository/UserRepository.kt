package pt.ipca.hometask.domain.repository

import pt.ipca.hometask.domain.model.User

interface UserRepository {
    suspend fun register(user: User, password: String): Result<User>
    suspend fun login(email: String, password: String): Result<User>
    suspend fun forgotPassword(email: String): Result<Unit>
    suspend fun verifyCode(email: String, code: String): Result<Unit>
    suspend fun verifyCodeForgotPassword(email: String, code: String): Result<Unit>
    suspend fun resetPassword(email: String, password: String, confirmPassword: String): Result<Unit>
    suspend fun getAllUsers(): Result<List<User>>
    suspend fun updateUser(id: Int, user: User): Result<User>
    suspend fun deleteUser(id: Int): Result<Unit>
}