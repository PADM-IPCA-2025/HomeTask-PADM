package pt.ipca.hometask.data.repository

import pt.ipca.hometask.data.remote.api.HomeTaskApi
import pt.ipca.hometask.data.remote.model.TaskDto
import pt.ipca.hometask.domain.model.Task
import pt.ipca.hometask.domain.repository.TaskRepository
import pt.ipca.hometask.data.remote.network.RetrofitClient

class TaskRepositoryImpl : TaskRepository {
    private val api: HomeTaskApi = RetrofitClient.homeTaskApi

    private fun TaskDto.toDomain(): Task {
        return Task(
            id = id,
            title = title,
            description = description,
            date = data, // API usa "data", domain usa "date"
            state = state,
            photo = photo,
            homeId = homeId,
            userId = userId,
            taskCategoryId = taskCategoryId
        )
    }

    private fun Task.toDto(): TaskDto {
        return TaskDto(
            id = id,
            title = title,
            description = description,
            data = date, // Domain usa "date", API espera "data"
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
            val response = api.getTasksByUser(userId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.map { it.toDomain() })
            } else {
                Result.failure(Exception("Get tasks by user failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateTask(id: Int, task: Task): Result<Task> {
        return try {
            val response = api.updateTask(id, task.toDto())
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toDomain())
            } else {
                Result.failure(Exception("Update task failed: ${response.message()}"))
            }
        } catch (e: Exception) {
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
}
