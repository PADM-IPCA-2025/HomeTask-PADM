package Modules


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pt.ipca.hometask.R

@Composable
fun SliderListItem(
    title: String,
    subtitle: String,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var offsetX by remember { mutableStateOf(0f) }

    Box(
        modifier = Modifier
            .width(350.dp)
            .height(70.dp)
            .background(Color.Transparent)
    ) {
        // Background actions (edit/delete)
        Row(
            modifier = Modifier
                .matchParentSize()
                .background(Color(0xFFB0BEC5)), // light gray background for actions
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onEdit) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.icon),
                    contentDescription = "Edit",
                    tint = Color.White
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_delete),
                    contentDescription = "Delete",
                    tint = Color.White
                )
            }
        }

        // Foreground content
        Column(
            modifier = Modifier
                .offset { IntOffset(offsetX.toInt(), 0) }
                .fillMaxHeight()
                .background(Color(0xFF33969D), RoundedCornerShape(10.dp))
                .pointerInput(Unit) {
                    detectHorizontalDragGestures { _, dragAmount ->
                        offsetX = (offsetX + dragAmount).coerceIn(-150f, 0f)
                    }
                }
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(text = title, fontWeight = FontWeight.Bold, color = Color.Black)
            Text(text = subtitle, fontSize = 14.sp, color = Color.White)
        }
    }
}

@Composable
fun ImageRadioListItem(
    imageRes: Int,
    title: String,
    subtitle: String,
    selected: Boolean,
    onSelect: () -> Unit
) {
    Row(
        modifier = Modifier
            .width(350.dp)
            .height(70.dp)
            .background(Color(0xFF33969D), RoundedCornerShape(10.dp))
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = null,
            modifier = Modifier
                .size(50.dp)
                .padding(end = 8.dp),
            contentScale = ContentScale.Crop
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontWeight = FontWeight.Bold, color = Color.Black)
            Text(text = subtitle, fontSize = 14.sp, color = Color.White)
        }

        RadioButton(
            selected = selected,
            onClick = onSelect
        )
    }
}
