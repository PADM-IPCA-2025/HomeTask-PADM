package presentation.ui.home

import modules.TopBar
import modules.CustomTextBox
import modules.CustomButton
import modules.BottomMenuBar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pt.ipca.hometask.R

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
    onProfileClick: () -> Unit = {}
) {
    var homeName by remember { mutableStateOf(initialHomeName) }
    var address by remember { mutableStateOf(initialAddress) }
    var zipCode by remember { mutableStateOf(initialZipCode) }

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
                    value = homeName,
                    onValueChange = { homeName = it },
                    placeholder = "Enter home name"
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
                    placeholder = "Enter address"
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Zip Code
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

                CustomTextBox(
                    value = zipCode,
                    onValueChange = { zipCode = it },
                    placeholder = "Enter zip code",
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    )
                )
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
                    onSaveClick(homeName, address, zipCode)
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

@Preview(showBackground = true)
@Composable
fun AddEditHouseScreenPreview() {
    Column {
        // Add Mode Preview
        AddEditHouseScreen(
            isEditMode = false,
            onBackClick = { /* Voltar */ },
            onSaveClick = { homeName, address, zipCode ->
                /* Salvar casa */
            },
            onHomeClick = { /* Home */ },
            onProfileClick = { /* Profile */ }
        )
    }
}