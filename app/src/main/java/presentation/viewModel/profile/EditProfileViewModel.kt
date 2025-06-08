package pt.ipca.hometask.presentation.viewModel.profile

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pt.ipca.hometask.data.repository.AuthRepository
import pt.ipca.hometask.data.repository.UserRepositoryImpl
import pt.ipca.hometask.domain.model.User

data class EditProfileUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isLogoutSuccessful: Boolean = false,
    val currentUser: User? = null
)

class EditProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val authRepository = AuthRepository(application.applicationContext)
    private val userRepository = UserRepositoryImpl()

    var uiState = mutableStateOf(EditProfileUiState())
        private set

    init {
        loadCurrentUser()
    }

    private fun loadCurrentUser() {
        val user = authRepository.getCurrentUser()
        uiState.value = uiState.value.copy(currentUser = user)
    }

    fun updateProfile(name: String, email: String, password: String) {
        if (name.isBlank() || email.isBlank()) {
            uiState.value = uiState.value.copy(
                error = "Name and email are required"
            )
            return
        }

        uiState.value = uiState.value.copy(
            isLoading = true,
            error = null
        )

        viewModelScope.launch {
            val currentUser = uiState.value.currentUser
            if (currentUser != null) {
                val updatedUser = currentUser.copy(
                    name = name,
                    email = email
                )

                userRepository.updateUser(currentUser.id!!, updatedUser)
                    .onSuccess { user ->
                        authRepository.updateUser(
                            name = user.name,
                            email = user.email
                        )
                        uiState.value = uiState.value.copy(
                            isLoading = false,
                            currentUser = user,
                            error = null
                        )
                    }
                    .onFailure { exception ->
                        uiState.value = uiState.value.copy(
                            isLoading = false,
                            error = exception.message ?: "Failed to update profile"
                        )
                    }
            }
        }
    }

    fun logout() {
        authRepository.logout()
        uiState.value = uiState.value.copy(
            isLogoutSuccessful = true,
            currentUser = null
        )
    }

    fun clearError() {
        uiState.value = uiState.value.copy(error = null)
    }

    fun clearLogoutSuccess() {
        uiState.value = uiState.value.copy(isLogoutSuccessful = false)
    }
} 