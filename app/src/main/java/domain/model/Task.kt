package pt.ipca.hometask.domain.model

data class Task(
    val id: Int? = null,
    val title: String,
    val description: String,
    val date: String,
    val state: String,
    val photo: String? = null,
    val homeId: Int,
    val userId: Int,
    val taskCategoryId: Int
)