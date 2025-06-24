package pt.ipca.hometask.presentation.viewModel.shopping

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pt.ipca.hometask.data.repository.AuthRepository
import pt.ipca.hometask.data.repository.ResidentRepositoryImpl
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
    val isUserLoggedIn: Boolean = false,
    val canEditList: Boolean = false,
    val canViewList: Boolean = false
)

class ShoppingListViewModel(
    private val repository: ShoppingRepository,
    context: Context  // Mudado de Application para Context
) : ViewModel() {  // Mudado de AndroidViewModel para ViewModel

    private val authRepository = AuthRepository(context)  // Usando context diretamente
    private val residentRepository = ResidentRepositoryImpl()  // Para verificar residência
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

    private suspend fun checkUserPermissions(shoppingList: ShoppingList): Boolean {
        val userId = authRepository.getUserId()
        if (userId == null) {
            android.util.Log.w("ShoppingListViewModel", "❌ User ID is null")
            return false
        }

        android.util.Log.d("ShoppingListViewModel", "🔍 Checking permissions for user $userId, list homeId: ${shoppingList.homeId}")

        // Se o utilizador é admin, tem todas as permissões
        if (authRepository.isAdmin()) {
            android.util.Log.d("ShoppingListViewModel", "✅ User is admin, full permissions granted")
            _uiState.value = _uiState.value.copy(canViewList = true, canEditList = true)
            return true
        }

        // Verificar se o utilizador é residente da casa
        try {
            val residentsResult = residentRepository.getResidentsByHomeId(shoppingList.homeId)
            residentsResult.onSuccess { residents ->
                android.util.Log.d("ShoppingListViewModel", "🏠 Found ${residents.size} residents for home ${shoppingList.homeId}")
                android.util.Log.d("ShoppingListViewModel", "👥 Residents: ${residents.map { "${it.name} (${it.id})" }}")
                
                val isResident = residents.any { it.id == userId }
                if (isResident) {
                    android.util.Log.d("ShoppingListViewModel", "✅ User is resident of the home, view permissions granted")
                    _uiState.value = _uiState.value.copy(canViewList = true)
                    
                    // Se o utilizador criou a lista (userId == shoppingList.userId), pode editar
                    if (userId == shoppingList.userId) {
                        android.util.Log.d("ShoppingListViewModel", "👑 User is list owner, edit permissions granted")
                        _uiState.value = _uiState.value.copy(canEditList = true)
                    } else {
                        android.util.Log.d("ShoppingListViewModel", "👀 User is resident but not list owner, view-only permissions")
                        _uiState.value = _uiState.value.copy(canEditList = false)
                    }
                    return true
                } else {
                    android.util.Log.w("ShoppingListViewModel", "❌ User is not resident of the home")
                    return false
                }
            }.onFailure { exception ->
                android.util.Log.e("ShoppingListViewModel", "❌ Failed to check user residency", exception)
                return false
            }
        } catch (e: Exception) {
            android.util.Log.e("ShoppingListViewModel", "💥 Exception checking user permissions", e)
            return false
        }
        
        return false
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
                    android.util.Log.d("ShoppingListViewModel", "📅 List end date: ${shoppingList.endDate}")
                    android.util.Log.d("ShoppingListViewModel", "📋 List completed: ${shoppingList.endDate != null && shoppingList.endDate.isNotEmpty()}")
                    
                    // Verificar permissões do utilizador
                    val hasPermission = checkUserPermissions(shoppingList)
                    
                    if (hasPermission) {
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
        android.util.Log.d("ShoppingListViewModel", "🔄 updateItemStatus called: itemId=$itemId, completed=$completed")
        android.util.Log.d("ShoppingListViewModel", "📋 Current state: canViewList=${_uiState.value.canViewList}, canEditList=${_uiState.value.canEditList}")
        android.util.Log.d("ShoppingListViewModel", "📋 List completed: ${isListCompleted()}")
        android.util.Log.d("ShoppingListViewModel", "📅 End date: ${_uiState.value.shoppingList?.endDate}")
        
        // Verificar se pode editar itens
        if (!canEditItems()) {
            android.util.Log.w("ShoppingListViewModel", "❌ Cannot edit items - list completed or no permissions")
            return
        }

        val currentItems = _uiState.value.shoppingItems.toMutableList()
        val itemIndex = currentItems.indexOfFirst { it.id == itemId }

        if (itemIndex != -1) {
            val oldState = currentItems[itemIndex].state
            val newState = if (completed) "comprado" else "pendente"
            
            android.util.Log.d("ShoppingListViewModel", "📝 Updating item $itemId: $oldState -> $newState")
            
            val updatedItem = currentItems[itemIndex].copy(state = newState)
            currentItems[itemIndex] = updatedItem
            updatedItemIds.add(itemId) // Track updated item
            updateShoppingItems(currentItems)
            
            android.util.Log.d("ShoppingListViewModel", "✅ Item status updated successfully")
        } else {
            android.util.Log.e("ShoppingListViewModel", "❌ Item with id $itemId not found")
        }
    }

    fun updateItemQuantity(itemId: Int, newQuantity: Float) {
        // Verificar se pode editar itens
        if (!canEditItems()) {
            android.util.Log.w("ShoppingListViewModel", "❌ Cannot edit items - list completed or no permissions")
            return
        }

        // Apenas o dono da lista pode editar quantidades
        if (!_uiState.value.canEditList) {
            android.util.Log.w("ShoppingListViewModel", "❌ User cannot edit list, cannot update item quantity")
            return
        }

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
        // Verificar se pode editar itens
        if (!canEditItems()) {
            android.util.Log.w("ShoppingListViewModel", "❌ Cannot edit items - list completed or no permissions")
            return
        }

        // Apenas o dono da lista pode remover itens
        if (!_uiState.value.canEditList) {
            android.util.Log.w("ShoppingListViewModel", "❌ User cannot edit list, cannot remove item")
            return
        }

        removedItemIds.add(itemId)
        updatedItemIds.remove(itemId) // No need to update if it's being removed
        val currentItems = _uiState.value.shoppingItems
        val updatedItems = currentItems.filter { it.id != itemId }
        updateShoppingItems(updatedItems)
    }

    fun saveChanges() {
        android.util.Log.d("ShoppingListViewModel", "💾 saveChanges called")
        android.util.Log.d("ShoppingListViewModel", "📊 Updated items: $updatedItemIds")
        android.util.Log.d("ShoppingListViewModel", "🗑️ Removed items: $removedItemIds")
        
        if (!authRepository.isLoggedIn()) {
            android.util.Log.w("ShoppingListViewModel", "❌ User not logged in")
            _uiState.value = _uiState.value.copy(errorMessage = "User not logged in")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, errorMessage = null)
            android.util.Log.d("ShoppingListViewModel", "--- Starting saveChanges ---")

            try {
                // Process removals
                if (removedItemIds.isNotEmpty()) {
                    android.util.Log.d("ShoppingListViewModel", "🗑️ Processing removals: $removedItemIds")
                    removedItemIds.forEach { itemId ->
                        val result = repository.deleteShoppingItem(itemId)
                        result.onSuccess {
                            android.util.Log.i("ShoppingListViewModel", "✅ Successfully deleted item $itemId")
                        }.onFailure { exception ->
                            android.util.Log.e("ShoppingListViewModel", "❌ Failed to delete item $itemId", exception)
                        }
                    }
                }

                // Process updates
                if (updatedItemIds.isNotEmpty()){
                    android.util.Log.d("ShoppingListViewModel", "📝 Processing updates: $updatedItemIds")
                    val currentItems = _uiState.value.shoppingItems
                    val listId = _uiState.value.shoppingList?.id
                    if (listId == null) {
                        android.util.Log.e("ShoppingListViewModel", "❌ Cannot update items, shoppingListId is null")
                        _uiState.value = _uiState.value.copy(errorMessage = "Error: Shopping List ID is missing.")
                        return@launch
                    }

                    updatedItemIds.forEach { itemId ->
                        val itemToUpdate = currentItems.find { it.id == itemId }
                        itemToUpdate?.let {
                            android.util.Log.d("ShoppingListViewModel", "📝 Updating item $itemId: $it")
                            // Ensure the shoppingListId is correct and set category to 1 before sending
                            val finalItem = it.copy(
                                shoppingListId = listId,
                                itemCategoryId = 1 // Hardcoding category ID to 1 as requested
                            )
                            android.util.Log.d("ShoppingListViewModel", "📤 Sending update for item: $finalItem")
                            val result = repository.updateShoppingItem(finalItem.id!!, finalItem)
                            result.onSuccess { updatedItem ->
                                android.util.Log.i("ShoppingListViewModel", "✅ Successfully updated item $itemId. Server response: $updatedItem")
                            }.onFailure { exception ->
                                 android.util.Log.e("ShoppingListViewModel", "❌ Failed to update item $itemId", exception)
                            }
                        } ?: run {
                            android.util.Log.e("ShoppingListViewModel", "❌ Item $itemId not found in current items")
                        }
                    }
                } else {
                    android.util.Log.d("ShoppingListViewModel", "ℹ️ No items to update")
                }

                // Clear tracked changes and signal completion
                removedItemIds.clear()
                updatedItemIds.clear()
                _uiState.value = _uiState.value.copy(saveCompleted = true)
                 android.util.Log.d("ShoppingListViewModel", "✅ saveChanges completed successfully")

            } catch (e: Exception) {
                 android.util.Log.e("ShoppingListViewModel", "💥 saveChanges failed with exception", e)
                 _uiState.value = _uiState.value.copy(errorMessage = "Error saving changes: ${e.message}")
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

    // Métodos para verificar permissões na UI
    fun canEditList(): Boolean {
        return _uiState.value.canEditList && !isListCompleted()
    }

    fun canViewList(): Boolean {
        return _uiState.value.canViewList
    }

    fun isListOwner(): Boolean {
        val userId = authRepository.getUserId()
        val shoppingList = _uiState.value.shoppingList
        return userId != null && shoppingList != null && userId == shoppingList.userId
    }

    // Verificar se a lista está concluída (tem endDate preenchido)
    fun isListCompleted(): Boolean {
        val shoppingList = _uiState.value.shoppingList
        return shoppingList?.endDate != null && shoppingList.endDate.isNotEmpty()
    }

    // Verificar se pode editar itens (não pode editar se a lista estiver concluída)
    fun canEditItems(): Boolean {
        return _uiState.value.canViewList && !isListCompleted()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}