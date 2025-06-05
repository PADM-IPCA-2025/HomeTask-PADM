package pt.ipca.hometask.presentation.viewModel.shopping

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pt.ipca.hometask.data.repository.AuthRepository
import pt.ipca.hometask.domain.model.ShoppingItem
import pt.ipca.hometask.domain.model.ShoppingList
import pt.ipca.hometask.domain.repository.ShoppingRepository

data class ShoppingListUiState(
    val isLoading: Boolean = false,
    val shoppingList: ShoppingList? = null,
    val shoppingItems: List<ShoppingItem> = emptyList(),
    val errorMessage: String? = null,
    val totalItems: Int = 0,
    val totalPrice: Float = 0f,
    val completedItems: Int = 0,
    val isUserLoggedIn: Boolean = false
)

class ShoppingListViewModel(
    private val repository: ShoppingRepository,
    context: Context  // Mudado de Application para Context
) : ViewModel() {  // Mudado de AndroidViewModel para ViewModel

    private val authRepository = AuthRepository(context)  // Usando context diretamente
    private val _uiState = MutableStateFlow(ShoppingListUiState())
    val uiState: StateFlow<ShoppingListUiState> = _uiState.asStateFlow()

    init {
        checkUserAuthentication()
    }

    private fun checkUserAuthentication() {
        _uiState.value = _uiState.value.copy(
            isUserLoggedIn = authRepository.isLoggedIn()
        )
    }

    fun loadShoppingList(listId: Int) {
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
                // Carregar a lista de compras
                val listResult = repository.getShoppingListById(listId)
                listResult.onSuccess { shoppingList ->
                    val userId = authRepository.getUserId()

                    // Verificar se o usuário tem permissão para acessar esta lista
                    if (userId != null && (shoppingList.homeId == userId || authRepository.isAdmin())) {
                        _uiState.value = _uiState.value.copy(
                            shoppingList = shoppingList
                        )

                        // Carregar os itens da lista usando a API real
                        loadShoppingItems(listId)
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = "Você não tem permissão para acessar esta lista"
                        )
                    }
                }.onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Erro ao carregar lista: ${exception.message}"
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

    // CORRIGIDO: Agora usa a API real em vez de dados mockados
    private suspend fun loadShoppingItems(listId: Int) {
        try {
            val itemsResult = repository.getItemsByShoppingList(listId)
            itemsResult.onSuccess { items ->
                updateShoppingItems(items)
                _uiState.value = _uiState.value.copy(isLoading = false)
            }.onFailure { exception ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Erro ao carregar itens: ${exception.message}"
                )
            }
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                errorMessage = "Erro inesperado ao carregar itens: ${e.message}"
            )
        }
    }

    private fun updateShoppingItems(items: List<ShoppingItem>) {
        val totalItems = items.size
        val totalPrice = items.sumOf { (it.quantity * it.price).toDouble() }.toFloat()
        val completedItems = items.count { it.state == "comprado" }

        _uiState.value = _uiState.value.copy(
            shoppingItems = items,
            totalItems = totalItems,
            totalPrice = totalPrice,
            completedItems = completedItems
        )
    }

    fun updateItemStatus(itemId: Int, completed: Boolean) {
        if (!authRepository.isLoggedIn()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Usuário não está logado"
            )
            return
        }

        viewModelScope.launch {
            try {
                val currentItems = _uiState.value.shoppingItems
                val itemToUpdate = currentItems.find { it.id == itemId }

                itemToUpdate?.let { item ->
                    val updatedItem = item.copy(
                        state = if (completed) "comprado" else "pendente"
                    )

                    val result = repository.updateShoppingItem(itemId, updatedItem)
                    result.onSuccess { updatedFromServer ->
                        // Usar a resposta do servidor para garantir consistência
                        val updatedItems = currentItems.map {
                            if (it.id == itemId) updatedFromServer else it
                        }
                        updateShoppingItems(updatedItems)
                    }.onFailure { exception ->
                        _uiState.value = _uiState.value.copy(
                            errorMessage = "Erro ao atualizar item: ${exception.message}"
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

    fun updateItemQuantity(itemId: Int, newQuantity: Float) {
        if (!authRepository.isLoggedIn()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Usuário não está logado"
            )
            return
        }

        if (newQuantity <= 0) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Quantidade deve ser maior que zero"
            )
            return
        }

        viewModelScope.launch {
            try {
                val currentItems = _uiState.value.shoppingItems
                val itemToUpdate = currentItems.find { it.id == itemId }

                itemToUpdate?.let { item ->
                    val updatedItem = item.copy(quantity = newQuantity)

                    val result = repository.updateShoppingItem(itemId, updatedItem)
                    result.onSuccess { updatedFromServer ->
                        // Usar a resposta do servidor para garantir consistência
                        val updatedItems = currentItems.map {
                            if (it.id == itemId) updatedFromServer else it
                        }
                        updateShoppingItems(updatedItems)
                    }.onFailure { exception ->
                        _uiState.value = _uiState.value.copy(
                            errorMessage = "Erro ao atualizar quantidade: ${exception.message}"
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

    fun removeItem(itemId: Int) {
        if (!authRepository.isLoggedIn()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Usuário não está logado"
            )
            return
        }

        viewModelScope.launch {
            try {
                val result = repository.deleteShoppingItem(itemId)
                result.onSuccess {
                    val currentItems = _uiState.value.shoppingItems
                    val updatedItems = currentItems.filter { it.id != itemId }
                    updateShoppingItems(updatedItems)
                }.onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "Erro ao remover item: ${exception.message}"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Erro inesperado: ${e.message}"
                )
            }
        }
    }

    fun refreshList() {
        val currentListId = _uiState.value.shoppingList?.id
        if (currentListId != null) {
            loadShoppingList(currentListId)
        }
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
}