package pt.ipca.hometask.data.repository

import pt.ipca.hometask.data.remote.api.HomeTaskApi
import pt.ipca.hometask.data.remote.model.HomeDto
import pt.ipca.hometask.domain.model.Home
import pt.ipca.hometask.domain.repository.HomeRepository
import pt.ipca.hometask.data.remote.network.RetrofitClient

class HomeRepositoryImpl : HomeRepository {
    private val api: HomeTaskApi = RetrofitClient.homeTaskApi

    private fun HomeDto.toDomain(): Home {
        return Home(
            id = id,
            name = name,
            address = address,
            zipCodeId = zipCodeId,
            userId = userId
        )
    }

    private fun Home.toDto(): HomeDto {
        return HomeDto(
            id = id,
            name = name,
            address = address,
            zipCodeId = zipCodeId,
            userId = userId
        )
    }

    override suspend fun createHome(home: Home): Result<Home> {
        return try {
            val response = api.createHome(home.toDto())
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toDomain())
            } else {
                Result.failure(Exception("Create home failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAllHomes(): Result<List<Home>> {
        return try {
            val response = api.getAllHomes()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.map { it.toDomain() })
            } else {
                Result.failure(Exception("Get homes failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getHomeById(id: Int): Result<Home> {
        return try {
            val response = api.getHomeById(id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toDomain())
            } else {
                Result.failure(Exception("Get home failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateHome(id: Int, home: Home): Result<Home> {
        return try {
            val response = api.updateHome(id, home.toDto())
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toDomain())
            } else {
                Result.failure(Exception("Update home failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteHome(id: Int): Result<Unit> {
        return try {
            val response = api.deleteHome(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Delete home failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
