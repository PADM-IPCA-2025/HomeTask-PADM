package pt.ipca.hometask.presentation.viewModel.shopping

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import com.google.maps.android.compose.Circle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import android.content.Context
import androidx.core.content.ContextCompat.getString
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.res.stringResource
import pt.ipca.hometask.R
import androidx.compose.ui.platform.LocalContext

// A simple data class for a supermarket
data class Supermarket(
    val name: String,
    val address: String,
    val location: LatLng
)

// The UI state for the screen
data class ClosestSupermarketUiState(
    val isLoading: Boolean = false,
    val userLocation: LatLng? = null,
    val supermarkets: List<Supermarket> = emptyList(),
    val errorMessage: String? = null
)

class ClosestSupermarketViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(ClosestSupermarketUiState())
    val uiState: StateFlow<ClosestSupermarketUiState> = _uiState.asStateFlow()

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(application)

    suspend fun fetchNearbySupermarketsFromGoogle(
        lat: Double,
        lon: Double,
        radiusKm: Float,
        apiKey: String
    ): List<Supermarket> = withContext(Dispatchers.IO) {
        val url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json" +
                "?location=$lat,$lon" +
                "&radius=${(radiusKm * 1000).toInt()}" +
                "&type=supermarket" +
                "&key=$apiKey"

        val response = URL(url).readText()
        val json = JSONObject(response)
        val results = json.getJSONArray("results")
        val supermarkets = mutableListOf<Supermarket>()
        for (i in 0 until results.length()) {
            val obj = results.getJSONObject(i)
            val name = obj.optString("name")
            val address = obj.optString("vicinity")
            val location = obj.getJSONObject("geometry").getJSONObject("location")
            val latRes = location.getDouble("lat")
            val lngRes = location.getDouble("lng")
            supermarkets.add(
                Supermarket(
                    name = name,
                    address = address,
                    location = com.google.android.gms.maps.model.LatLng(latRes, lngRes)
                )
            )
        }
        supermarkets
    }

    // Atualizado para aceitar raio e apiKey
    fun findClosestSupermarkets(radiusKm: Float, apiKey: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val location = getCurrentLocation()
                if (location != null) {
                    _uiState.value = _uiState.value.copy(userLocation = LatLng(location.latitude, location.longitude))
                    // Busca supermercados reais
                    val fetchedSupermarkets = fetchNearbySupermarketsFromGoogle(location.latitude, location.longitude, radiusKm, apiKey)
                    _uiState.value = _uiState.value.copy(isLoading = false, supermarkets = fetchedSupermarkets)
                } else {
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = "Could not get current location.")
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = "An error occurred: ${e.message}")
            }
        }
    }

    private suspend fun getCurrentLocation(): android.location.Location? {
        val context = getApplication<Application>().applicationContext
        val hasAccessFineLocationPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val hasAccessCoarseLocationPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasAccessFineLocationPermission || !hasAccessCoarseLocationPermission) {
            throw SecurityException("Location permission not granted.")
        }

        return fusedLocationClient.lastLocation.await()
    }
}

