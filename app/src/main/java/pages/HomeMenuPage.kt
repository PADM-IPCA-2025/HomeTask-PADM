package pages

import modules.BottomMenuBar
import modules.TaskListItem
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material3.Icon
import modules.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pt.ipca.hometask.R

@Composable
fun HomeMenu() {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)
                .padding(bottom = 70.dp)
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Hello, John",
                    fontSize = 20.sp,
                    color = colorResource(id = R.color.secondary_blue),
                    fontFamily = FontFamily(Font(R.font.inter_bold))
                )
                
                Icon(
                    imageVector = Icons.Default.AddCircleOutline,
                    contentDescription = "Notifications",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { /* Ação do ícone */ },
                    tint = colorResource(id = R.color.secondary_blue)
                )
            }

            Spacer(modifier = Modifier.height(60.dp))

            Text(
                text = "Homes:",
                fontSize = 20.sp,
                color = colorResource(id = R.color.secondary_blue),
                fontFamily = FontFamily(Font(R.font.inter_bold))
            )

            Spacer(modifier = Modifier.height(22.dp))

            Column(
                modifier = Modifier
                    .height(240.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                repeat(5) {
                    ListItem(
                        houseName = "Casa ${it + 1}",
                        address = "Endereço ${it + 1}",
                        onEdit = {},
                        onDelete = {}
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }

            Spacer(modifier = Modifier.height(60.dp))


            Text(
                text = "My Tasks:",
                fontSize = 20.sp,
                color = colorResource(id = R.color.secondary_blue),
                fontFamily = FontFamily(Font(R.font.inter_bold))
            )

            Spacer(modifier = Modifier.height(22.dp))

            Column(
                modifier = Modifier
                    .height(240.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                repeat(5) {
                    TaskListItem(
                        taskName = "Tarefa ${it + 1}",
                        taskDate = "Today at ${it + 1}:00",
                        imageRes = R.drawable.logotipo,
                        isCompleted = false,
                        onStatusChange = {}
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            BottomMenuBar(
                onHomeClick = {},
                onProfileClick = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeMenuPreview (){
    HomeMenu()
}