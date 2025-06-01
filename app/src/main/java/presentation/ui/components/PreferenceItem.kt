package modules

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pt.ipca.hometask.R

@Composable
fun PreferenceItem(
    icon: ImageVector,
    title: String,
    modifier: Modifier = Modifier,
    hasSwitch: Boolean = false,
    isChecked: Boolean = false,
    onCheckedChange: ((Boolean) -> Unit)? = null,
    hasDropdown: Boolean = false,
    onDropdownClick: (() -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .clickable {
                if (hasSwitch && onCheckedChange != null) {
                    onCheckedChange(!isChecked)
                } else if (hasDropdown && onDropdownClick != null) {
                    onDropdownClick()
                } else {
                    onClick?.invoke()
                }
            }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Ícone
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = colorResource(id = R.color.secondary_blue),
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        // Título
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            color = colorResource(id = R.color.secondary_blue),
            modifier = Modifier.weight(1f)
        )

        // Switch ou Dropdown
        when {
            hasSwitch -> {
                Switch(
                    checked = isChecked,
                    onCheckedChange = onCheckedChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = colorResource(id = R.color.white),
                        checkedTrackColor = colorResource(id = R.color.secondary_blue),
                        uncheckedThumbColor = colorResource(id = R.color.white),
                        uncheckedTrackColor = colorResource(id = R.color.background)
                    )
                )
            }
            hasDropdown -> {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Dropdown",
                    tint = colorResource(id = R.color.secondary_blue),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }

    // Linha divisória
    HorizontalDivider(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        color = colorResource(id = R.color.secondary_blue).copy(alpha = 0.2f),
        thickness = 1.dp
    )
}