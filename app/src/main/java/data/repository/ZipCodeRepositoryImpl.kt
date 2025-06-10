package pt.ipca.hometask.data.repository

import android.util.Log
import pt.ipca.hometask.data.remote.model.ApiResponse
import pt.ipca.hometask.data.remote.model.ZipCodeDto
import pt.ipca.hometask.domain.model.ZipCode
import pt.ipca.hometask.network.RetrofitClient

class ZipCodeRepositoryImpl {
    private val api = RetrofitClient.homeTaskApi

    suspend fun getAllZipCodes(): Result<List<ZipCode>> {
        return try {
            Log.d("ZipCodeRepository", "Fetching zip codes from API...")
            val response = api.getAllZipCodes()
            
            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody?.success == true) {
                    val zipCodes = responseBody.data?.mapNotNull { dto ->
                        dto.id?.let { id ->
                            ZipCode(
                                id = id,
                                postalCode = dto.postalCode,
                                city = dto.city
                            )
                        }
                    } ?: emptyList()
                    
                    Log.d("ZipCodeRepository", "Successfully loaded ${zipCodes.size} zip codes")
                    zipCodes.forEach { zip ->
                        Log.d("ZipCodeRepository", "Zip code: id=${zip.id}, postalCode=${zip.postalCode}, city=${zip.city}")
                    }
                    
                    Result.success(zipCodes)
                } else {
                    Log.e("ZipCodeRepository", "API returned success=false: ${responseBody?.message}")
                    Result.failure(Exception(responseBody?.message ?: "Failed to get zip codes"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("ZipCodeRepository", "Failed to get zip codes: ${response.code()} - $errorBody")
                Result.failure(Exception("Failed to get zip codes: ${response.code()} - $errorBody"))
            }
        } catch (e: Exception) {
            Log.e("ZipCodeRepository", "Error fetching zip codes", e)
            Result.failure(e)
        }
    }

    private fun ZipCodeDto.toDomain(): ZipCode? {
        val id = id ?: return null
        return ZipCode(
            id = id,
            postalCode = postalCode,
            city = city
        )
    }
} 