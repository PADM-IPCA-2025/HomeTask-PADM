package pt.ipca.hometask.domain.model

data class Home(
    val id: Int? = null,
    val name: String,
    val address: String,
    val zipCodeId: Int,
    val userId: Int
)
