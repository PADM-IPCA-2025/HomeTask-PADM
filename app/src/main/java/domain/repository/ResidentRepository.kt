package pt.ipca.hometask.domain.repository

import pt.ipca.hometask.data.remote.model.ResidentDto
import pt.ipca.hometask.domain.model.User

interface ResidentRepository {
    suspend fun getResidentsByHomeId(homeId: Int): Result<List<User>>
    suspend fun createResident(resident: ResidentDto): Result<Unit>
    suspend fun deleteResident(userId: Int, homeId: Int): Result<Unit>
} 