package Pages

import Modules.ListItem
import Modules.TaskListItem
import androidx.compose.foundation.layout.Column
import pt.ipca.hometask.R
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeMenu() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        ListItem(
            houseName = "Casa Principal",
            address = "Rua Example, 123",
            onEdit = { /* ação de editar */ },
            onDelete = { /* ação de deletar */ }
        )

        // Você pode adicionar mais ListItems aqui
        ListItem(
            houseName = "Casa de Férias",
            address = "Avenida da Praia, 456",
            onEdit = { /* ação de editar */ },
            onDelete = { /* ação de deletar */ }
        )

        TaskListItem(
            taskName = "Limpar Banheiro",
            taskDate = "Today at 14:00",
            imageRes = R.drawable.logotipo,  // Exemplo de recurso
            isCompleted = false,
            onStatusChange = { isCompleted ->
                // Handle status change
            }
        )

        TaskListItem(
            taskName = "Limpar Banheiro",
            taskDate = "Today at 14:00",
            imageRes = R.drawable.logotipo,  // Exemplo de recurso
            isCompleted = false,
            onStatusChange = { isCompleted ->
                // Handle status change
            }
        )
    }
}