package pt.ipca.hometask.domain.repository

import pt.ipca.hometask.domain.model.Task

interface TaskRepository {
    suspend fun createTask(task: Task): Result<Task>
    suspend fun getAllTasks(): Result<List<Task>>
    suspend fun getTaskById(id: Int): Result<Task>
    suspend fun getTasksByHome(homeId: Int): Result<List<Task>>
    suspend fun getTasksByUser(userId: Int): Result<List<Task>>
    suspend fun updateTask(id: Int, task: Task): Result<Task>
    suspend fun deleteTask(id: Int): Result<Unit>
}