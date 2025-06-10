package presentation.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import modules.TopBar
import modules.CustomTextBox
import modules.CustomButton
import modules.BottomMenuBar
import pt.ipca.hometask.presentation.viewModel.home.AddHouseViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.ui.res.colorResource
import pt.ipca.hometask.R
import androidx.compose.foundation.border
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.background
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke

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
    var name by remember { mutableStateOf(initialHomeName) }
    var address by remember { mutableStateOf(initialAddress) }
    val uiState by viewModel.uiState.collectAsState()
    val secondaryBlue = colorResource(id = R.color.secondary_blue)

    // Observar o estado de sucesso
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onBackClick() // Voltar para o HomeScreen
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            TopBar(
                title = "Add House",
                onBackClick = onBackClick
            )
            Spacer(modifier = Modifier.height(40.dp))

            // Campo Nome da Casa
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                CustomTextBox(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = "Enter house name"
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Campo Endereço
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                CustomTextBox(
                    value = address,
                    onValueChange = { address = it },
                    placeholder = "Enter address"
                )
            }

            Spacer(modifier = Modifier.height(60.dp))

            // Botão Save
            CustomButton(
                text = "Save",
                onClick = { 
                    viewModel.createHome(name, address, "1")
                }
            )

            if (isEditMode) {
                Spacer(modifier = Modifier.height(16.dp))
                // Botão Remove
                CustomButton(
                    text = "Remove House",
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

@Preview(showBackground = true)
@Composable
fun AddEditHouseScreenPreview() {
    AddEditHouseScreen(
        onBackClick = { },
        onSaveClick = { _, _, _ -> },
        onRemoveClick = { },
        onHomeClick = { },
        onProfileClick = { }
    )
}