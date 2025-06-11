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
    private val apiUser: UserAuthApi = RetrofitClient.userAuthApi
    val users = mutableStateOf<List<User>>(emptyList())
    val isLoading = mutableStateOf(false)
    val errorMessage = mutableStateOf<String?>(null)
    val inviteSuccess = mutableStateOf(false)

    fun loadAllUsers() {
        viewModelScope.launch {
            android.util.Log.d("InviteResident", "Iniciando loadAllUsers()")
            isLoading.value = true
            errorMessage.value = null
            try {
                android.util.Log.d("InviteResident", "Chamando API getAllUsers()")
                val response = apiUser.getAllUsers()
                android.util.Log.d("InviteResident", "Resposta recebida: success=${response.isSuccessful}, code=${response.code()}")
                
                if (!response.isSuccessful) {
                    val errorBody = response.errorBody()?.string()
                    android.util.Log.e("InviteResident", "Erro na resposta: $errorBody")
                    errorMessage.value = "Erro ao carregar utilizadores: ${response.code()}"
                    isLoading.value = false
                    return@launch
                }

                val responseBody = response.body()
                if (responseBody == null) {
                    android.util.Log.e("InviteResident", "Resposta vazia")
                    errorMessage.value = "Resposta vazia do servidor"
                    isLoading.value = false
                    return@launch
                }

                android.util.Log.d("InviteResident", """
                    API Response completa:
                    - Success: ${responseBody.success}
                    - Message: ${responseBody.message}
                    - Data: ${responseBody.data}
                """.trimIndent())
                
                if (responseBody.success && responseBody.data != null) {
                    android.util.Log.d("InviteResident", "Resposta bem sucedida, mapeando usuários")
                    
                    val userDto = responseBody.data
                    android.util.Log.d("InviteResident", """
                        Mapeando usuário:
                        - ID: ${userDto.id}
                        - Nome: ${userDto.name}
                        - Email: ${userDto.email}
                        - Roles: ${userDto.role ?: userDto.roles}
                        - Profile Picture: ${userDto.profilePicture}
                    """.trimIndent())
                    
                    val user = User(
                        id = userDto.id,
                        name = userDto.name ?: "Sem nome",
                        email = userDto.email ?: "Sem email",
                        roles = userDto.role ?: userDto.roles ?: "Resident",
                        profilePicture = userDto.profilePicture,
                        token = userDto.token
                    )
                    
                    users.value = listOf(user)
                    android.util.Log.d("InviteResident", "Usuário mapeado com sucesso")
                } else {
                    android.util.Log.e("InviteResident", "Erro na resposta da API: ${responseBody.message}")
                    errorMessage.value = responseBody.message ?: "Erro ao carregar utilizadores"
                }
            } catch (e: Exception) {
                android.util.Log.e("InviteResident", "Exceção ao carregar usuários", e)
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

