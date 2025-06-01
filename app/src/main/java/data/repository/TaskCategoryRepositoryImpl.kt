package pt.ipca.hometask.data.repository

import pt.ipca.hometask.data.remote.api.HomeTaskApi
import pt.ipca.hometask.data.remote.model.TaskCategoryDto
import pt.ipca.hometask.domain.model.TaskCategory
import pt.ipca.hometask.domain.repository.TaskCategoryRepository
import pt.ipca.hometask.data.remote.network.RetrofitClient

class TaskCategoryRepositoryImpl : TaskCategoryRepository {
    private val api: HomeTaskApi = RetrofitClient.homeTaskApi

    private fun TaskCategoryDto.toDomain(): TaskCategory {
        return TaskCategory(
            id = id,
            description = description
        )
    }

    private fun TaskCategory.toDto(): TaskCategoryDto {
        return TaskCategoryDto(
            id = id,
            description = description
        )
    }

    override suspend fun createTaskCategory(category: TaskCategory): Result<TaskCategory> {
        return try {
            val response = api.createTaskCategory(category.toDto())
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toDomain())
            } else {
                Result.failure(Exception("Create task category failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAllTaskCategories(): Result<List<TaskCategory>> {
        return try {
            val response = api.getAllTaskCategories()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.map { it.toDomain() })
            } else {
                Result.failure(Exception("Get task categories failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getTaskCategoryById(id: Int): Result<TaskCategory> {
        return try {
            val response = api.getTaskCategoryById(id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toDomain())
            } else {
                Result.failure(Exception("Get task category failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateTaskCategory(id: Int, category: TaskCategory): Result<TaskCategory> {
        return try {
            val response = api.updateTaskCategory(id, category.toDto())
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toDomain())
            } else {
                Result.failure(Exception("Update task category failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteTaskCategory(id: Int): Result<Unit> {
        return try {
            val response = api.deleteTaskCategory(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Delete task category failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}