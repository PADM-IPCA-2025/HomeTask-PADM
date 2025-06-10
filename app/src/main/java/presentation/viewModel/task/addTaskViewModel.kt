package pt.ipca.hometask.presentation.viewModel.task

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pt.ipca.hometask.data.repository.TaskRepositoryImpl
import pt.ipca.hometask.domain.model.Task
import pt.ipca.hometask.domain.repository.TaskRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddTaskViewModel : ViewModel() {
    private val taskRepository: TaskRepository = TaskRepositoryImpl()
    val isLoading = mutableStateOf(false)
    val errorMessage = mutableStateOf<String?>(null)
    val success = mutableStateOf(false)

    fun createTask(
        title: String,
        description: String,
        group: String,
        status: String,
        date: String,
        homeId: Int,
        userId: Int
    ) {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            success.value = false
            val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            android.util.Log.d("AddTaskViewModel", "Iniciando criação de task: title=$title, desc=$description, group=$group, status=$status, date=$currentDate, homeId=$homeId, userId=$userId, categoryId=1, photo=photo_url")
            try {
                val task = Task(
                    id = null,
                    title = title,
                    description = description,
                    date = currentDate,
                    state = "Pendente",
                    photo = "photo_url",
                    homeId = homeId,
                    userId = userId,
                    taskCategoryId = 1
                )
                val result = taskRepository.createTask(task)
                result.fold(
                    onSuccess = {
                        android.util.Log.d("AddTaskViewModel", "Task criada com sucesso!")
                        success.value = true
                        isLoading.value = false
                    },
                    onFailure = { error ->
                        android.util.Log.e("AddTaskViewModel", "Erro ao criar task: ${error.message}")
                        errorMessage.value = error.message
                        isLoading.value = false
                    }
                )
            } catch (e: Exception) {
                android.util.Log.e("AddTaskViewModel", "Exceção ao criar task: ${e.message}")
                errorMessage.value = e.message
                isLoading.value = false
            }
        }
    }
}

