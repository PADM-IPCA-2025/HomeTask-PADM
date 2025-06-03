package pt.ipca.hometask.presentation.viewModel.auth

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pt.ipca.hometask.data.repository.UserRepositoryImpl

class VerificationCodeViewModel : ViewModel() {
    private val userRepository = UserRepositoryImpl()

    var uiState = mutableStateOf(VerificationCodeUiState())
        private set

    fun verifyCode(email: String, code: String) {
        // Validação básica
        if (email.isBlank()) {
            uiState.value = uiState.value.copy(
                error = "Email is required"
            )
            return
        }

        if (code.isBlank() || code.length != 6) {
            uiState.value = uiState.value.copy(
                error = "Please enter a valid 6-digit code"
            )
            return
        }

        if (!code.all { it.isDigit() }) {
            uiState.value = uiState.value.copy(
                error = "Code must contain only numbers"
            )
            return
        }

        uiState.value = uiState.value.copy(
            isLoading = true,
            error = null
        )

        viewModelScope.launch {
            userRepository.verifyCode(email, code)
                .onSuccess {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        isVerificationSuccessful = true,
                        error = null
                    )
                }
                .onFailure { exception ->
                    val errorMessage = when {
                        exception.message?.contains("already verified") == true ->
                            "Account is already verified"
                        exception.message?.contains("Invalid verification code") == true ->
                            "Invalid verification code"
                        exception.message?.contains("expired") == true ->
                            "Verification code has expired"
                        exception.message?.contains("does not exist") == true ->
                            "User not found"
                        else -> exception.message ?: "Verification failed"
                    }

                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        error = errorMessage
                    )
                }
        }
    }

    fun resendCode(email: String) {
        if (email.isBlank()) {
            uiState.value = uiState.value.copy(
                error = "Email is required to resend code"
            )
            return
        }

        uiState.value = uiState.value.copy(
            isLoading = true,
            error = null
        )

        viewModelScope.launch {
            // Usa forgotPassword para reenviar código
            userRepository.forgotPassword(email)
                .onSuccess {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        codeResent = true,
                        error = null
                    )
                }
                .onFailure { exception ->
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Failed to resend code"
                    )
                }
        }
    }

    fun clearError() {
        uiState.value = uiState.value.copy(error = null)
    }

    fun clearCodeResent() {
        uiState.value = uiState.value.copy(codeResent = false)
    }

    fun clearVerificationSuccess() {
        uiState.value = uiState.value.copy(isVerificationSuccessful = false)
    }

    fun resetTimer() {
        uiState.value = uiState.value.copy(
            codeResent = false,
            error = null
        )
    }
}

data class VerificationCodeUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isVerificationSuccessful: Boolean = false,
    val codeResent: Boolean = false
)