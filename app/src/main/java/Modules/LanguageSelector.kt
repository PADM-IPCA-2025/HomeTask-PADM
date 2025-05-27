package Modules

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import pt.ipca.hometask.R

@Composable
fun LanguageSelector(
    isVisible: Boolean,
    selectedLanguage: String,
    onLanguageSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    if (isVisible) {
        Dialog(onDismissRequest = onDismiss) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = colorResource(id = R.color.background)
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        text = "Select Language",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(id = R.color.secondary_blue),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    LanguageOption(
                        language = "English",
                        isSelected = selectedLanguage == "English",
                        onClick = {
                            onLanguageSelected("English")
                            onDismiss()
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    LanguageOption(
                        language = "Português",
                        isSelected = selectedLanguage == "Português",
                        onClick = {
                            onLanguageSelected("Português")
                            onDismiss()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun LanguageOption(
    language: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .background(
                if (isSelected) colorResource(id = R.color.secondary_blue).copy(alpha = 0.1f)
                else Color.Transparent
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = colorResource(id = R.color.secondary_blue),
                unselectedColor = colorResource(id = R.color.secondary_blue).copy(alpha = 0.6f)
            )
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = language,
            fontSize = 16.sp,
            color = colorResource(id = R.color.secondary_blue),
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LanguageSelectorPreview() {
    LanguageSelector(
        isVisible = true,
        selectedLanguage = "English",
        onLanguageSelected = { },
        onDismiss = { }
    )
}