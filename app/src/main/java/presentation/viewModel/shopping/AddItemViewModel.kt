package pt.ipca.hometask.presentation.viewModel.shopping

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pt.ipca.hometask.data.repository.AuthRepository
import pt.ipca.hometask.domain.model.ItemCategory
import pt.ipca.hometask.domain.model.ShoppingItem
import pt.ipca.hometask.domain.repository.ShoppingRepository

data class AddItemUiState(
    val isLoading: Boolean = false,
    val categories: List<ItemCategory> = emptyList(),
    val selectedCategory: ItemCategory? = null,
    val isItemSaved: Boolean = false,
    val errorMessage: String? = null,
    val isFormValid: Boolean = false,
    val isUserLoggedIn: Boolean = false,
    val currentUserId: Int? = null,
    val currentUserName: String? = null
)

class AddItemViewModel(
    private val repository: ShoppingRepository,
    private val context: Context
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
        if (authRepository.isLoggedIn()) {
            loadCategories()
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

    private fun loadCategories() {
        if (!authRepository.isLoggedIn()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Usuário não está logado"
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            try {
                val result = repository.getAllItemCategories()
                result.onSuccess { categories ->
                    _uiState.value = _uiState.value.copy(
                        categories = categories,
                        isLoading = false
                    )
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

    fun updateItemName(name: String) {
        _itemName.value = name
        validateForm()
    }

    fun updateQuantity(qty: String) {
        // Validar se é um número válido
        if (qty.isEmpty() || qty.toFloatOrNull() != null) {
            _quantity.value = qty
            validateForm()
        }
    }

    fun updatePricePerUnit(price: String) {
        // Validar se é um número decimal válido
        if (price.isEmpty() || price.toFloatOrNull() != null) {
            _pricePerUnit.value = price
            validateForm()
        }
    }

    fun selectCategory(category: ItemCategory) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
        validateForm()
    }

    fun selectCategoryByDescription(categoryDescription: String) {
        val category = _uiState.value.categories.find { it.description == categoryDescription }
        category?.let {
            selectCategory(it)
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
                _pricePerUnit.value.toFloat() > 0 &&
                _uiState.value.selectedCategory != null

        _uiState.value = _uiState.value.copy(isFormValid = isValid)
    }

    fun saveItem(shoppingListId: Int) {
        if (!authRepository.isLoggedIn()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Usuário não está logado"
            )
            return
        }

        if (!_uiState.value.isFormValid) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Por favor, preencha todos os campos corretamente"
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

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            try {
                val selectedCategory = _uiState.value.selectedCategory!!

                val newItem = ShoppingItem(
                    description = _itemName.value.trim(),
                    quantity = _quantity.value.toFloat(),
                    state = "pendente", // Estado inicial
                    price = _pricePerUnit.value.toFloat(),
                    shoppingListId = shoppingListId,
                    itemCategoryId = selectedCategory.id!!
                )

                val result = repository.createShoppingItem(newItem)
                result.onSuccess { createdItem ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isItemSaved = true
                    )
                    clearForm()
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

    fun createNewCategory(description: String) {
        if (!authRepository.isLoggedIn()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Usuário não está logado"
            )
            return
        }

        viewModelScope.launch {
            try {
                val newCategory = ItemCategory(description = description.trim())
                val result = repository.createItemCategory(newCategory)

                result.onSuccess { createdCategory ->
                    // Recarregar categorias para incluir a nova
                    loadCategories()
                    // Selecionar automaticamente a nova categoria
                    selectCategory(createdCategory)
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

    private fun clearForm() {
        _itemName.value = ""
        _quantity.value = ""
        _pricePerUnit.value = ""
        _uiState.value = _uiState.value.copy(
            selectedCategory = null,
            isFormValid = false
        )
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun resetSavedState() {
        _uiState.value = _uiState.value.copy(isItemSaved = false)
    }

    // Função para facilitar o uso no screen com os parâmetros atuais
    fun saveItemWithCurrentData(shoppingListId: Int, itemName: String, quantity: String, pricePerUnit: String, categoryDescription: String) {
        updateItemName(itemName)
        updateQuantity(quantity)
        updatePricePerUnit(pricePerUnit)
        selectCategoryByDescription(categoryDescription)

        // Aguardar um frame para que as validações sejam processadas
        viewModelScope.launch {
            saveItem(shoppingListId)
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

    fun logout() {
        authRepository.logout()
        _uiState.value = AddItemUiState() // Reset completo do estado
        _itemName.value = ""
        _quantity.value = ""
        _pricePerUnit.value = ""
    }

    fun retryAfterLogin() {
        checkUserAuthentication()
        if (authRepository.isLoggedIn()) {
            loadCategories()
        }
    }
}