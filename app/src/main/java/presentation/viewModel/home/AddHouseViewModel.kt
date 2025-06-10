package pt.ipca.hometask.presentation.viewModel.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pt.ipca.hometask.data.repository.ZipCodeRepositoryImpl
import pt.ipca.hometask.data.repository.HomeRepositoryImpl
import pt.ipca.hometask.data.repository.AuthRepository
import pt.ipca.hometask.domain.model.ZipCode
import pt.ipca.hometask.domain.model.Home

data class AddHouseUiState(
    val isLoading: Boolean = false,
    val zipCodes: List<ZipCode> = emptyList(),
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)

class AddHouseViewModel(application: Application) : AndroidViewModel(application) {
    private val zipCodeRepository = ZipCodeRepositoryImpl()
    private val homeRepository = HomeRepositoryImpl()
    private val authRepository = AuthRepository(application.applicationContext)
    private val _uiState = MutableStateFlow(AddHouseUiState())
    val uiState: StateFlow<AddHouseUiState> = _uiState.asStateFlow()

    init {
        loadZipCodes()
    }

    fun loadZipCodes() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                zipCodeRepository.getAllZipCodes()
                    .onSuccess { zipCodes ->
                        _uiState.value = AddHouseUiState(
                            zipCodes = zipCodes,
                            isLoading = false
                        )
                    }
                    .onFailure { error ->
                        _uiState.value = _uiState.value.copy(
                            errorMessage = error.message ?: "Failed to load zip codes",
                            isLoading = false
                        )
                    }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message ?: "An error occurred",
                    isLoading = false
                )
            }
        }
    }

    fun createHome(name: String, address: String, selectedZipCodeText: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                // Validar campos
                if (name.isBlank()) {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "House name cannot be empty",
                        isLoading = false,
                        isSuccess = true // Sempre marca como sucesso para voltar ao menu
                    )
                    return@launch
                }

                if (address.isBlank()) {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "Address cannot be empty",
                        isLoading = false,
                        isSuccess = true // Sempre marca como sucesso para voltar ao menu
                    )
                    return@launch
                }

                Log.d("AddHouseViewModel", "Creating home with:")
                Log.d("AddHouseViewModel", "Name: $name")
                Log.d("AddHouseViewModel", "Address: $address")

                // Obter o ID do usuário logado
                val currentUser = authRepository.getCurrentUser()
                if (currentUser == null) {
                    Log.e("AddHouseViewModel", "No user logged in")
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "No user logged in",
                        isLoading = false,
                        isSuccess = true // Sempre marca como sucesso para voltar ao menu
                    )
                    return@launch
                }

                Log.d("AddHouseViewModel", "Current user ID: ${currentUser.id}")

                val home = Home(
                    name = name.trim(),
                    address = address.trim(),
                    zipCodeId = 1, // Sempre usar o ID 1
                    userId = currentUser.id ?: 0
                )

                Log.d("AddHouseViewModel", "Sending home object to backend:")
                Log.d("AddHouseViewModel", "Home: $home")

                homeRepository.createHome(home)
                    .onSuccess {
                        Log.d("AddHouseViewModel", "Home created successfully")
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isSuccess = true
                        )
                    }
                    .onFailure { error ->
                        Log.e("AddHouseViewModel", "Failed to create home", error)
                        _uiState.value = _uiState.value.copy(
                            errorMessage = error.message ?: "Failed to create home",
                            isLoading = false,
                            isSuccess = true // Sempre marca como sucesso para voltar ao menu
                        )
                    }
            } catch (e: Exception) {
                Log.e("AddHouseViewModel", "Error creating home", e)
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message ?: "An error occurred",
                    isLoading = false,
                    isSuccess = true // Sempre marca como sucesso para voltar ao menu
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
} 