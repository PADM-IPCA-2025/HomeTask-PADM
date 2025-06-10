package pt.ipca.hometask.presentation.viewModel.task

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pt.ipca.hometask.data.repository.TaskRepositoryImpl
import pt.ipca.hometask.domain.model.Task
import pt.ipca.hometask.domain.repository.TaskRepository

// Estado da tela de tasks do menu
data class TaskMenuUiState(
    val isLoading: Boolean = false,
    val tasks: List<Task> = emptyList(),
    val errorMessage: String? = null
)

class TaskMenuViewModel : ViewModel() {
    private val taskRepository: TaskRepository = TaskRepositoryImpl()
    private val _uiState = mutableStateOf(TaskMenuUiState())
    val uiState = _uiState

    fun loadTasksByHome(homeId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val result = taskRepository.getTasksByHome(homeId)
                result.fold(
                    onSuccess = { tasks ->
                        tasks.forEach { task ->
                            android.util.Log.d("TaskMenuViewModel", "Task recebida: id=${task.id}, title=${task.title}, state=${task.state}, date=${task.date}")
                        }
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            tasks = tasks,
                            errorMessage = null
                        )
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Erro ao carregar tarefas da casa"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Erro ao carregar tarefas da casa"
                )
            }
        }
    }

    fun updateTaskState(taskId: Int, newState: String, homeId: Int) {
        // Atualiza localmente para feedback imediato
        val updatedTasks = uiState.value.tasks.map {
            if (it.id == taskId) it.copy(state = newState) else it
        }
        _uiState.value = _uiState.value.copy(tasks = updatedTasks)

        // Atualiza no backend e recarrega
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val currentTask = uiState.value.tasks.find { it.id == taskId }
                if (currentTask != null) {
                    val updatedTask = currentTask.copy(state = newState)
                    val result = taskRepository.updateTask(taskId, updatedTask)
                    result.fold(
                        onSuccess = {
                            loadTasksByHome(homeId)
                        },
                        onFailure = { error ->
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                errorMessage = error.message
                            )
                        }
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Task não encontrada"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message
                )
            }
        }
    }
}

