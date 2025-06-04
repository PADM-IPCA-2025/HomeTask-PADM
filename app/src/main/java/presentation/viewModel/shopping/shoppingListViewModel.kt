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
    private val context: Context
) : ViewModel() {

    private val authRepository = AuthRepository(context)
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
        // Verificar se usuário está logado
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
                val result = repository.getShoppingListById(listId)
                result.onSuccess { shoppingList ->
                    // Verificar se a lista pertence ao usuário (através do homeId)
                    val userId = authRepository.getUserId()
                    if (userId != null && shoppingList.homeId == userId) {
                        _uiState.value = _uiState.value.copy(
                            shoppingList = shoppingList,
                            isLoading = false
                        )
                        // Carregar itens da lista
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
                        errorMessage = exception.message
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message
                )
            }
        }
    }

    private fun loadShoppingItems(listId: Int) {
        // Como não há endpoint específico para buscar itens por lista,
        // usando dados mockados que filtram pelo shoppingListId
        val mockItems = getMockItemsForList(listId)
        updateShoppingItems(mockItems)
    }

    // Método temporário com dados mockados - substituir quando tiver endpoint real
    private fun getMockItemsForList(listId: Int): List<ShoppingItem> {
        val userId = authRepository.getUserId() ?: return emptyList()

        return when (listId) {
            1 -> listOf(
                ShoppingItem(1, "Leite", 2f, "pendente", 1.50f, listId, 1),
                ShoppingItem(2, "Pão", 1f, "comprado", 0.80f, listId, 2),
                ShoppingItem(3, "Ovos", 12f, "pendente", 2.30f, listId, 1)
            )
            2 -> listOf(
                ShoppingItem(4, "Arroz", 1f, "comprado", 3.20f, listId, 2),
                ShoppingItem(5, "Feijão", 1f, "comprado", 2.50f, listId, 2)
            )
            3 -> listOf(
                ShoppingItem(6, "Maçãs", 2f, "comprado", 1.80f, listId, 3),
                ShoppingItem(7, "Bananas", 1f, "comprado", 1.20f, listId, 3)
            )
            else -> emptyList()
        }.filter {
            // Garantir que só mostra itens de listas do usuário logado
            true // Aqui você pode adicionar lógica adicional se necessário
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
                    result.onSuccess {
                        val updatedItems = currentItems.map {
                            if (it.id == itemId) updatedItem else it
                        }
                        updateShoppingItems(updatedItems)
                    }.onFailure { exception ->
                        _uiState.value = _uiState.value.copy(
                            errorMessage = exception.message
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message
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

        viewModelScope.launch {
            try {
                val currentItems = _uiState.value.shoppingItems
                val itemToUpdate = currentItems.find { it.id == itemId }

                itemToUpdate?.let { item ->
                    val updatedItem = item.copy(quantity = newQuantity)

                    val result = repository.updateShoppingItem(itemId, updatedItem)
                    result.onSuccess {
                        val updatedItems = currentItems.map {
                            if (it.id == itemId) updatedItem else it
                        }
                        updateShoppingItems(updatedItems)
                    }.onFailure { exception ->
                        _uiState.value = _uiState.value.copy(
                            errorMessage = exception.message
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message
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
                        errorMessage = exception.message
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message
                )
            }
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

    fun logout() {
        authRepository.logout()
        _uiState.value = ShoppingListUiState() // Reset completo do estado
    }
}