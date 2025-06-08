package pt.ipca.hometask.domain.repository

import pt.ipca.hometask.domain.model.Home

interface HomeRepository {
    suspend fun createHome(home: Home): Result<Home>
    suspend fun getAllHomes(): Result<List<Home>>
    suspend fun getHomeById(id: Int): Result<Home>
    suspend fun getHomeByUserId(id: Int): Result<List<Home>>
    suspend fun updateHome(id: Int, home: Home): Result<Home>
    suspend fun deleteHome(id: Int): Result<Unit>
}