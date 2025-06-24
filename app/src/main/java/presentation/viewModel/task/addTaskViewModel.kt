package pt.ipca.hometask.presentation.viewModel.task

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
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
import pt.ipca.hometask.utils.ImageUtils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddTaskViewModel(application: Application) : AndroidViewModel(application) {
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
        photo: String?,
        homeId: Int,
        userId: Int
    ) {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            success.value = false
            val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            
            android.util.Log.d("AddTaskViewModel", "Iniciando criação de task: title=$title, desc=$description, selectedResidentId=$selectedResidentId, status=$status, date=$currentDate, photo=$photo, homeId=$homeId, userId=$userId, categoryId=1")
            
            try {
                // Copiar imagem para armazenamento interno se for um content URI
                val processedPhoto = if (ImageUtils.isContentUri(photo)) {
                    android.util.Log.d("AddTaskViewModel", "📸 Detected content URI: $photo")
                    android.util.Log.d("AddTaskViewModel", "📸 Copiando imagem para armazenamento interno...")
                    val permanentUri = ImageUtils.copyImageToInternalStorage(getApplication(), photo)
                    if (permanentUri != null) {
                        android.util.Log.d("AddTaskViewModel", "✅ Imagem copiada com sucesso: $permanentUri")
                        permanentUri
                    } else {
                        android.util.Log.w("AddTaskViewModel", "⚠️ Falha ao copiar imagem, usando URI original")
                        photo
                    }
                } else if (ImageUtils.isFileUri(photo)) {
                    android.util.Log.d("AddTaskViewModel", "📁 Detected file URI: $photo")
                    photo
                } else {
                    android.util.Log.d("AddTaskViewModel", "🔗 Detected other URI type: $photo")
                    photo
                }
                
                android.util.Log.d("AddTaskViewModel", "📋 Photo final: $processedPhoto")
                
                val task = Task(
                    id = null,
                    title = title,
                    description = description,
                    date = currentDate,
                    state = "Pendente",
                    photo = processedPhoto ?: "photo_url",
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
                            android.util.Log.d("AddTaskViewModel", "Criando TaskParticipantDto: taskId=${createdTask.id}, userId=$selectedResidentId")
                            try {
                                val taskParticipant = TaskParticipantDto(
                                    taskId = createdTask.id,
                                    userId = selectedResidentId
                                )
                                android.util.Log.d("AddTaskViewModel", "Enviando TaskParticipantDto: $taskParticipant")
                                val participantResponse = api.createTaskParticipant(taskParticipant)
                                if (participantResponse.isSuccessful) {
                                    android.util.Log.d("AddTaskViewModel", "✅ Task participant criado com sucesso!")
                                } else {
                                    val errorBody = participantResponse.errorBody()?.string()
                                    android.util.Log.e("AddTaskViewModel", "❌ Erro ao criar task participant: Status=${participantResponse.code()}, Message=${participantResponse.message()}, ErrorBody=$errorBody")
                                }
                            } catch (e: Exception) {
                                android.util.Log.e("AddTaskViewModel", "💥 Exceção ao criar task participant: ${e.message}", e)
                            }
                        } else {
                            android.util.Log.d("AddTaskViewModel", "ℹ️ Nenhum residente selecionado ou task ID nulo, pulando criação de TaskParticipantDto")
                        }
                        
                        success.value = true
                        isLoading.value = false
                    },
                    onFailure = { error ->
                        android.util.Log.e("AddTaskViewModel", "❌ Erro ao criar task: ${error.message}")
                        errorMessage.value = error.message
                        isLoading.value = false
                    }
                )
            } catch (e: Exception) {
                android.util.Log.e("AddTaskViewModel", "💥 Exceção ao criar task: ${e.message}", e)
                errorMessage.value = e.message
                isLoading.value = false
            }
        }
    }
}

