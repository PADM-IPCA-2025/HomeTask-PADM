package pt.ipca.hometask.data.repository

import pt.ipca.hometask.data.remote.api.ShoppingApi
import pt.ipca.hometask.data.remote.model.*
import pt.ipca.hometask.domain.model.*
import pt.ipca.hometask.domain.repository.ShoppingRepository
import pt.ipca.hometask.data.remote.network.RetrofitClient

class ShoppingRepositoryImpl : ShoppingRepository {
    private val api: ShoppingApi = RetrofitClient.shoppingApi

    // Mappers
    private fun ShoppingListDto.toDomain(): ShoppingList {
        return ShoppingList(
            id = id,
            title = title,
            startDate = startDate,
            endDate = endDate,
            homeId = homeId
        )
    }

    private fun ShoppingList.toDto(): ShoppingListDto {
        return ShoppingListDto(
            id = id,
            title = title,
            startDate = startDate,
            endDate = endDate,
            homeId = homeId
        )
    }

    private fun ShoppingItemDto.toDomain(): ShoppingItem {
        return ShoppingItem(
            id = id,
            description = description,
            quantity = quantity,
            state = state,
            price = price,
            shoppingListId = shoppingListId,
            itemCategoryId = itemCategoryId
        )
    }

    private fun ShoppingItem.toDto(): ShoppingItemDto {
        return ShoppingItemDto(
            id = id,
            description = description,
            quantity = quantity,
            state = state,
            price = price,
            shoppingListId = shoppingListId,
            itemCategoryId = itemCategoryId
        )
    }

    private fun ItemCategoryDto.toDomain(): ItemCategory {
        return ItemCategory(
            id = id,
            description = description
        )
    }

    private fun ItemCategory.toDto(): ItemCategoryDto {
        return ItemCategoryDto(
            id = id,
            description = description
        )
    }

    // Shopping Lists
    override suspend fun createShoppingList(shoppingList: ShoppingList): Result<ShoppingList> {
        return try {
            val response = api.createShoppingList(shoppingList.toDto())
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toDomain())
            } else {
                Result.failure(Exception("Create shopping list failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getShoppingListById(id: Int): Result<ShoppingList> {
        return try {
            val response = api.getShoppingListById(id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toDomain())
            } else {
                Result.failure(Exception("Get shopping list failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getShoppingListsByHome(homeId: Int): Result<List<ShoppingList>> {
        return try {
            val response = api.getShoppingListsByHome(homeId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.map { it.toDomain() })
            } else {
                Result.failure(Exception("Get shopping lists failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateShoppingList(id: Int, shoppingList: ShoppingList): Result<ShoppingList> {
        return try {
            val response = api.updateShoppingList(id, shoppingList.toDto())
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toDomain())
            } else {
                Result.failure(Exception("Update shopping list failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteShoppingList(id: Int): Result<Unit> {
        return try {
            val response = api.deleteShoppingList(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Delete shopping list failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Shopping Items
    override suspend fun createShoppingItem(item: ShoppingItem): Result<ShoppingItem> {
        return try {
            val response = api.createShoppingItem(item.toDto())
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toDomain())
            } else {
                Result.failure(Exception("Create shopping item failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getShoppingItemById(id: Int): Result<ShoppingItem> {
        return try {
            val response = api.getShoppingItemById(id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toDomain())
            } else {
                Result.failure(Exception("Get shopping item failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateShoppingItem(id: Int, item: ShoppingItem): Result<ShoppingItem> {
        return try {
            val response = api.updateShoppingItem(id, item.toDto())
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toDomain())
            } else {
                Result.failure(Exception("Update shopping item failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteShoppingItem(id: Int): Result<Unit> {
        return try {
            val response = api.deleteShoppingItem(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Delete shopping item failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Item Categories
    override suspend fun createItemCategory(category: ItemCategory): Result<ItemCategory> {
        return try {
            val response = api.createItemCategory(category.toDto())
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toDomain())
            } else {
                Result.failure(Exception("Create item category failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAllItemCategories(): Result<List<ItemCategory>> {
        return try {
            val response = api.getAllItemCategories()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.map { it.toDomain() })
            } else {
                Result.failure(Exception("Get item categories failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getItemCategoryById(id: Int): Result<ItemCategory> {
        return try {
            val response = api.getItemCategoryById(id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toDomain())
            } else {
                Result.failure(Exception("Get item category failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateItemCategory(id: Int, category: ItemCategory): Result<ItemCategory> {
        return try {
            val response = api.updateItemCategory(id, category.toDto())
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toDomain())
            } else {
                Result.failure(Exception("Update item category failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteItemCategory(id: Int): Result<Unit> {
        return try {
            val response = api.deleteItemCategory(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Delete item category failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}