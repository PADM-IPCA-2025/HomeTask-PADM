package pt.ipca.hometask.presentation.viewModel.home

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pt.ipca.hometask.data.repository.HomeRepositoryImpl
import pt.ipca.hometask.data.repository.ZipCodeRepositoryImpl
import pt.ipca.hometask.domain.model.Home
import pt.ipca.hometask.domain.model.ZipCode

data class AddHouseUiState(
    val isLoading: Boolean = false,
    val zipCodes: List<ZipCode> = emptyList(),
    val errorMessage: String? = null
)

class AddHouseViewModel : ViewModel() {
    private val homeRepository = HomeRepositoryImpl()
    private val zipCodeRepository = ZipCodeRepositoryImpl()
    private val _uiState = MutableStateFlow(AddHouseUiState())
    val uiState: StateFlow<AddHouseUiState> = _uiState.asStateFlow()

    init {
        Log.d("AddHouseViewModel", "ViewModel initialized")
        loadZipCodes()
    }

    fun loadZipCodes() {
        viewModelScope.launch {
            try {
                Log.d("AddHouseViewModel", "Loading zip codes...")
                _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
                
                zipCodeRepository.getAllZipCodes()
                    .onSuccess { zipCodes ->
                        Log.d("AddHouseViewModel", "Successfully loaded ${zipCodes.size} zip codes")
                        zipCodes.forEach { zip ->
                            Log.d("AddHouseViewModel", "Zip code: id=${zip.id}, postalCode=${zip.postalCode}, city=${zip.city}")
                        }
                        
                        // Atualizar o estado com os novos zip codes
                        _uiState.value = AddHouseUiState(
                            zipCodes = zipCodes,
                            isLoading = false
                        )
                        
                        Log.d("AddHouseViewModel", "State updated with ${_uiState.value.zipCodes.size} zip codes")
                        _uiState.value.zipCodes.forEach { zip ->
                            Log.d("AddHouseViewModel", "Current state zip code: id=${zip.id}, postalCode=${zip.postalCode}, city=${zip.city}")
                        }
                    }
                    .onFailure { error ->
                        Log.e("AddHouseViewModel", "Failed to load zip codes", error)
                        _uiState.value = _uiState.value.copy(
                            errorMessage = error.message ?: "Failed to load zip codes",
                            isLoading = false
                        )
                    }
            } catch (e: Exception) {
                Log.e("AddHouseViewModel", "Error in loadZipCodes", e)
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message ?: "An error occurred",
                    isLoading = false
                )
            }
        }
    }

    fun createHome(name: String, address: String, zipCodeId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val home = Home(
                    name = name,
                    address = address,
                    zipCodeId = zipCodeId.toIntOrNull() ?: 0,
                    userId = 0 // Substitua pelo ID do usuário logado
                )
                val result = homeRepository.createHome(home)
                result.fold(
                    onSuccess = {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = null
                        )
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Erro ao criar casa"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Erro ao criar casa"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
} 