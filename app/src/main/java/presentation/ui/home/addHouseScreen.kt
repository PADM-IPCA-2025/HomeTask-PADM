package presentation.ui.home

import android.util.Log
import modules.TopBar
import modules.CustomTextBox
import modules.CustomButton
import modules.BottomMenuBar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pt.ipca.hometask.R
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.foundation.clickable
import pt.ipca.hometask.presentation.viewModel.home.AddHouseViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditHouseScreen(
    isEditMode: Boolean = false,
    initialHomeName: String = "",
    initialAddress: String = "",
    initialZipCode: String = "",
    onBackClick: () -> Unit = {},
    onSaveClick: (String, String, String) -> Unit = { _, _, _ -> },
    onRemoveClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    viewModel: AddHouseViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var name by remember { mutableStateOf(initialHomeName) }
    var address by remember { mutableStateOf(initialAddress) }
    var selectedZipCodeId by remember { mutableStateOf<Int?>(null) }
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadZipCodes()
    }

    LaunchedEffect(uiState) {
        Log.d("AddEditHouseScreen", "UI State updated - Zip codes: ${uiState.zipCodes.size}")
        uiState.zipCodes.forEach { zip ->
            Log.d("AddEditHouseScreen", "Zip code: id=${zip.id}, postalCode=${zip.postalCode}, city=${zip.city}")
        }
    }

    LaunchedEffect(expanded) {
        Log.d("AddEditHouseScreen", "Dropdown expanded: $expanded")
        Log.d("AddEditHouseScreen", "Available zip codes: ${uiState.zipCodes.size}")
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(bottom = if (isEditMode) 220.dp else 140.dp)
                .verticalScroll(rememberScrollState())
        ) {
            TopBar(
                title = "Home Name",
                onBackClick = onBackClick
            )

            Spacer(modifier = Modifier.height(80.dp))

            // Home Name
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Home Name",
                    color = colorResource(id = R.color.secondary_blue).copy(alpha = 0.7f),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                CustomTextBox(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = "Home name"
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Address
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Address",
                    color = colorResource(id = R.color.secondary_blue).copy(alpha = 0.7f),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                CustomTextBox(
                    value = address,
                    onValueChange = { address = it },
                    placeholder = "Address"
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Zip Code Dropdown
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Zip Code",
                    color = colorResource(id = R.color.secondary_blue).copy(alpha = 0.7f),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Box {
                    val selectedZipCode = uiState.zipCodes.find { it.id == selectedZipCodeId }
                    OutlinedTextField(
                        value = selectedZipCode?.postalCode ?: "",
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expanded = true },
                        placeholder = { Text("Select Zip Code") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        }
                    )

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        uiState.zipCodes.forEach { zipCode ->
                            DropdownMenuItem(
                                text = { Text(zipCode.postalCode) },
                                onClick = {
                                    selectedZipCodeId = zipCode.id
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        }

        // Buttons
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(start = 16.dp, end = 16.dp, bottom = 140.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CustomButton(
                text = "Save",
                onClick = {
                    selectedZipCodeId?.let { zipId ->
                        viewModel.createHome(name, address, zipId.toString())
                    }
                }
            )

            if (isEditMode) {
                CustomButton(
                    text = "Remove Resident",
                    onClick = onRemoveClick,
                    isDanger = true
                )
            }
        }

        // Bottom Menu
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
fun AddEditHouseScreenPreview() {
    Column {
        AddEditHouseScreen(
            isEditMode = false,
            onBackClick = { },
            onSaveClick = { _, _, _ -> },
            onHomeClick = { },
            onProfileClick = { }
        )
    }
}