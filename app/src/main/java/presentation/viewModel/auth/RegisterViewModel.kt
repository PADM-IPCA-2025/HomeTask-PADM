package pt.ipca.hometask.presentation.viewModel.auth

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pt.ipca.hometask.data.repository.UserRepositoryImpl
import pt.ipca.hometask.domain.model.User

class RegisterViewModel : ViewModel() {
    private val userRepository = UserRepositoryImpl()

    var uiState = mutableStateOf(RegisterUiState())
        private set

    fun register(
        name: String,
        email: String,
        password: String,
        roles: String,
        profilePicture: String? = null
    ) {
        // Validação básica
        if (name.isBlank()) {
            uiState.value = uiState.value.copy(
                error = "Name is required"
            )
            return
        }

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

        if (roles.isBlank()) {
            uiState.value = uiState.value.copy(
                error = "Please select a role"
            )
            return
        }

        uiState.value = uiState.value.copy(
            isLoading = true,
            error = null
        )

        viewModelScope.launch {
            val user = User(
                name = name,
                email = email,
                roles = roles,
                profilePicture = profilePicture
            )

            userRepository.register(user, password)
                .onSuccess { registeredUser ->
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        isRegistrationSuccessful = true,
                        user = registeredUser,
                        error = null
                    )
                }
                .onFailure { exception ->
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Registration failed"
                    )
                }
        }
    }

    fun clearError() {
        uiState.value = uiState.value.copy(error = null)
    }

    fun clearRegistrationSuccess() {
        uiState.value = uiState.value.copy(isRegistrationSuccessful = false)
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}

data class RegisterUiState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val error: String? = null,
    val isRegistrationSuccessful: Boolean = false
)