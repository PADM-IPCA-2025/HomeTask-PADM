package pt.ipca.hometask.presentation.viewModel.shopping

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pt.ipca.hometask.data.repository.AuthRepository
import pt.ipca.hometask.domain.model.ShoppingList
import pt.ipca.hometask.domain.model.ShoppingItem
import pt.ipca.hometask.domain.repository.ShoppingRepository

data class ShoppingListsUiState(
    val isLoading: Boolean = false,
    val allShoppingLists: List<ShoppingList> = emptyList(),
    val inProgressLists: List<ShoppingListWithTotal> = emptyList(),
    val historyLists: List<ShoppingListWithTotal> = emptyList(),
    val selectedTab: ShoppingListTab = ShoppingListTab.IN_PROGRESS,
    val errorMessage: String? = null,
    val isListCreated: Boolean = false,
    val isUserLoggedIn: Boolean = false,
    val currentUserId: Int? = null,
    val currentUserName: String? = null
)

data class ShoppingListWithTotal(
    val shoppingList: ShoppingList,
    val totalPrice: Float = 0f,
    val totalItems: Int = 0,
    val completedItems: Int = 0
)

enum class ShoppingListTab {
    IN_PROGRESS, HISTORY
}

class ShoppingListsViewModel(
    private val repository: ShoppingRepository,
    private val context: Context
) : ViewModel() {

    private val authRepository = AuthRepository(context)
    private val _uiState = MutableStateFlow(ShoppingListsUiState())
    val uiState: StateFlow<ShoppingListsUiState> = _uiState.asStateFlow()

    // Cache para evitar múltiplas chamadas da API para os mesmos dados
    private val itemsCache = mutableMapOf<Int, List<ShoppingItem>>()

    init {
        checkUserAuthentication()
        if (authRepository.isLoggedIn()) {
            loadShoppingListsForCurrentUser()
        }
    }

    private fun checkUserAuthentication() {
        val isLoggedIn = authRepository.isLoggedIn()
        _uiState.value = _uiState.value.copy(
            isUserLoggedIn = isLoggedIn,
            currentUserId = authRepository.getUserId(),
            currentUserName = authRepository.getUserName()
        )

        if (!isLoggedIn) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Usuário não está logado"
            )
        }
    }

    fun loadShoppingListsForCurrentUser() {
        if (!authRepository.isLoggedIn()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Usuário não está logado",
                isUserLoggedIn = false
            )
            return
        }

        val userId = authRepository.getUserId()
        if (userId == null) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "ID do usuário não encontrado"
            )
            return
        }

        loadShoppingListsByHome(userId)
    }

    fun loadShoppingListsByHome(homeId: Int) {
        // Verificar se o usuário está logado
        if (!authRepository.isLoggedIn()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Usuário não está logado",
                isUserLoggedIn = false
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            try {
                val result = repository.getShoppingListsByHome(homeId)
                result.onSuccess { lists ->
                    _uiState.value = _uiState.value.copy(
                        allShoppingLists = lists,
                        isLoading = false
                    )
                    // Carregar os totais para cada lista
                    loadListTotals(lists)
                }.onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Erro ao carregar listas: ${exception.message}"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Erro inesperado: ${e.message}"
                )
            }
        }
    }

    private suspend fun loadListTotals(lists: List<ShoppingList>) {
        val listsWithTotals = mutableListOf<ShoppingListWithTotal>()

        lists.forEach { shoppingList ->
            val listId = shoppingList.id ?: return@forEach

            try {
                // Se a lista já tem o total calculado pela API, usar esse valor
                val totalPrice = if (shoppingList.total != null) {
                    shoppingList.total.toFloat()
                } else {
                    // Caso contrário, calcular manualmente
                    val items = getItemsForList(listId)
                    itemsCache[listId] = items
                    items.sumOf { (it.quantity * it.price).toDouble() }.toFloat()
                }

                // Se a lista já tem os items, usar esses dados
                val totalItems = if (shoppingList.shoppingItems != null) {
                    shoppingList.shoppingItems.size
                } else {
                    val items = getItemsForList(listId)
                    itemsCache[listId] = items
                    items.size
                }

                val completedItems = if (shoppingList.shoppingItems != null) {
                    shoppingList.shoppingItems.count { it.state == "comprado" }
                } else {
                    val items = getItemsForList(listId)
                    itemsCache[listId] = items
                    items.count { it.state == "comprado" }
                }

                listsWithTotals.add(
                    ShoppingListWithTotal(
                        shoppingList = shoppingList,
                        totalPrice = totalPrice,
                        totalItems = totalItems,
                        completedItems = completedItems
                    )
                )
            } catch (e: Exception) {
                // Se falhar ao carregar itens de uma lista específica, usar dados da API ou valores padrão
                val totalPrice = shoppingList.total?.toFloat() ?: 0f
                val totalItems = shoppingList.shoppingItems?.size ?: 0
                val completedItems = shoppingList.shoppingItems?.count { it.state == "comprado" } ?: 0

                listsWithTotals.add(
                    ShoppingListWithTotal(
                        shoppingList = shoppingList,
                        totalPrice = totalPrice,
                        totalItems = totalItems,
                        completedItems = completedItems
                    )
                )
            }
        }

        val (inProgress, history) = separateListsByStatus(listsWithTotals)
        _uiState.value = _uiState.value.copy(
            inProgressLists = inProgress,
            historyLists = history
        )
    }

    // CORRIGIDO: Agora usa a API real em vez de dados mockados
    private suspend fun getItemsForList(listId: Int): List<ShoppingItem> {
        return try {
            val result = repository.getItemsByShoppingList(listId)
            result.getOrElse {
                println("⚠️ Erro ao buscar itens da lista $listId: ${it.message}")
                emptyList()
            }
        } catch (e: Exception) {
            println("⚠️ Exceção ao buscar itens da lista $listId: ${e.message}")
            emptyList()
        }
    }

    private fun separateListsByStatus(lists: List<ShoppingListWithTotal>): Pair<List<ShoppingListWithTotal>, List<ShoppingListWithTotal>> {
        // Listas com endDate preenchida são "History"
        // Listas sem endDate são "In Progress"
        val inProgress = lists.filter { it.shoppingList.endDate.isNullOrEmpty() }
        val history = lists.filter { !it.shoppingList.endDate.isNullOrEmpty() }
        return Pair(inProgress, history)
    }

    fun selectTab(tab: ShoppingListTab) {
        _uiState.value = _uiState.value.copy(selectedTab = tab)
    }

    fun getCurrentLists(): List<ShoppingListWithTotal> {
        return when (_uiState.value.selectedTab) {
            ShoppingListTab.IN_PROGRESS -> _uiState.value.inProgressLists
            ShoppingListTab.HISTORY -> _uiState.value.historyLists
        }
    }

    fun createShoppingList(title: String, homeId: Int? = null) {
        android.util.Log.d("ShoppingListsViewModel", "=== START createShoppingList ===")
        android.util.Log.d("ShoppingListsViewModel", "Title: $title")
        android.util.Log.d("ShoppingListsViewModel", "HomeId: $homeId")
        
        if (!authRepository.isLoggedIn()) {
            android.util.Log.e("ShoppingListsViewModel", "User not logged in")
            _uiState.value = _uiState.value.copy(
                errorMessage = "Usuário não está logado"
            )
            return
        }

        val targetHomeId = homeId ?: authRepository.getUserId()
        android.util.Log.d("ShoppingListsViewModel", "Target homeId: $targetHomeId")
        
        if (targetHomeId == null) {
            android.util.Log.e("ShoppingListsViewModel", "Home ID not found")
            _uiState.value = _uiState.value.copy(
                errorMessage = "ID da casa não encontrado"
            )
            return
        }

        val trimmedTitle = title.trim()
        if (trimmedTitle.isBlank()) {
            android.util.Log.e("ShoppingListsViewModel", "Title is blank")
            _uiState.value = _uiState.value.copy(
                errorMessage = "O título da lista não pode estar vazio"
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            try {
                val newList = ShoppingList(
                    title = trimmedTitle,
                    homeId = targetHomeId
                )
                android.util.Log.d("ShoppingListsViewModel", "Creating new list: $newList")

                val result = repository.createShoppingList(newList)
                result.onSuccess { createdList ->
                    android.util.Log.d("ShoppingListsViewModel", "List created successfully: $createdList")
                    
                    // Recarregar todas as listas da API para garantir que temos os dados corretos
                    val refreshResult = repository.getShoppingListsByHome(targetHomeId)
                    refreshResult.onSuccess { updatedLists ->
                        android.util.Log.d("ShoppingListsViewModel", "Lists refreshed from API: ${updatedLists.size} lists")
                        _uiState.value = _uiState.value.copy(
                            allShoppingLists = updatedLists,
                            isLoading = false,
                            isListCreated = true
                        )
                        
                        // Recarregar totais
                        loadListTotals(updatedLists)
                    }.onFailure { refreshException ->
                        android.util.Log.e("ShoppingListsViewModel", "Failed to refresh lists after creation", refreshException)
                        // Se falhar ao recarregar, pelo menos adicionar à lista local
                        val updatedAllLists = _uiState.value.allShoppingLists + createdList
                        _uiState.value = _uiState.value.copy(
                            allShoppingLists = updatedAllLists,
                            isLoading = false,
                            isListCreated = true
                        )
                        loadListTotals(updatedAllLists)
                    }
                }.onFailure { exception ->
                    android.util.Log.e("ShoppingListsViewModel", "Failed to create list", exception)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Erro ao criar lista: ${exception.message}"
                    )
                }
            } catch (e: Exception) {
                android.util.Log.e("ShoppingListsViewModel", "Unexpected error creating list", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Erro inesperado: ${e.message}"
                )
            }
        }
        android.util.Log.d("ShoppingListsViewModel", "=== END createShoppingList ===")
    }

    fun updateShoppingList(listId: Int, title: String) {
        if (!authRepository.isLoggedIn()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Usuário não está logado"
            )
            return
        }

        viewModelScope.launch {
            try {
                val listToUpdate = _uiState.value.allShoppingLists.find { it.id == listId }

                listToUpdate?.let { currentList ->
                    // Verificar se a lista pertence ao usuário atual
                    val currentUserId = authRepository.getUserId()
                    if (currentUserId != currentList.homeId && !authRepository.isAdmin()) {
                        _uiState.value = _uiState.value.copy(
                            errorMessage = "Você não tem permissão para editar esta lista"
                        )
                        return@let
                    }

                    val updatedList = currentList.copy(title = title.trim())

                    val result = repository.updateShoppingList(listId, updatedList)
                    result.onSuccess { updated ->
                        val updatedAllLists = _uiState.value.allShoppingLists.map {
                            if (it.id == listId) updated else it
                        }
                        _uiState.value = _uiState.value.copy(
                            allShoppingLists = updatedAllLists
                        )

                        // Recarregar totais
                        loadListTotals(updatedAllLists)
                    }.onFailure { exception ->
                        _uiState.value = _uiState.value.copy(
                            errorMessage = "Erro ao atualizar lista: ${exception.message}"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Erro inesperado: ${e.message}"
                )
            }
        }
    }

    fun deleteShoppingList(listId: Int) {
        if (!authRepository.isLoggedIn()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Usuário não está logado"
            )
            return
        }

        viewModelScope.launch {
            try {
                val listToDelete = _uiState.value.allShoppingLists.find { it.id == listId }

                listToDelete?.let { currentList ->
                    // Verificar se a lista pertence ao usuário atual
                    val currentUserId = authRepository.getUserId()
                    if (currentUserId != currentList.homeId && !authRepository.isAdmin()) {
                        _uiState.value = _uiState.value.copy(
                            errorMessage = "Você não tem permissão para deletar esta lista"
                        )
                        return@let
                    }
                }

                val result = repository.deleteShoppingList(listId)
                result.onSuccess {
                    val updatedAllLists = _uiState.value.allShoppingLists.filter { it.id != listId }
                    _uiState.value = _uiState.value.copy(
                        allShoppingLists = updatedAllLists
                    )

                    // Remover do cache
                    itemsCache.remove(listId)

                    // Recarregar totais
                    loadListTotals(updatedAllLists)
                }.onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "Erro ao deletar lista: ${exception.message}"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Erro inesperado: ${e.message}"
                )
            }
        }
    }

    fun markListAsCompleted(listId: Int) {
        if (!authRepository.isLoggedIn()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Usuário não está logado"
            )
            return
        }

        viewModelScope.launch {
            try {
                val listToUpdate = _uiState.value.allShoppingLists.find { it.id == listId }

                listToUpdate?.let { currentList ->
                    // Verificar se a lista pertence ao usuário atual
                    val currentUserId = authRepository.getUserId()
                    if (currentUserId != currentList.homeId && !authRepository.isAdmin()) {
                        _uiState.value = _uiState.value.copy(
                            errorMessage = "Você não tem permissão para marcar esta lista como concluída"
                        )
                        return@let
                    }

                    val currentDate = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()).format(java.util.Date())
                    val updatedList = currentList.copy(endDate = currentDate)

                    val result = repository.updateShoppingList(listId, updatedList)
                    result.onSuccess { updated ->
                        val updatedAllLists = _uiState.value.allShoppingLists.map {
                            if (it.id == listId) updated else it
                        }
                        _uiState.value = _uiState.value.copy(
                            allShoppingLists = updatedAllLists
                        )

                        // Recarregar totais
                        loadListTotals(updatedAllLists)
                    }.onFailure { exception ->
                        _uiState.value = _uiState.value.copy(
                            errorMessage = "Erro ao marcar lista como concluída: ${exception.message}"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Erro inesperado: ${e.message}"
                )
            }
        }
    }

    fun refreshList(listId: Int) {
        if (!authRepository.isLoggedIn()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Usuário não está logado"
            )
            return
        }

        viewModelScope.launch {
            try {
                // Verificar se a lista pertence ao usuário
                val listToRefresh = _uiState.value.allShoppingLists.find { it.id == listId }
                val currentUserId = authRepository.getUserId()

                if (listToRefresh != null && (currentUserId == listToRefresh.homeId || authRepository.isAdmin())) {
                    // Recarregar itens desta lista específica usando a API real
                    val items = getItemsForList(listId)
                    itemsCache[listId] = items

                    // Recarregar totais
                    loadListTotals(_uiState.value.allShoppingLists)
                } else {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "Você não tem permissão para atualizar esta lista"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Erro inesperado: ${e.message}"
                )
            }
        }
    }

    fun getListTotal(listId: Int): Float {
        // Verificar permissão antes de retornar dados
        val list = _uiState.value.allShoppingLists.find { it.id == listId }
        val currentUserId = authRepository.getUserId()

        if (list != null && (currentUserId == list.homeId || authRepository.isAdmin())) {
            val items = itemsCache[listId] ?: return 0f
            return items.sumOf { (it.quantity * it.price).toDouble() }.toFloat()
        }
        return 0f
    }

    fun getListProgress(listId: Int): Pair<Int, Int> {
        // Verificar permissão antes de retornar dados
        val list = _uiState.value.allShoppingLists.find { it.id == listId }
        val currentUserId = authRepository.getUserId()

        if (list != null && (currentUserId == list.homeId || authRepository.isAdmin())) {
            val items = itemsCache[listId] ?: return Pair(0, 0)
            val completed = items.count { it.state == "comprado" }
            val total = items.size
            return Pair(completed, total)
        }
        return Pair(0, 0)
    }

    fun getCurrentUserInfo(): Triple<Int?, String?, String?> {
        return if (authRepository.isLoggedIn()) {
            Triple(
                authRepository.getUserId(),
                authRepository.getUserName(),
                authRepository.getUserEmail()
            )
        } else {
            Triple(null, null, null)
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun resetCreatedState() {
        _uiState.value = _uiState.value.copy(isListCreated = false)
    }

    fun logout() {
        authRepository.logout()
        _uiState.value = ShoppingListsUiState() // Reset completo do estado
        itemsCache.clear()
    }

    fun retryAfterLogin() {
        checkUserAuthentication()
        if (authRepository.isLoggedIn()) {
            loadShoppingListsForCurrentUser()
        }
    }

    fun refreshLists() {
        loadShoppingListsForCurrentUser()
    }

    fun refreshListsByHome(homeId: Int) {
        loadShoppingListsByHome(homeId)
    }
}