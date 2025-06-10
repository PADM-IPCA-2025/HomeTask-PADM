package pt.ipca.hometask.presentation.viewModel.home

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pt.ipca.hometask.data.remote.api.HomeTaskApi
import pt.ipca.hometask.data.remote.api.UserAuthApi
import pt.ipca.hometask.data.remote.model.ResidentDto
import pt.ipca.hometask.domain.model.User
import pt.ipca.hometask.network.RetrofitClient
import retrofit2.HttpException

class InviteResidentViewModel : ViewModel() {
    private val api: HomeTaskApi = RetrofitClient.homeTaskApi
    private val userApi: UserAuthApi = RetrofitClient.userAuthApi
    val users = mutableStateOf<List<User>>(emptyList())
    val isLoading = mutableStateOf(false)
    val errorMessage = mutableStateOf<String?>(null)
    val inviteSuccess = mutableStateOf(false)

    fun loadAllUsers() {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            try {
                val response = userApi.getAllUsers()
                if (response.isSuccessful && response.body() != null) {
                    users.value = response.body()!!.map { dto ->
                        User(
                            id = dto.id,
                            name = dto.name,
                            email = dto.email,
                            roles = dto.role ?: dto.roles ?: "",
                            profilePicture = dto.profilePicture,
                            token = dto.token
                        )
                    }
                    android.util.Log.d("InviteResident", "Users carregados: " + users.value.joinToString { it.email ?: "null" })
                } else {
                    errorMessage.value = "Erro ao carregar utilizadores"
                }
            } catch (e: Exception) {
                errorMessage.value = e.message
            }
            isLoading.value = false
        }
    }

    fun inviteResidentByEmail(email: String, homeId: Int) {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            inviteSuccess.value = false
            android.util.Log.d("InviteResident", "Procurando user com email: $email")
            users.value.forEach { user ->
                android.util.Log.d("InviteResident", "Comparando: digitado=${email.trim().lowercase()} vs user=${user.email?.trim()?.lowercase()}")
            }
            val user = users.value.find { it.email?.trim()?.equals(email.trim(), ignoreCase = true) == true }
            if (user == null) {
                android.util.Log.e("InviteResident", "Utilizador não encontrado para email: $email")
                errorMessage.value = "Utilizador não encontrado"
                isLoading.value = false
                return@launch
            }
            try {
                android.util.Log.d("InviteResident", "Enviando convite para userId=${user.id}, homeId=$homeId")
                val residentDto = ResidentDto(userId = user.id!!, homeId = homeId)
                val response = api.createResident(residentDto)
                if (response.isSuccessful) {
                    android.util.Log.d("InviteResident", "Convite enviado com sucesso!")
                    inviteSuccess.value = true
                } else {
                    android.util.Log.e("InviteResident", "Erro ao convidar residente: ${response.message()}")
                    errorMessage.value = "Erro ao convidar residente"
                }
            } catch (e: HttpException) {
                android.util.Log.e("InviteResident", "Erro HTTP: ${e.code()}")
                errorMessage.value = "Erro HTTP: ${e.code()}"
            } catch (e: Exception) {
                android.util.Log.e("InviteResident", "Exceção: ${e.message}")
                errorMessage.value = e.message
            }
            isLoading.value = false
        }
    }
}

