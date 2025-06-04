package pt.ipca.hometask.data.local

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import pt.ipca.hometask.domain.model.User

class AuthPreferences(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val KEY_USER = "user_data"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_ROLES = "user_roles"
        private const val KEY_PROFILE_PICTURE = "profile_picture"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
    }

    fun saveUserData(user: User) {
        sharedPreferences.edit().apply {
            putString(KEY_USER, gson.toJson(user))
            putInt(KEY_USER_ID, user.id ?: 0)
            putString(KEY_USER_EMAIL, user.email)
            putString(KEY_USER_NAME, user.name)
            putString(KEY_USER_ROLES, user.roles)
            putString(KEY_PROFILE_PICTURE, user.profilePicture)
            putBoolean(KEY_IS_LOGGED_IN, true)
            apply()
        }
    }

    fun getUser(): User? {
        return try {
            val userJson = sharedPreferences.getString(KEY_USER, null)
            if (userJson != null) {
                gson.fromJson(userJson, User::class.java)
            } else null
        } catch (e: Exception) {
            null
        }
    }

    fun getUserId(): Int? {
        val userId = sharedPreferences.getInt(KEY_USER_ID, 0)
        return if (userId > 0) userId else null
    }

    fun getUserEmail(): String? {
        return sharedPreferences.getString(KEY_USER_EMAIL, null)
    }

    fun getUserName(): String? {
        return sharedPreferences.getString(KEY_USER_NAME, null)
    }

    fun getUserRoles(): String? {
        return sharedPreferences.getString(KEY_USER_ROLES, null)
    }

    fun getProfilePicture(): String? {
        return sharedPreferences.getString(KEY_PROFILE_PICTURE, null)
    }

    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun updateUserData(
        name: String? = null,
        email: String? = null,
        roles: String? = null,
        profilePicture: String? = null
    ) {
        val currentUser = getUser()
        if (currentUser != null) {
            val updatedUser = currentUser.copy(
                name = name ?: currentUser.name,
                email = email ?: currentUser.email,
                roles = roles ?: currentUser.roles,
                profilePicture = profilePicture ?: currentUser.profilePicture
            )
            saveUserData(updatedUser)
        }
    }

    fun logout() {
        sharedPreferences.edit().clear().apply()
    }
}