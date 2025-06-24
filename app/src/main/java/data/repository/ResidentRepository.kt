package pt.ipca.hometask.data.repository

import pt.ipca.hometask.data.remote.api.HomeTaskApi
import pt.ipca.hometask.data.remote.model.ResidentDto
import pt.ipca.hometask.domain.model.User
import pt.ipca.hometask.domain.repository.ResidentRepository
import pt.ipca.hometask.domain.repository.UserRepository
import pt.ipca.hometask.network.RetrofitClient

class ResidentRepositoryImpl : ResidentRepository {
    private val api: HomeTaskApi = RetrofitClient.homeTaskApi
    private val userRepository: UserRepository = UserRepositoryImpl()

    override suspend fun getResidentsByHomeId(homeId: Int): Result<List<User>> {
        return try {
            val response = api.getResidentsByHomeId(homeId)
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success) {
                    val residentDtos = apiResponse.data

                    val allUsersResult = userRepository.getAllUsers()
                    allUsersResult.fold(
                        onSuccess = { allUsers ->
                            val homeResidents = mutableListOf<User>()
                            for (user in allUsers) {
                                for (resident in residentDtos) {
                                    if (resident.userId == user.id) {
                                        homeResidents.add(user)
                                        break
                                    }
                                }
                            }
                            Result.success(homeResidents.toList())
                        },
                        onFailure = { error ->
                            Result.failure(error)
                        }
                    )
                } else {
                    Result.failure(Exception(apiResponse.message))
                }
            } else {
                Result.failure(Exception("Get residents failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createResident(resident: ResidentDto): Result<Unit> {
        return try {
            val response = api.createResident(resident)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Create resident failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteResident(userId: Int, homeId: Int): Result<Unit> {
        return try {
            val response = api.deleteResident(userId, homeId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Delete resident failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
