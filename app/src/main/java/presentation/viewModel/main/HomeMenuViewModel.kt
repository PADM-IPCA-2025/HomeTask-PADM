package pt.ipca.hometask.presentation.viewModel.main

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pt.ipca.hometask.data.repository.HomeRepositoryImpl
import pt.ipca.hometask.data.repository.TaskRepositoryImpl
import pt.ipca.hometask.domain.model.Home
import pt.ipca.hometask.domain.model.Task

data class HomeMenuUiState(
    val isLoading: Boolean = false,
    val homes: List<Home> = emptyList(),
    val tasks: List<Task> = emptyList(),
    val errorMessage: String? = null,
    val isUserLoggedIn: Boolean = false,
    val currentUserId: Int? = null,
    val currentUserName: String? = null,
    val userRoles: String? = null,
    val showErrorPopup: Boolean = false
)

class HomeMenuViewModel : ViewModel() {
    private val homeRepository = HomeRepositoryImpl()
    private val taskRepository = TaskRepositoryImpl()
    private val _uiState = mutableStateOf(HomeMenuUiState())
    val uiState = _uiState

    fun updateUserState(isLoggedIn: Boolean, userId: Int?, userName: String?, roles: String?) {
        if (_uiState.value.isUserLoggedIn != isLoggedIn || 
            _uiState.value.currentUserId != userId || 
            _uiState.value.currentUserName != userName ||
            _uiState.value.userRoles != roles) {
            _uiState.value = _uiState.value.copy(
                isUserLoggedIn = isLoggedIn,
                currentUserId = userId,
                currentUserName = userName,
                userRoles = roles
            )
        }
    }

    fun loadUserHomes(userId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val result = homeRepository.getHomeByUserId(userId)
                result.fold(
                    onSuccess = { homes ->
                        android.util.Log.d("HomeMenuViewModel", "Successfully loaded ${homes.size} homes")
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            homes = homes,
                            errorMessage = null
                        )
                    },
                    onFailure = { error ->
                        android.util.Log.e("HomeMenuViewModel", "Error loading homes", error)
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Erro ao carregar casas"
                        )
                    }
                )
            } catch (e: Exception) {
                android.util.Log.e("HomeMenuViewModel", "Exception loading homes", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Erro ao carregar casas"
                )
            }
        }
    }

    fun createHome(name: String, address: String, zipCode: String) {
        if (_uiState.value.userRoles?.contains("Gestor", ignoreCase = true) != true) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Apenas gestores podem criar casas"
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val home = Home(
                    name = name,
                    address = address,
                    zipCodeId = zipCode.toIntOrNull() ?: 0,
                    userId = _uiState.value.currentUserId ?: 0
                )

                val result = homeRepository.createHome(home)
                result.fold(
                    onSuccess = { createdHome ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            homes = _uiState.value.homes + createdHome,
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
        _uiState.value = _uiState.value.copy(errorMessage = null, showErrorPopup = false)
    }

    fun updateTasks(tasks: List<Task>) {
        _uiState.value = _uiState.value.copy(tasks = tasks)
    }

    fun deleteHome(homeId: Int) {
        if (_uiState.value.userRoles?.contains("Gestor", ignoreCase = true) != true) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Apenas gestores podem excluir casas",
                showErrorPopup = true
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val result = homeRepository.deleteHome(homeId)
                result.fold(
                    onSuccess = {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            homes = _uiState.value.homes.filter { it.id != homeId },
                            errorMessage = null,
                            showErrorPopup = false
                        )
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Erro ao excluir casa",
                            showErrorPopup = true
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Erro ao excluir casa",
                    showErrorPopup = true
                )
            }
        }
    }

    fun loadUserTasks(userId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val result = taskRepository.getTasksByUserId(userId)
                result.fold(
                    onSuccess = { tasks ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            tasks = tasks,
                            errorMessage = null
                        )
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Erro ao carregar tarefas"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Erro ao carregar tarefas"
                )
            }
        }
    }
}