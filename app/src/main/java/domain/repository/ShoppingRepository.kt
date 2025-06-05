package pt.ipca.hometask.domain.repository

import pt.ipca.hometask.domain.model.ShoppingList
import pt.ipca.hometask.domain.model.ShoppingItem
import pt.ipca.hometask.domain.model.ItemCategory


interface ShoppingRepository {
    // Shopping Lists
    suspend fun createShoppingList(shoppingList: ShoppingList): Result<ShoppingList>
    suspend fun getShoppingListById(id: Int): Result<ShoppingList>
    suspend fun getShoppingListsByHome(homeId: Int): Result<List<ShoppingList>>
    suspend fun updateShoppingList(id: Int, shoppingList: ShoppingList): Result<ShoppingList>
    suspend fun deleteShoppingList(id: Int): Result<Unit>

    // Shopping Items
    suspend fun createShoppingItem(item: ShoppingItem): Result<ShoppingItem>
    suspend fun getShoppingItemById(id: Int): Result<ShoppingItem>
    suspend fun updateShoppingItem(id: Int, item: ShoppingItem): Result<ShoppingItem>
    suspend fun deleteShoppingItem(id: Int): Result<Unit>
    suspend fun getItemsByShoppingList(shoppingListId: Int): Result<List<ShoppingItem>>

    // Item Categories
    suspend fun createItemCategory(category: ItemCategory): Result<ItemCategory>
    suspend fun getAllItemCategories(): Result<List<ItemCategory>>
    suspend fun getItemCategoryById(id: Int): Result<ItemCategory>
    suspend fun updateItemCategory(id: Int, category: ItemCategory): Result<ItemCategory>
    suspend fun deleteItemCategory(id: Int): Result<Unit>
}