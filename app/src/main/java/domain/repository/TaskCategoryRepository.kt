package pt.ipca.hometask.domain.repository

import pt.ipca.hometask.domain.model.TaskCategory

interface TaskCategoryRepository {
    suspend fun createTaskCategory(category: TaskCategory): Result<TaskCategory>
    suspend fun getAllTaskCategories(): Result<List<TaskCategory>>
    suspend fun getTaskCategoryById(id: Int): Result<TaskCategory>
    suspend fun updateTaskCategory(id: Int, category: TaskCategory): Result<TaskCategory>
    suspend fun deleteTaskCategory(id: Int): Result<Unit>
}
