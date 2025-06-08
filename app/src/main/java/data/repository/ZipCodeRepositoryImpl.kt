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
            Log.d("ZipCodeRepository", "API Response code: ${response.code()}")
            Log.d("ZipCodeRepository", "API Response headers: ${response.headers()}")
            Log.d("ZipCodeRepository", "API Response raw: ${response.raw()}")

            if (response.isSuccessful) {
                val responseBody = response.body()
                Log.d("ZipCodeRepository", "Response body: $responseBody")
                Log.d("ZipCodeRepository", "Response success: ${responseBody?.success}")
                Log.d("ZipCodeRepository", "Response message: ${responseBody?.message}")
                Log.d("ZipCodeRepository", "Response data: ${responseBody?.data}")

                if (responseBody?.success == true) {
                    val zipCodes = responseBody.data?.mapNotNull { it.toDomain() } ?: emptyList()
                    Log.d("ZipCodeRepository", "Converted ${zipCodes.size} zip codes")
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