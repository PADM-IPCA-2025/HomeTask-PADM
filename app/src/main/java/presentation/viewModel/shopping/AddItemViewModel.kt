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
import pt.ipca.hometask.domain.repository.ShoppingRepository

data class AddItemUiState(
    val isLoading: Boolean = false,
    val isItemSaved: Boolean = false,
    val errorMessage: String? = null,
    val isFormValid: Boolean = false,
    val isUserLoggedIn: Boolean = false,
    val currentUserId: Int? = null,
    val currentUserName: String? = null
)

class AddItemViewModel(
    private val repository: ShoppingRepository,
    context: Context
) : ViewModel() {

    private val authRepository = AuthRepository(context)
    private val _uiState = MutableStateFlow(AddItemUiState())
    val uiState: StateFlow<AddItemUiState> = _uiState.asStateFlow()

    // Form fields
    private val _itemName = MutableStateFlow("")
    val itemName: StateFlow<String> = _itemName.asStateFlow()

    private val _quantity = MutableStateFlow("")
    val quantity: StateFlow<String> = _quantity.asStateFlow()

    private val _pricePerUnit = MutableStateFlow("")
    val pricePerUnit: StateFlow<String> = _pricePerUnit.asStateFlow()

    init {
        checkUserAuthentication()
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

    fun updateItemName(name: String) {
        _itemName.value = name
        validateForm()
    }

    fun updateQuantity(qty: String) {
        if (qty.isEmpty() || qty.toFloatOrNull() != null) {
            _quantity.value = qty
            validateForm()
        }
    }

    fun updatePricePerUnit(price: String) {
        if (price.isEmpty() || price.toFloatOrNull() != null) {
            _pricePerUnit.value = price
            validateForm()
        }
    }

    private fun validateForm() {
        val isValid = authRepository.isLoggedIn() &&
                _itemName.value.isNotBlank() &&
                _quantity.value.isNotBlank() &&
                _quantity.value.toFloatOrNull() != null &&
                _quantity.value.toFloat() > 0 &&
                _pricePerUnit.value.isNotBlank() &&
                _pricePerUnit.value.toFloatOrNull() != null &&
                _pricePerUnit.value.toFloat() > 0

        _uiState.value = _uiState.value.copy(isFormValid = isValid)
    }

    fun saveItem(shoppingListId: Int) {
        android.util.Log.d("AddItemViewModel", "=== START saveItem ===")
        android.util.Log.d("AddItemViewModel", "ShoppingListId: $shoppingListId")
        android.util.Log.d("AddItemViewModel", "Item name: ${_itemName.value}")
        android.util.Log.d("AddItemViewModel", "Quantity: ${_quantity.value}")
        android.util.Log.d("AddItemViewModel", "Price per unit: ${_pricePerUnit.value}")
        
        if (shoppingListId <= 0) {
            android.util.Log.e("AddItemViewModel", "Invalid shoppingListId: $shoppingListId")
            _uiState.value = _uiState.value.copy(
                errorMessage = "ID da lista de compras inválido"
            )
            return
        }
        
        if (!authRepository.isLoggedIn()) {
            android.util.Log.e("AddItemViewModel", "User not logged in")
            _uiState.value = _uiState.value.copy(
                errorMessage = "Usuário não está logado"
            )
            return
        }

        if (!_uiState.value.isFormValid) {
            android.util.Log.e("AddItemViewModel", "Form is not valid")
            _uiState.value = _uiState.value.copy(
                errorMessage = "Por favor, preencha todos os campos corretamente"
            )
            return
        }

        val userId = authRepository.getUserId()
        if (userId == null) {
            android.util.Log.e("AddItemViewModel", "User ID not found")
            _uiState.value = _uiState.value.copy(
                errorMessage = "ID do usuário não encontrado"
            )
            return
        }

        viewModelScope.launch {
            android.util.Log.d("AddItemViewModel", "Starting to create shopping item...")
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            try {
                // Validações adicionais
                val description = _itemName.value.trim()
                val quantity = _quantity.value.toFloatOrNull()
                val price = _pricePerUnit.value.toFloatOrNull()
                
                if (description.isBlank()) {
                    android.util.Log.e("AddItemViewModel", "Description is blank")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Nome do item não pode estar vazio"
                    )
                    return@launch
                }
                
                if (quantity == null || quantity <= 0) {
                    android.util.Log.e("AddItemViewModel", "Invalid quantity: ${_quantity.value}")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Quantidade deve ser um número maior que zero"
                    )
                    return@launch
                }
                
                if (price == null || price <= 0) {
                    android.util.Log.e("AddItemViewModel", "Invalid price: ${_pricePerUnit.value}")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Preço deve ser um número maior que zero"
                    )
                    return@launch
                }
                
                val newItem = ShoppingItem(
                    description = description,
                    quantity = quantity,
                    state = "pendente",
                    price = price,
                    shoppingListId = shoppingListId,
                    itemCategoryId = 1 // Sempre usar categoria 1
                )
                
                android.util.Log.d("AddItemViewModel", "Created ShoppingItem object: $newItem")
                android.util.Log.d("AddItemViewModel", "Item description: ${newItem.description}")
                android.util.Log.d("AddItemViewModel", "Item quantity: ${newItem.quantity}")
                android.util.Log.d("AddItemViewModel", "Item state: ${newItem.state}")
                android.util.Log.d("AddItemViewModel", "Item price: ${newItem.price}")
                android.util.Log.d("AddItemViewModel", "Item shoppingListId: ${newItem.shoppingListId}")
                android.util.Log.d("AddItemViewModel", "Item itemCategoryId: ${newItem.itemCategoryId}")

                val result = repository.createShoppingItem(newItem)
                android.util.Log.d("AddItemViewModel", "Repository call completed")
                
                result.onSuccess { createdItem ->
                    android.util.Log.d("AddItemViewModel", "Item created successfully: $createdItem")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isItemSaved = true
                    )
                    clearForm()
                }.onFailure { exception ->
                    android.util.Log.e("AddItemViewModel", "Failed to create item", exception)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Erro ao salvar item: ${exception.message}"
                    )
                }
            } catch (e: Exception) {
                android.util.Log.e("AddItemViewModel", "Unexpected error creating item", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Erro inesperado: ${e.message}"
                )
            }
        }
    }

    private fun clearForm() {
        _itemName.value = ""
        _quantity.value = ""
        _pricePerUnit.value = ""
        _uiState.value = _uiState.value.copy(
            isFormValid = false
        )
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun resetSavedState() {
        _uiState.value = _uiState.value.copy(isItemSaved = false)
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
}