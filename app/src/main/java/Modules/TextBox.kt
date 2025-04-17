package Modules

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pt.ipca.hometask.R
import pt.ipca.hometask.ui.theme.HomeTaskTheme

@Composable
fun CustomTextBox(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    singleLine: Boolean = true,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    isPassword: Boolean = false,
    trailingIcon: ImageVector? = null,
    onTrailingIconClick: (() -> Unit)? = null
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .padding(0.dp)
            .width(328.dp)
            .height(60.dp)
    ) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(id = R.color.background)),
            placeholder = { 
                Text(
                    text = placeholder,
                    color = colorResource(id = R.color.secondary_blue)
                ) 
            },
            keyboardOptions = keyboardOptions,
            visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else visualTransformation,
            singleLine = singleLine,
            enabled = enabled,
            readOnly = readOnly,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = colorResource(id = R.color.background),
                unfocusedContainerColor = colorResource(id = R.color.background),
                disabledContainerColor = colorResource(id = R.color.background),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                focusedTextColor = colorResource(id = R.color.secondary_blue),
                unfocusedTextColor = colorResource(id = R.color.secondary_blue),
                disabledTextColor = colorResource(id = R.color.secondary_blue)
            ),
            trailingIcon = {
                if (isPassword) {
                    IconButton(
                        onClick = { passwordVisible = !passwordVisible }
                    ) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = if (passwordVisible) "Hide password" else "Show password",
                            tint = colorResource(id = R.color.secondary_blue)
                        )
                    }
                } else if (trailingIcon != null && onTrailingIconClick != null) {
                    IconButton(
                        onClick = onTrailingIconClick
                    ) {
                        Icon(
                            imageVector = trailingIcon,
                            contentDescription = "Custom icon",
                            tint = colorResource(id = R.color.secondary_blue)
                        )
                    }
                }
            }
        )
        HorizontalDivider(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            color = colorResource(id = R.color.secondary_blue),
            thickness = 1.dp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CustomTextBoxPreview() {
    HomeTaskTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // TextBox vazio com placeholder
            CustomTextBox(
                value = "",
                onValueChange = {},
                placeholder = "Digite algo..."
            )

            // TextBox com texto
            CustomTextBox(
                value = "Texto de exemplo",
                onValueChange = {}
            )

            // TextBox desabilitado
            CustomTextBox(
                value = "Desabilitado",
                onValueChange = {},
                enabled = false
            )

            // TextBox somente leitura
            CustomTextBox(
                value = "Somente leitura",
                onValueChange = {},
                readOnly = true
            )

            // TextBox de senha
            CustomTextBox(
                value = "",
                onValueChange = {},
                placeholder = "Senha",
                isPassword = true
            )

            // TextBox com ícone personalizado
            CustomTextBox(
                value = "",
                onValueChange = {},
                placeholder = "Com ícone personalizado",
                trailingIcon = Icons.Filled.Visibility,
                onTrailingIconClick = { /* Ação do ícone */ }
            )
        }
    }
}