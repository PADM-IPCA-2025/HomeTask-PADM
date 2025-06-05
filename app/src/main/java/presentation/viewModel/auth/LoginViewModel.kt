package pt.ipca.hometask.presentation.viewModel.auth

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pt.ipca.hometask.data.local.AuthPreferences
import pt.ipca.hometask.data.repository.UserRepositoryImpl
import pt.ipca.hometask.domain.model.User

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val userRepository = UserRepositoryImpl()
    private val authPreferences = AuthPreferences(application.applicationContext)

    var uiState = mutableStateOf(LoginUiState())
        private set

    init {
        // Verificar se já existe usuário logado
        checkExistingLogin()
    }

    private fun checkExistingLogin() {
        if (authPreferences.isLoggedIn()) {
            val user = authPreferences.getUser()
            if (user != null) {
                uiState.value = uiState.value.copy(user = user)
            }
        }
    }

    fun login(email: String, password: String) {
        // Validação básica
        if (email.isBlank() || password.isBlank()) {
            uiState.value = uiState.value.copy(
                error = "Email and password are required"
            )
            return
        }

        uiState.value = uiState.value.copy(
            isLoading = true,
            error = null
        )

        viewModelScope.launch {
            userRepository.login(email, password)
                .onSuccess { user ->
                    // 🔥 SALVAR NO SHARED PREFERENCES
                    authPreferences.saveUserData(user)

                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        user = user,
                        error = null
                    )
                }
                .onFailure { exception ->
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Login failed"
                    )
                }
        }
    }

    fun logout() {
        authPreferences.logout()
        uiState.value = LoginUiState() // Reset do estado
    }

    fun getCurrentUser(): User? {
        return authPreferences.getUser()
    }

    fun forgotPassword(email: String) {
        if (email.isBlank()) {
            uiState.value = uiState.value.copy(
                error = "Email is required for password reset"
            )
            return
        }

        uiState.value = uiState.value.copy(
            isLoading = true,
            error = null
        )

        viewModelScope.launch {
            userRepository.forgotPassword(email)
                .onSuccess {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        forgotPasswordSent = true,
                        error = null
                    )
                }
                .onFailure { exception ->
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Failed to send reset email"
                    )
                }
        }
    }

    fun clearError() {
        uiState.value = uiState.value.copy(error = null)
    }

    fun clearForgotPasswordSent() {
        uiState.value = uiState.value.copy(forgotPasswordSent = false)
    }
}

data class LoginUiState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val error: String? = null,
    val forgotPasswordSent: Boolean = false
)