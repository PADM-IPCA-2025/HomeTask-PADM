package Modules

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pt.ipca.hometask.R
import kotlin.math.roundToInt

@Composable
fun ListItem(
    houseName: String,
    address: String,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var offsetX by remember { mutableStateOf(0f) }
    val swipeableDistance = 350f

    Box(
        modifier = Modifier
            .width(380.dp)
            .height(70.dp)
            .clip(RoundedCornerShape(10.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(id = R.color.secondary_blue)),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onEdit,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Edit",
                    tint = colorResource(id = R.color.white),
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = colorResource(id = R.color.white),
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
        }

        Box(
            modifier = Modifier
                .offset { IntOffset(offsetX.roundToInt(), 0) }
                .fillMaxSize()
                .background(colorResource(id = R.color.listitem_blue))
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            offsetX = if (offsetX < -swipeableDistance / 2) {
                                -swipeableDistance
                            } else {
                                0f
                            }
                        }
                    ) { _, dragAmount ->
                        val newOffset = offsetX + dragAmount
                        offsetX = newOffset.coerceIn(-swipeableDistance, 0f)
                    }
                }
        ) {
            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = houseName,
                    fontSize = 16.sp,
                    color = colorResource(id = R.color.main_blue)
                )
                Text(
                    text = address,
                    fontSize = 14.sp,
                    color = colorResource(id = R.color.white)
                )
            }
        }
    }
}



