package presentation.ui.task

import modules.TopBar
import modules.CustomTextBox
import modules.CustomButton
import modules.BottomMenuBar
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pt.ipca.hometask.R
import android.util.Log

@Composable
fun AddEditTaskScreen(
    isEditMode: Boolean = false,
    initialTaskName: String = "",
    initialDescription: String = "",
    initialGroup: String = "",
    initialStatus: String = "",
    initialDate: String = "",
    initialImageRes: Int? = null,
    onBackClick: () -> Unit = {},
    onSaveClick: (String, String, String, String, String) -> Unit = { _, _, _, _, _ -> },
    onRemoveClick: () -> Unit = {},
    onImageClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    var taskName by remember { mutableStateOf(initialTaskName) }
    var description by remember { mutableStateOf(initialDescription) }
    var selectedGroup by remember { mutableStateOf(initialGroup) }
    var selectedStatus by remember { mutableStateOf(initialStatus) }
    var selectedDate by remember { mutableStateOf(initialDate) }
    var selectedImageRes by remember { mutableStateOf(initialImageRes) }

    var showGroupDialog by remember { mutableStateOf(false) }
    var showStatusDialog by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    val groups = listOf("Kitchen", "Living Room", "Bedroom", "Bathroom", "Garden", "Other")
    val statuses = listOf("To Do", "In Progress", "Completed")

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(bottom = 220.dp)
                .verticalScroll(rememberScrollState())
        ) {
            TopBar(
                title = if (isEditMode) "Edit Task" else "Task Title",
                onBackClick = onBackClick
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Task Image
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .align(Alignment.CenterHorizontally)
                    .clip(CircleShape)
                    .background(colorResource(id = R.color.listitem_blue))
                    .clickable { onImageClick() },
                contentAlignment = Alignment.Center
            ) {
                if (selectedImageRes != null) {
                    Image(
                        painter = painterResource(id = selectedImageRes!!),
                        contentDescription = "Task Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Image",
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Task Name
            Text(
                text = "Task Name",
                fontSize = 14.sp,
                color = colorResource(id = R.color.secondary_blue).copy(alpha = 0.7f),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            CustomTextBox(
                value = taskName,
                onValueChange = { taskName = it },
                placeholder = "Enter task name"
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Description
            Text(
                text = "Task Description",
                fontSize = 14.sp,
                color = colorResource(id = R.color.secondary_blue).copy(alpha = 0.7f),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            CustomTextBox(
                value = description,
                onValueChange = { description = it },
                placeholder = "Enter task description"
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Group Dropdown
            Text(
                text = "Task Responsible",
                fontSize = 14.sp,
                color = colorResource(id = R.color.secondary_blue).copy(alpha = 0.7f),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            DropdownField(
                value = selectedGroup,
                placeholder = "Select group",
                onClick = { showGroupDialog = true }
            )

            Spacer(modifier = Modifier.height(24.dp))


            // Date
            Text(
                text = "Date",
                fontSize = 14.sp,
                color = colorResource(id = R.color.secondary_blue).copy(alpha = 0.7f),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            DateField(
                value = selectedDate,
                placeholder = "Select date",
                onClick = { showDatePicker = true }
            )
        }

        // Buttons
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(start = 16.dp, end = 16.dp, bottom = 140.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CustomButton(
                text = if (isEditMode) "Save Changes" else "Create Task",
                onClick = {
                    Log.d("AddEditTaskScreen", "Criando task: name=$taskName, desc=$description, group=$selectedGroup, status=$selectedStatus, date=$selectedDate")
                    onSaveClick(taskName, description, selectedGroup, selectedStatus, selectedDate)
                }
            )

            if (isEditMode) {
                CustomButton(
                    text = "Remove Task",
                    onClick = onRemoveClick,
                    isDanger = true
                )
            }
        }

        // Bottom Menu
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            BottomMenuBar(
                onHomeClick = onHomeClick,
                onProfileClick = onProfileClick
            )
        }

        // Group Selection Dialog
        if (showGroupDialog) {
            SelectionDialog(
                title = "Select Group",
                options = groups,
                selectedOption = selectedGroup,
                onOptionSelected = { selectedGroup = it },
                onDismiss = { showGroupDialog = false }
            )
        }

        // Status Selection Dialog
        if (showStatusDialog) {
            SelectionDialog(
                title = "Select Status",
                options = statuses,
                selectedOption = selectedStatus,
                onOptionSelected = { selectedStatus = it },
                onDismiss = { showStatusDialog = false }
            )
        }

        // Date Picker (simplified)
        if (showDatePicker) {
            AlertDialog(
                onDismissRequest = { showDatePicker = false },
                title = {
                    Text(
                        text = "Select Date",
                        color = colorResource(id = R.color.secondary_blue)
                    )
                },
                text = {
                    CustomTextBox(
                        value = selectedDate,
                        onValueChange = { selectedDate = it },
                        placeholder = "DD/MM/YYYY"
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = { showDatePicker = false }
                    ) {
                        Text(
                            text = "OK",
                            color = colorResource(id = R.color.secondary_blue)
                        )
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showDatePicker = false }
                    ) {
                        Text(
                            text = "Cancel",
                            color = colorResource(id = R.color.secondary_blue)
                        )
                    }
                },
                containerColor = colorResource(id = R.color.background)
            )
        }
    }
}

@Composable
private fun DropdownField(
    value: String,
    placeholder: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(328.dp)
            .height(60.dp)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = if (value.isEmpty()) placeholder else value,
                fontSize = 16.sp,
                color = if (value.isEmpty())
                    colorResource(id = R.color.secondary_blue).copy(alpha = 0.6f)
                else
                    colorResource(id = R.color.secondary_blue)
            )

            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Dropdown",
                tint = colorResource(id = R.color.secondary_blue),
                modifier = Modifier.size(24.dp)
            )
        }

        HorizontalDivider(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            color = colorResource(id = R.color.secondary_blue),
            thickness = 1.dp
        )
    }
}

@Composable
private fun DateField(
    value: String,
    placeholder: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(328.dp)
            .height(60.dp)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = if (value.isEmpty()) placeholder else value,
                fontSize = 16.sp,
                color = if (value.isEmpty())
                    colorResource(id = R.color.secondary_blue).copy(alpha = 0.6f)
                else
                    colorResource(id = R.color.secondary_blue)
            )

            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = "Date Picker",
                tint = colorResource(id = R.color.secondary_blue),
                modifier = Modifier.size(24.dp)
            )
        }

        HorizontalDivider(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            color = colorResource(id = R.color.secondary_blue),
            thickness = 1.dp
        )
    }
}

@Composable
private fun SelectionDialog(
    title: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                color = colorResource(id = R.color.secondary_blue)
            )
        },
        text = {
            Column {
                options.forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onOptionSelected(option)
                                onDismiss()
                            }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedOption == option,
                            onClick = {
                                onOptionSelected(option)
                                onDismiss()
                            },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = colorResource(id = R.color.secondary_blue)
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = option,
                            color = colorResource(id = R.color.secondary_blue)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(
                    text = "Cancel",
                    color = colorResource(id = R.color.secondary_blue)
                )
            }
        },
        containerColor = colorResource(id = R.color.background)
    )
}

@Preview(showBackground = true)
@Composable
fun AddEditTaskScreenPreview() {
    Column {
        // Add Mode
        AddEditTaskScreen(
            isEditMode = false,
            onBackClick = { /* Voltar */ },
            onSaveClick = { name, desc, group, status, date ->
                /* Criar tarefa */
            },
            onImageClick = { /* Selecionar imagem */ },
            onHomeClick = { /* Home */ },
            onProfileClick = { /* Profile */ }
        )
    }
}