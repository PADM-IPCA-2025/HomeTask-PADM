package pt.ipca.hometask.presentation.ui.shopping

import android.Manifest
import pt.ipca.hometask.R
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import android.util.Log
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import modules.BottomMenuBar
import modules.TopBar
import pt.ipca.hometask.presentation.viewModel.shopping.ClosestSupermarketViewModel
import com.google.maps.android.compose.Circle
import androidx.compose.ui.platform.LocalContext
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.width
import pt.ipca.hometask.presentation.viewModel.shopping.Supermarket

@Composable
fun ClosestSuperMarketScreen(
    onBackClick: () -> Unit,
    onHomeClick: () -> Unit,
    onProfileClick: () -> Unit,
    viewModel: ClosestSupermarketViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val locationPermissions = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    var searchRadius by remember { mutableStateOf(5f) }

    val apiKey = stringResource(id = R.string.API_KEY)

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            if (permissions.values.all { it }) {
                viewModel.findClosestSupermarkets(searchRadius, apiKey)
            }
        }
    )

    LaunchedEffect(Unit) {
        permissionLauncher.launch(locationPermissions)
    }

    // Estado para o diálogo e supermercado selecionado
    var showDialog by remember { mutableStateOf(false) }
    var selectedSupermarket by remember { mutableStateOf<Supermarket?>(null) }

    // Estado para fullscreen do mapa
    var showFullMap by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(bottom = 70.dp) // Space for BottomMenuBar
        ) {
            TopBar(title = "Closest Supermarket", onBackClick = onBackClick)

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (uiState.errorMessage != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = uiState.errorMessage!!,
                        color = colorResource(id = R.color.main_blue),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    // Map View
                    val cameraPositionState = rememberCameraPositionState {
                        position = CameraPosition.fromLatLngZoom(uiState.userLocation ?: LatLng(41.55, -8.42), 14f)
                    }

                    // Filtrar supermercados pelo raio
                    val filteredSupermarkets = uiState.userLocation?.let { userLoc ->
                        uiState.supermarkets.filter { supermarket ->
                            val results = FloatArray(1)
                            android.location.Location.distanceBetween(
                                userLoc.latitude, userLoc.longitude,
                                supermarket.location.latitude, supermarket.location.longitude,
                                results
                            )
                            results[0] <= searchRadius * 1000 // metros
                        }
                    } ?: emptyList()

                    // Update camera when user location changes
                    LaunchedEffect(uiState.userLocation) {
                        uiState.userLocation?.let {
                            cameraPositionState.position = CameraPosition.fromLatLngZoom(it, 14f)
                        }
                    }

                    // RESTORE: Card original do mapa
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        GoogleMap(
                            modifier = Modifier.fillMaxSize(),
                            cameraPositionState = cameraPositionState
                        ) {
                            Log.d("MAP_DEBUG", "GoogleMap Composable rendered")
                            // Pinpoint especial para a localização do utilizador
                            uiState.userLocation?.let {
                                Marker(
                                    state = MarkerState(position = it),
                                    title = "Você está aqui"
                                )
                                // Desenhar círculo do raio
                                Circle(
                                    center = it,
                                    radius = (searchRadius * 1000).toDouble(), // conversão explícita para Double
                                    fillColor = Color(0x22007AFF), // azul claro semi-transparente
                                    strokeColor = Color(0xFF007AFF),
                                    strokeWidth = 2f
                                )
                            }
                            // Supermarket markers dentro do raio
                            filteredSupermarkets.forEach { supermarket ->
                                Marker(
                                    state = MarkerState(position = supermarket.location),
                                    title = supermarket.name,
                                    snippet = supermarket.address
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Slider de raio de procura mais bonito
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                        colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.background))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Definir raio de procura",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = colorResource(id = R.color.secondary_blue)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Slider(
                                    value = searchRadius,
                                    onValueChange = { searchRadius = it },
                                    valueRange = 1f..25f,
                                    steps = 24,
                                    modifier = Modifier.weight(1f),
                                    colors = SliderDefaults.colors(
                                        thumbColor = colorResource(id = R.color.secondary_blue),
                                        activeTrackColor = colorResource(id = R.color.secondary_blue),
                                        inactiveTrackColor = colorResource(id = R.color.secondary_blue).copy(alpha = 0.2f)
                                    )
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "${searchRadius.toInt()} km",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = colorResource(id = R.color.secondary_blue)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    filteredSupermarkets.forEach { supermarket ->
                        SupermarketListItem(
                            name = supermarket.name,
                            address = supermarket.address,
                            onClick = {
                                selectedSupermarket = supermarket
                                showDialog = true
                            }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // AlertDialog para abrir no Google Maps
                    val context = LocalContext.current
                    if (showDialog && selectedSupermarket != null) {
                        AlertDialog(
                            onDismissRequest = { showDialog = false },
                            title = { Text("Abrir no Google Maps?") },
                            text = { Text("Deseja abrir o Google Maps para este supermercado?") },
                            confirmButton = {
                                TextButton(onClick = {
                                    val supermarket = selectedSupermarket!!
                                    val gmmIntentUri = Uri.parse("geo:${supermarket.location.latitude},${supermarket.location.longitude}?q=${Uri.encode(supermarket.name)}")
                                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                                    mapIntent.setPackage("com.google.android.apps.maps")
                                    context.startActivity(mapIntent)
                                    showDialog = false
                                }) {
                                    Text("Sim")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showDialog = false }) {
                                    Text("Não")
                                }
                            }
                        )
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            BottomMenuBar(
                onHomeClick = onHomeClick,
                onProfileClick = onProfileClick
            )
        }
    }
}

@Composable
fun SupermarketListItem(name: String, address: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.listitem_blue)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = name,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = address,
                color = Color.White,
                fontSize = 14.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ClosestSuperMarketScreenPreview() {
    ClosestSuperMarketScreen(onBackClick = {}, onHomeClick = {}, onProfileClick = {})
}

