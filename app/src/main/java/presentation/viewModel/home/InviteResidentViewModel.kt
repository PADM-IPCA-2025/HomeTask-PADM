package pt.ipca.hometask.presentation.viewModel.home

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pt.ipca.hometask.data.remote.api.HomeTaskApi
import pt.ipca.hometask.data.remote.model.ResidentDto
import pt.ipca.hometask.domain.model.User
import pt.ipca.hometask.domain.repository.UserRepository
import pt.ipca.hometask.network.RetrofitClient

class InviteResidentViewModel : ViewModel() {
    private val api: HomeTaskApi = RetrofitClient.homeTaskApi
    private val userRepository: UserRepository = pt.ipca.hometask.data.repository.UserRepositoryImpl()
    
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
                android.util.Log.d("InviteResident", "Chamando repository getAllUsers()")
                val result = userRepository.getAllUsers()
                
                result.fold(
                    onSuccess = { userList ->
                        android.util.Log.d("InviteResident", "Utilizadores carregados com sucesso: ${userList.size} utilizadores")
                        users.value = userList
                    },
                    onFailure = { exception ->
                        android.util.Log.e("InviteResident", "Erro ao carregar utilizadores", exception)
                        errorMessage.value = exception.message ?: "Erro ao carregar utilizadores"
                    }
                )
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
            } catch (e: Exception) {
                android.util.Log.e("InviteResident", "Exceção: ${e.message}")
                errorMessage.value = e.message
            }
            isLoading.value = false
        }
    }
}

