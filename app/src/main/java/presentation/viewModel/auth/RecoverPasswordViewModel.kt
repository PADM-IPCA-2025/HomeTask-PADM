package pt.ipca.hometask.presentation.viewModel.auth

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pt.ipca.hometask.data.repository.UserRepositoryImpl

class RecoverPasswordViewModel : ViewModel() {
    private val userRepository = UserRepositoryImpl()

    var uiState = mutableStateOf(RecoverPasswordUiState())
        private set

    fun sendRecoveryCode(email: String) {
        // Validação básica
        if (email.isBlank()) {
            uiState.value = uiState.value.copy(
                error = "Email is required"
            )
            return
        }

        if (!isValidEmail(email)) {
            uiState.value = uiState.value.copy(
                error = "Please enter a valid email address"
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
                        isCodeSent = true,
                        error = null
                    )
                }
                .onFailure { exception ->
                    val errorMessage = when {
                        exception.message?.contains("does not exist") == true ->
                            "No account found with this email"
                        exception.message?.contains("not found") == true ->
                            "Email not found"
                        else -> exception.message ?: "Failed to send recovery code"
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

    fun clearCodeSent() {
        uiState.value = uiState.value.copy(isCodeSent = false)
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}

data class RecoverPasswordUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isCodeSent: Boolean = false
)