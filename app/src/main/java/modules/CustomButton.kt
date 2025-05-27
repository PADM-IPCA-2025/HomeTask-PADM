package modules

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pt.ipca.hometask.R

@Composable
fun CustomButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isDanger: Boolean = false
) {
    Button(
        onClick = onClick,
        modifier = if (isDanger) {
            modifier
                .alpha(0.7f)
                .border(
                    width = 1.dp,
                    color = Color(0xFFFF0000),
                    shape = RoundedCornerShape(size = 15.dp)
                )
                .width(328.dp)
                .height(70.dp)
                .background(
                    color = Color(0x0DFF0000),
                    shape = RoundedCornerShape(size = 15.dp)
                )
        } else {
            modifier
                .width(328.dp)
                .height(70.dp)
                .background(
                    color = Color(0xFF1B4B5F),
                    shape = RoundedCornerShape(size = 15.dp)
                )
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = if (isDanger) Color(0xFFFF0000) else Color.White
        )
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontSize = 16.sp,
                fontFamily = FontFamily(Font(R.font.inter_light)),
                fontWeight = FontWeight(400),
                color = if (isDanger) Color(0xFFFF0000) else Color.White
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CustomButtonPreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Botão normal
        CustomButton(
            text = "Continuar",
            onClick = { /* Ação do botão */ }
        )

        // Botão de perigo (logout, apagar, etc)
        CustomButton(
            text = "Logout",
            onClick = { /* Ação do botão */ },
            isDanger = true
        )
    }
} 