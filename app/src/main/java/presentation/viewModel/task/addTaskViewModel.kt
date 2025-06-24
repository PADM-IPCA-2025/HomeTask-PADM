package pt.ipca.hometask.presentation.viewModel.task

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pt.ipca.hometask.data.remote.api.HomeTaskApi
import pt.ipca.hometask.data.remote.model.TaskParticipantDto
import pt.ipca.hometask.data.repository.TaskRepositoryImpl
import pt.ipca.hometask.data.repository.ResidentRepositoryImpl
import pt.ipca.hometask.domain.model.Task
import pt.ipca.hometask.domain.repository.TaskRepository
import pt.ipca.hometask.domain.repository.ResidentRepository
import pt.ipca.hometask.network.RetrofitClient
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddTaskViewModel : ViewModel() {
    private val taskRepository: TaskRepository = TaskRepositoryImpl()
    private val api: HomeTaskApi = RetrofitClient.homeTaskApi
    private val residentRepository: ResidentRepository = ResidentRepositoryImpl()
    
    val isLoading = mutableStateOf(false)
    val errorMessage = mutableStateOf<String?>(null)
    val success = mutableStateOf(false)
    val residents = mutableStateOf<List<pt.ipca.hometask.domain.model.User>>(emptyList())

    fun loadResidentsByHomeId(homeId: Int) {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            try {
                val result = residentRepository.getResidentsByHomeId(homeId)
                result.fold(
                    onSuccess = { homeResidents ->
                        residents.value = homeResidents
                        android.util.Log.d("AddTaskViewModel", "Residentes carregados: ${homeResidents.size}")
                    },
                    onFailure = { error ->
                        android.util.Log.e("AddTaskViewModel", "Erro ao carregar residentes: ${error.message}")
                        errorMessage.value = error.message
                    }
                )
            } catch (e: Exception) {
                android.util.Log.e("AddTaskViewModel", "Exceção ao carregar residentes: ${e.message}")
                errorMessage.value = e.message
            }
            isLoading.value = false
        }
    }

    fun createTask(
        title: String,
        description: String,
        selectedResidentId: Int?,
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
            android.util.Log.d("AddTaskViewModel", "Iniciando criação de task: title=$title, desc=$description, selectedResidentId=$selectedResidentId, status=$status, date=$currentDate, homeId=$homeId, userId=$userId, categoryId=1, photo=photo_url")
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
                    onSuccess = { createdTask ->
                        android.util.Log.d("AddTaskViewModel", "Task criada com sucesso! ID: ${createdTask.id}")
                        
                        // Se foi selecionado um residente, criar a ligação de task participant
                        if (selectedResidentId != null && createdTask.id != null) {
                            try {
                                val taskParticipant = TaskParticipantDto(
                                    taskId = createdTask.id,
                                    userId = selectedResidentId
                                )
                                val participantResponse = api.createTaskParticipant(taskParticipant)
                                if (participantResponse.isSuccessful) {
                                    android.util.Log.d("AddTaskViewModel", "Task participant criado com sucesso!")
                                } else {
                                    android.util.Log.e("AddTaskViewModel", "Erro ao criar task participant: ${participantResponse.message()}")
                                }
                            } catch (e: Exception) {
                                android.util.Log.e("AddTaskViewModel", "Exceção ao criar task participant: ${e.message}")
                            }
                        }
                        
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

