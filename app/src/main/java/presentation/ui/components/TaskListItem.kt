package modules

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pt.ipca.hometask.R
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.ui.graphics.Color
import coil.compose.AsyncImage

@Composable
fun TaskListItem(
    taskName: String,
    taskDate: String,
    photoUri: String?,
    isCompleted: Boolean,
    onStatusChange: (Boolean) -> Unit
) {
    Box(
        modifier = Modifier
            .width(380.dp)
            .height(70.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(colorResource(id = R.color.listitem_blue))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Image - use AsyncImage if photoUri is provided, otherwise use default image
            if (photoUri != null && photoUri != "photo_url") {
                AsyncImage(
                    model = photoUri,
                    contentDescription = "Task Image",
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop,
                    error = painterResource(id = R.drawable.ic_task_default)
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.ic_task_default),
                    contentDescription = "Task Image",
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = taskName,
                    fontSize = 16.sp,
                    color = colorResource(id = R.color.main_blue)
                )
                Text(
                    text = taskDate,
                    fontSize = 14.sp,
                    color = colorResource(id = R.color.white)
                )
            }

            RadioButton(
                selected = isCompleted,
                onClick = { onStatusChange(true) },
                colors = RadioButtonDefaults.colors(
                    selectedColor = Color.White,
                    unselectedColor = Color.White
                )
            )
        }
    }
}