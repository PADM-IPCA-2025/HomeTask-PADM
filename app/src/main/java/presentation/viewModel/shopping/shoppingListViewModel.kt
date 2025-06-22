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
    val isSaving: Boolean = false,
    val saveCompleted: Boolean = false,
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

    // Store the original list to compare changes
    private var originalShoppingItems: List<ShoppingItem> = emptyList()

    // Keep track of changes
    private val updatedItemIds = mutableSetOf<Int>()
    private val removedItemIds = mutableSetOf<Int>()

    init {
        checkUserAuthentication()
    }

    private fun checkUserAuthentication() {
        _uiState.value = _uiState.value.copy(
            isUserLoggedIn = authRepository.isLoggedIn()
        )
    }

    fun loadShoppingList(listId: Int) {
        android.util.Log.d("ShoppingListViewModel", "--- loadShoppingList START ---")
        android.util.Log.d("ShoppingListViewModel", "Attempting to load list with id: $listId")

        if (!authRepository.isLoggedIn()) {
            android.util.Log.w("ShoppingListViewModel", "User is not logged in.")
            _uiState.value = _uiState.value.copy(
                errorMessage = "Usuário não está logado",
                isUserLoggedIn = false
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            android.util.Log.d("ShoppingListViewModel", "Set state to loading.")

            try {
                android.util.Log.d("ShoppingListViewModel", "Calling repository.getShoppingListById($listId)")
                val listResult = repository.getShoppingListById(listId)

                listResult.onSuccess { shoppingList ->
                    android.util.Log.i("ShoppingListViewModel", "Successfully fetched shopping list: $shoppingList")
                    val userId = authRepository.getUserId()
                    android.util.Log.d("ShoppingListViewModel", "Current user id: $userId, List homeId: ${shoppingList.homeId}")

                    if (userId != null && (shoppingList.homeId == userId || authRepository.isAdmin())) {
                        android.util.Log.d("ShoppingListViewModel", "User has permission. Updating state.")
                        _uiState.value = _uiState.value.copy(
                            shoppingList = shoppingList
                        )
                        loadShoppingItems(listId)
                    } else {
                        android.util.Log.w("ShoppingListViewModel", "User does not have permission.")
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = "Você não tem permissão para acessar esta lista"
                        )
                    }
                }.onFailure { exception ->
                    android.util.Log.e("ShoppingListViewModel", "Failed to fetch shopping list.", exception)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Erro ao carregar lista: ${exception.message}"
                    )
                }
            } catch (e: Exception) {
                android.util.Log.e("ShoppingListViewModel", "An unexpected error occurred in loadShoppingList.", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Erro inesperado: ${e.message}"
                )
            }
            android.util.Log.d("ShoppingListViewModel", "--- loadShoppingList END ---")
        }
    }

    // CORRIGIDO: Agora usa a API real em vez de dados mockados
    private suspend fun loadShoppingItems(listId: Int) {
        android.util.Log.d("ShoppingListViewModel", "--- loadShoppingItems START for listId: $listId ---")
        try {
            val itemsResult = repository.getItemsByShoppingList(listId)
            itemsResult.onSuccess { items ->
                android.util.Log.i("ShoppingListViewModel", "Successfully fetched ${items.size} items: $items")
                originalShoppingItems = items // Store the original list
                updateShoppingItems(items)
                _uiState.value = _uiState.value.copy(isLoading = false)
                android.util.Log.d("ShoppingListViewModel", "Set state to not loading.")
            }.onFailure { exception ->
                android.util.Log.e("ShoppingListViewModel", "Failed to fetch shopping items.", exception)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Erro ao carregar itens: ${exception.message}"
                )
            }
        } catch (e: Exception) {
            android.util.Log.e("ShoppingListViewModel", "An unexpected error occurred in loadShoppingItems.", e)
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                errorMessage = "Erro inesperado ao carregar itens: ${e.message}"
            )
        }
        android.util.Log.d("ShoppingListViewModel", "--- loadShoppingItems END ---")
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
        val currentItems = _uiState.value.shoppingItems.toMutableList()
        val itemIndex = currentItems.indexOfFirst { it.id == itemId }

        if (itemIndex != -1) {
            val updatedItem = currentItems[itemIndex].copy(
                state = if (completed) "comprado" else "pendente"
            )
            currentItems[itemIndex] = updatedItem
            updatedItemIds.add(itemId) // Track updated item
            updateShoppingItems(currentItems)
        }
    }

    fun updateItemQuantity(itemId: Int, newQuantity: Float) {
        if (newQuantity <= 0) return

        val currentItems = _uiState.value.shoppingItems.toMutableList()
        val itemIndex = currentItems.indexOfFirst { it.id == itemId }

        if (itemIndex != -1) {
            val updatedItem = currentItems[itemIndex].copy(quantity = newQuantity)
            currentItems[itemIndex] = updatedItem
            updatedItemIds.add(itemId) // Track updated item
            updateShoppingItems(currentItems)
        }
    }

    fun removeItem(itemId: Int) {
        removedItemIds.add(itemId)
        updatedItemIds.remove(itemId) // No need to update if it's being removed
        val currentItems = _uiState.value.shoppingItems
        val updatedItems = currentItems.filter { it.id != itemId }
        updateShoppingItems(updatedItems)
    }

    fun saveChanges() {
        if (!authRepository.isLoggedIn()) {
            _uiState.value = _uiState.value.copy(errorMessage = "User not logged in")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, errorMessage = null)
            android.util.Log.d("ShoppingListViewModel", "--- Starting saveChanges ---")

            try {
                // Process removals
                if (removedItemIds.isNotEmpty()) {
                    android.util.Log.d("ShoppingListViewModel", "Deleting items: $removedItemIds")
                    removedItemIds.forEach { itemId ->
                        val result = repository.deleteShoppingItem(itemId)
                        result.onSuccess {
                            android.util.Log.i("ShoppingListViewModel", "Successfully deleted item $itemId")
                        }.onFailure { exception ->
                            android.util.Log.e("ShoppingListViewModel", "Failed to delete item $itemId", exception)
                        }
                    }
                }

                // Process updates
                if (updatedItemIds.isNotEmpty()){
                    android.util.Log.d("ShoppingListViewModel", "Updating items: $updatedItemIds")
                    val currentItems = _uiState.value.shoppingItems
                    val listId = _uiState.value.shoppingList?.id
                    if (listId == null) {
                        android.util.Log.e("ShoppingListViewModel", "Cannot update items, shoppingListId is null")
                        _uiState.value = _uiState.value.copy(errorMessage = "Error: Shopping List ID is missing.")
                        return@launch
                    }

                    updatedItemIds.forEach { itemId ->
                        val itemToUpdate = currentItems.find { it.id == itemId }
                        itemToUpdate?.let {
                            // Ensure the shoppingListId is correct and set category to 1 before sending
                            val finalItem = it.copy(
                                shoppingListId = listId,
                                itemCategoryId = 1 // Hardcoding category ID to 1 as requested
                            )
                            val result = repository.updateShoppingItem(finalItem.id!!, finalItem)
                            result.onSuccess { updatedItem ->
                                android.util.Log.i("ShoppingListViewModel", "Successfully updated item $itemId. Server response: $updatedItem")
                            }.onFailure { exception ->
                                 android.util.Log.e("ShoppingListViewModel", "Failed to update item $itemId", exception)
                            }
                        }
                    }
                }

                // Clear tracked changes and signal completion
                removedItemIds.clear()
                updatedItemIds.clear()
                _uiState.value = _uiState.value.copy(saveCompleted = true)
                 android.util.Log.d("ShoppingListViewModel", "--- saveChanges completed successfully ---")

            } catch (e: Exception) {
                 _uiState.value = _uiState.value.copy(errorMessage = "Error saving changes: ${e.message}")
                 android.util.Log.e("ShoppingListViewModel", "--- saveChanges failed with exception ---", e)
            } finally {
                _uiState.value = _uiState.value.copy(isSaving = false)
            }
        }
    }

    fun onSaveCompleted() {
        _uiState.value = _uiState.value.copy(saveCompleted = false)
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