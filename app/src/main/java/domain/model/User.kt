package pt.ipca.hometask.domain.model

data class User(
    val id: Int? = null,
    val name: String,
    val email: String,
    val roles: String,
    val profilePicture: String? = null,
    val token: String? = null
)