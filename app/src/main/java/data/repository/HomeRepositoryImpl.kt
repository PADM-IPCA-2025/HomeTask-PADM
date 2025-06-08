package pt.ipca.hometask.data.repository

import pt.ipca.hometask.data.remote.api.HomeTaskApi
import pt.ipca.hometask.data.remote.model.HomeDto
import pt.ipca.hometask.domain.model.Home
import pt.ipca.hometask.domain.repository.HomeRepository
import pt.ipca.hometask.network.RetrofitClient

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

    override suspend fun getHomeByUserId(userId: Int): Result<List<Home>> {
        return try {
            android.util.Log.d("HomeRepositoryImpl", "Getting homes for user $userId")
            val response = api.getHomeByUserId(userId)
            android.util.Log.d("HomeRepositoryImpl", "API response: ${response.isSuccessful}, body: ${response.body()}")

            if (response.isSuccessful) {
                val apiResponse = response.body()
                if (apiResponse?.success == true) {
                    val homes = apiResponse.data
                    android.util.Log.d("HomeRepositoryImpl", "Found ${homes.size} homes")

                    // Filtrar casas que pertencem ao usuário
                    val userHomes = homes.filter { it.userId == userId }
                    android.util.Log.d("HomeRepositoryImpl", "Found ${userHomes.size} homes for user $userId")

                    // Converter todas as casas para o modelo de domínio
                    val domainHomes = userHomes.map { homeDto ->
                        val domainHome = homeDto.toDomain()
                        android.util.Log.d("HomeRepositoryImpl", "Returning home: ${domainHome.name}")
                        domainHome
                    }

                    Result.success(domainHomes)
                } else {
                    android.util.Log.e("HomeRepositoryImpl", "API returned error: ${apiResponse?.message}")
                    Result.failure(Exception(apiResponse?.message ?: "Unknown error"))
                }
            } else {
                android.util.Log.e("HomeRepositoryImpl", "API call failed: ${response.code()}")
                Result.failure(Exception("Failed to get homes: ${response.code()}"))
            }
        } catch (e: Exception) {
            android.util.Log.e("HomeRepositoryImpl", "Error getting homes", e)
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
