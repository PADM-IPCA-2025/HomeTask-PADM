package pt.ipca.hometask.presentation.viewModel.main

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pt.ipca.hometask.data.repository.HomeRepositoryImpl
import pt.ipca.hometask.data.repository.TaskRepositoryImpl
import pt.ipca.hometask.domain.model.Home
import pt.ipca.hometask.domain.model.Task
import pt.ipca.hometask.domain.repository.HomeRepository
import pt.ipca.hometask.domain.repository.TaskRepository

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
    private val homeRepository: HomeRepository = HomeRepositoryImpl()
    private val taskRepository: TaskRepository = TaskRepositoryImpl()
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
                android.util.Log.d("HomeMenuViewModel", "Loading homes for user ID: $userId")
                val result = homeRepository.getHomeByUserId(userId)
                result.fold(
                    onSuccess = { homes ->
                        android.util.Log.d("HomeMenuViewModel", "Successfully loaded ${homes.size} homes")
                        homes.forEach { home ->
                            android.util.Log.d("HomeMenuViewModel", """
                                Home details:
                                - ID: ${home.id}
                                - Name: ${home.name}
                                - Address: ${home.address}
                                - ZipCode ID: ${home.zipCodeId}
                                - User ID: ${home.userId}
                            """.trimIndent())
                        }
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

    fun loadUserTasks(userId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                android.util.Log.d("HomeMenuViewModel", "Loading tasks for user ID: $userId")
                val result = taskRepository.getTasksByUser(userId)
                result.fold(
                    onSuccess = { tasks ->
                        android.util.Log.d("HomeMenuViewModel", "Successfully loaded ${tasks.size} tasks")
                        tasks.forEach { task ->
                            android.util.Log.d("HomeMenuViewModel", """
                                Task details:
                                - ID: ${task.id}
                                - Title: ${task.title}
                                - Description: ${task.description}
                                - Date: ${task.date}
                                - State: ${task.state}
                                - Home ID: ${task.homeId}
                                - User ID: ${task.userId}
                                - Category ID: ${task.taskCategoryId}
                            """.trimIndent())
                        }
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            tasks = tasks,
                            errorMessage = null
                        )
                    },
                    onFailure = { error ->
                        android.util.Log.e("HomeMenuViewModel", "Error loading tasks", error)
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Erro ao carregar tarefas"
                        )
                    }
                )
            } catch (e: Exception) {
                android.util.Log.e("HomeMenuViewModel", "Exception loading tasks", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Erro ao carregar tarefas"
                )
            }
        }
    }

    fun updateTaskState(taskId: Int, newState: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                android.util.Log.d("HomeMenuViewModel", "Updating task $taskId state to $newState")
                
                // Encontrar a task atual
                val currentTask = _uiState.value.tasks.find { it.id == taskId }
                if (currentTask == null) {
                    android.util.Log.e("HomeMenuViewModel", "Task $taskId not found")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Task not found"
                    )
                    return@launch
                }

                // Criar uma cópia da task com o novo estado
                val updatedTask = currentTask.copy(state = newState)
                
                // Atualizar a task no backend
                val result = taskRepository.updateTask(taskId, updatedTask)
                result.fold(
                    onSuccess = { task ->
                        android.util.Log.d("HomeMenuViewModel", "Successfully updated task state")
                        // Atualizar a lista de tasks
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            tasks = _uiState.value.tasks.map { 
                                if (it.id == taskId) task else it 
                            },
                            errorMessage = null
                        )
                        // Recarregar todas as tasks após atualizar
                        loadUserTasks(_uiState.value.currentUserId!!)
                    },
                    onFailure = { error ->
                        android.util.Log.e("HomeMenuViewModel", "Error updating task state", error)
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Erro ao atualizar estado da tarefa"
                        )
                    }
                )
            } catch (e: Exception) {
                android.util.Log.e("HomeMenuViewModel", "Exception updating task state", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Erro ao atualizar estado da tarefa"
                )
            }
        }
    }

    fun createHome(name: String, address: String, zipCode: String) {
        if (_uiState.value.userRoles?.contains("Manager", ignoreCase = true) != true) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Only managers can create houses"
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
                            errorMessage = error.message ?: "Error creating house"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Error creating house"
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
        if (_uiState.value.userRoles?.contains("Manager", ignoreCase = true) != true) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Only managers can delete houses",
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
                        // Recarregar a lista de casas após a exclusão
                        loadUserHomes(_uiState.value.currentUserId!!)
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Error deleting house",
                            showErrorPopup = true
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Error deleting house",
                    showErrorPopup = true
                )
            }
        }
    }
}