package modules


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.colorResource
import pt.ipca.hometask.R

@Composable
fun TopBar(
    title: String,
    onBackClick: () -> Unit,
    rightIcon: ImageVector? = null,
    onRightIconClick: (() -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Voltar atrás",
                    tint = colorResource(id = R.color.secondary_blue)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            if (rightIcon != null && onRightIconClick != null) {
                Icon(
                    imageVector = rightIcon,
                    contentDescription = "Ícone à direita",
                    tint = colorResource(id = R.color.secondary_blue),
                    modifier = Modifier
                        .clickable { onRightIconClick() }
                        .size(24.dp)
                )
            }
        }

        Text(
            text = title,
            fontSize = 20.sp,
            fontFamily = FontFamily(Font(R.font.inter_bold)),
            fontWeight = FontWeight.Bold,
            color = colorResource(id = R.color.secondary_blue)
        )
    }
}
