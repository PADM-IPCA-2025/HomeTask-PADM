package pt.ipca.hometask.data.repository

import android.util.Log
import pt.ipca.hometask.data.remote.api.HomeTaskApi
import pt.ipca.hometask.data.remote.model.TaskDto
import pt.ipca.hometask.domain.model.Task
import pt.ipca.hometask.domain.repository.TaskRepository
import pt.ipca.hometask.network.RetrofitClient
import java.text.SimpleDateFormat
import java.util.*

class TaskRepositoryImpl : TaskRepository {
    private val api: HomeTaskApi = RetrofitClient.homeTaskApi
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val apiDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    private fun TaskDto.toDomain(): Task {
        return Task(
            id = id,
            title = title,
            description = description,
            date = try {
                // Converter da data da API (yyyy-MM-dd) para o formato do app (dd/MM/yyyy)
                val date = apiDateFormat.parse(data)
                dateFormat.format(date)
            } catch (e: Exception) {
                data // Se falhar, retorna a data original
            },
            state = state,
            photo = photo,
            homeId = homeId,
            userId = userId,
            taskCategoryId = taskCategoryId
        )
    }

    private fun Task.toDto(): TaskDto {
        return TaskDto(
            id = null,  // Sempre enviar null para atualização
            title = title,
            description = description,
            data = try {
                // Converter da data do app (dd/MM/yyyy) para o formato da API (yyyy-MM-dd)
                val date = dateFormat.parse(date)
                apiDateFormat.format(date)
            } catch (e: Exception) {
                date // Se falhar, retorna a data original
            },
            state = state,
            photo = photo,
            homeId = homeId,
            userId = userId,
            taskCategoryId = taskCategoryId
        )
    }

    override suspend fun createTask(task: Task): Result<Task> {
        return try {
            val response = api.createTask(task.toDto())
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toDomain())
            } else {
                Result.failure(Exception("Create task failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAllTasks(): Result<List<Task>> {
        return try {
            val response = api.getAllTasks()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.map { it.toDomain() })
            } else {
                Result.failure(Exception("Get tasks failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getTaskById(id: Int): Result<Task> {
        return try {
            val response = api.getTaskById(id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toDomain())
            } else {
                Result.failure(Exception("Get task failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getTasksByHome(homeId: Int): Result<List<Task>> {
        return try {
            val response = api.getTasksByHome(homeId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.map { it.toDomain() })
            } else {
                Result.failure(Exception("Get tasks by home failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getTasksByUser(userId: Int): Result<List<Task>> {
        return try {
            Log.d("TaskRepositoryImpl", "Getting tasks for user $userId")
            val response = api.getTasksByUser(userId)
            Log.d("TaskRepositoryImpl", "API response: ${response.isSuccessful}, body: ${response.body()}")

            if (response.isSuccessful) {
                val apiResponse = response.body()
                if (apiResponse?.success == true) {
                    val tasks = apiResponse.data
                    Log.d("TaskRepositoryImpl", "Found ${tasks.size} tasks")

                    // Converter todas as tasks para o modelo de domínio
                    val domainTasks = tasks.map { taskDto ->
                        val domainTask = taskDto.toDomain()
                        Log.d("TaskRepositoryImpl", "Returning task: ${domainTask.title}")
                        domainTask
                    }

                    Result.success(domainTasks)
                } else {
                    Log.e("TaskRepositoryImpl", "API returned error: ${apiResponse?.message}")
                    Result.failure(Exception(apiResponse?.message ?: "Unknown error"))
                }
            } else {
                Log.e("TaskRepositoryImpl", "API call failed: ${response.code()}")
                Result.failure(Exception("Failed to get tasks: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e("TaskRepositoryImpl", "Error getting tasks", e)
            Result.failure(e)
        }
    }

    override suspend fun updateTask(id: Int, task: Task): Result<Task> {
        return try {
            Log.d("TaskRepositoryImpl", "Updating task $id with state ${task.state}")
            
            // Criar uma cópia da task com a data formatada
            val taskWithFormattedDate = task.copy(
                date = dateFormat.format(dateFormat.parse(task.date) ?: Date())
            )
            
            val response = api.updateTask(id, taskWithFormattedDate.toDto())
            Log.d("TaskRepositoryImpl", "Update response: ${response.isSuccessful}, body: ${response.body()}")

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toDomain())
            } else {
                Log.e("TaskRepositoryImpl", "Update failed: ${response.message()}")
                Result.failure(Exception("Update task failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e("TaskRepositoryImpl", "Error updating task", e)
            Result.failure(e)
        }
    }

    override suspend fun deleteTask(id: Int): Result<Unit> {
        return try {
            val response = api.deleteTask(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Delete task failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTasksByUserId(userId: Int): Result<List<Task>> {
        return try {
            val response = api.getTaskParticipantsByUserId(userId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.map { it.toDomain() })
            } else {
                Result.failure(Exception("Erro ao carregar tarefas"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
