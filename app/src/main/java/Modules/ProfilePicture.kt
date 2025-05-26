package Modules


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import pt.ipca.hometask.R

@Composable
fun ProfilePicture(
    imageRes: Int,
    modifier: Modifier = Modifier,
    size: Dp = 120.dp,
    showEditButton: Boolean = false,
    onEditClick: (() -> Unit)? = null,
    borderColor: Color = Color.Transparent,
    borderWidth: Dp = 0.dp
) {
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        // Foto de perfil
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = "Profile Picture",
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .border(
                    width = borderWidth,
                    color = borderColor,
                    shape = CircleShape
                ),
            contentScale = ContentScale.Crop
        )

        // Botão de editar (se habilitado)
        if (showEditButton && onEditClick != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = (-8).dp, y = (-8).dp)
            ) {
                IconButton(
                    onClick = onEditClick,
                    modifier = Modifier
                        .size(36.dp)
                        .background(
                            color = colorResource(id = R.color.main_blue),
                            shape = CircleShape
                        )
                        .border(
                            width = 2.dp,
                            color = Color.White,
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Profile Picture",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfilePicturePreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Foto de perfil sem botão de editar
        ProfilePicture(
            imageRes = R.drawable.ic_launcher_background, // Substitua pela sua imagem
            size = 120.dp
        )

        // Foto de perfil com botão de editar
        ProfilePicture(
            imageRes = R.drawable.ic_launcher_background, // Substitua pela sua imagem
            size = 120.dp,
            showEditButton = true,
            onEditClick = { /* Ação para editar foto */ }
        )

        // Foto de perfil pequena sem botão
        ProfilePicture(
            imageRes = R.drawable.ic_launcher_background, // Substitua pela sua imagem
            size = 80.dp
        )

        // Foto de perfil com borda
        ProfilePicture(
            imageRes = R.drawable.ic_launcher_background, // Substitua pela sua imagem
            size = 100.dp,
            showEditButton = true,
            onEditClick = { /* Ação para editar foto */ },
            borderColor = Color.Gray,
            borderWidth = 2.dp
        )
    }
}