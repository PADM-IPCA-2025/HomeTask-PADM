package pt.ipca.hometask.presentation.viewModel.auth

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pt.ipca.hometask.data.repository.UserRepositoryImpl

class NewPasswordViewModel : ViewModel() {
    private val userRepository = UserRepositoryImpl()

    var uiState = mutableStateOf(NewPasswordUiState())
        private set

    fun resetPassword(email: String, password: String, confirmPassword: String) {
        // Validação básica
        if (email.isBlank()) {
            uiState.value = uiState.value.copy(
                error = "Email is required"
            )
            return
        }

        if (password.isBlank()) {
            uiState.value = uiState.value.copy(
                error = "Password is required"
            )
            return
        }

        if (password.length < 6) {
            uiState.value = uiState.value.copy(
                error = "Password must be at least 6 characters"
            )
            return
        }

        if (confirmPassword.isBlank()) {
            uiState.value = uiState.value.copy(
                error = "Please confirm your password"
            )
            return
        }

        if (password != confirmPassword) {
            uiState.value = uiState.value.copy(
                error = "Passwords do not match"
            )
            return
        }

        uiState.value = uiState.value.copy(
            isLoading = true,
            error = null
        )

        viewModelScope.launch {
            userRepository.resetPassword(email, password, confirmPassword)
                .onSuccess {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        isPasswordResetSuccessful = true,
                        error = null
                    )
                }
                .onFailure { exception ->
                    val errorMessage = when {
                        exception.message?.contains("does not exist") == true ->
                            "User not found"
                        exception.message?.contains("invalid") == true ->
                            "Invalid request"
                        exception.message?.contains("expired") == true ->
                            "Reset session has expired"
                        else -> exception.message ?: "Failed to reset password"
                    }

                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        error = errorMessage
                    )
                }
        }
    }

    fun clearError() {
        uiState.value = uiState.value.copy(error = null)
    }

    fun clearPasswordResetSuccess() {
        uiState.value = uiState.value.copy(isPasswordResetSuccessful = false)
    }
}

data class NewPasswordUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isPasswordResetSuccessful: Boolean = false
)