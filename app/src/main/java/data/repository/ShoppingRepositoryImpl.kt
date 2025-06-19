package pt.ipca.hometask.data.repository

import pt.ipca.hometask.data.remote.api.ShoppingApi
import pt.ipca.hometask.data.remote.model.*
import pt.ipca.hometask.data.remote.model.ShoppingListDto
import pt.ipca.hometask.domain.model.*
import pt.ipca.hometask.domain.repository.ShoppingRepository
import pt.ipca.hometask.network.RetrofitClient

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
                val apiResponse = response.body()!!
                if (apiResponse.success) {
                    Result.success(apiResponse.data.toDomain())
                } else {
                    Result.failure(Exception(apiResponse.message ?: "Failed to create shopping list"))
                }
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
                val apiResponse = response.body()!!
                if (apiResponse.success) {
                    Result.success(apiResponse.data.toDomain())
                } else {
                    Result.failure(Exception(apiResponse.message ?: "Failed to get shopping list"))
                }
            } else {
                Result.failure(Exception("Get shopping list failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getShoppingListsByHome(homeId: Int): Result<List<ShoppingList>> {
        return try {
            android.util.Log.d("ShoppingRepositoryImpl", "=== START getShoppingListsByHome ===")
            android.util.Log.d("ShoppingRepositoryImpl", "Getting shopping lists for home $homeId")
            
            val response = api.getShoppingListsByHome(homeId)
            android.util.Log.d("ShoppingRepositoryImpl", "API response received")
            android.util.Log.d("ShoppingRepositoryImpl", "Response successful: ${response.isSuccessful}")
            android.util.Log.d("ShoppingRepositoryImpl", "Response code: ${response.code()}")
            android.util.Log.d("ShoppingRepositoryImpl", "Response message: ${response.message()}")
            android.util.Log.d("ShoppingRepositoryImpl", "Response body is null: ${response.body() == null}")
            
            if (response.isSuccessful) {
                android.util.Log.d("ShoppingRepositoryImpl", "Response is successful, processing body...")
                
                if (response.body() != null) {
                    val apiResponse = response.body()!!
                    android.util.Log.d("ShoppingRepositoryImpl", "ApiResponse object created")
                    android.util.Log.d("ShoppingRepositoryImpl", "ApiResponse success: ${apiResponse.success}")
                    android.util.Log.d("ShoppingRepositoryImpl", "ApiResponse message: ${apiResponse.message}")
                    
                    if (apiResponse.success) {
                        android.util.Log.d("ShoppingRepositoryImpl", "ApiResponse is successful, processing data...")
                        
                        val shoppingListDto = apiResponse.data
                        android.util.Log.d("ShoppingRepositoryImpl", "ShoppingListDto: $shoppingListDto")
                        
                        val shoppingList = shoppingListDto.toDomain()
                        android.util.Log.d("ShoppingRepositoryImpl", "Converted to domain: $shoppingList")
                        
                        val shoppingLists = listOf(shoppingList)
                        android.util.Log.d("ShoppingRepositoryImpl", "Final shopping lists count: ${shoppingLists.size}")
                        android.util.Log.d("ShoppingRepositoryImpl", "Final shopping lists: $shoppingLists")
                        android.util.Log.d("ShoppingRepositoryImpl", "=== END getShoppingListsByHome SUCCESS ===")
                        Result.success(shoppingLists)
                    } else {
                        android.util.Log.e("ShoppingRepositoryImpl", "ApiResponse is not successful: ${apiResponse.message}")
                        android.util.Log.d("ShoppingRepositoryImpl", "=== END getShoppingListsByHome ERROR ===")
                        Result.failure(Exception(apiResponse.message ?: "Failed to get shopping lists"))
                    }
                } else {
                    android.util.Log.e("ShoppingRepositoryImpl", "Response body is null")
                    android.util.Log.d("ShoppingRepositoryImpl", "=== END getShoppingListsByHome ERROR ===")
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                android.util.Log.e("ShoppingRepositoryImpl", "API call failed: ${response.code()}")
                android.util.Log.d("ShoppingRepositoryImpl", "=== END getShoppingListsByHome ERROR ===")
                Result.failure(Exception("Get shopping lists failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            android.util.Log.e("ShoppingRepositoryImpl", "Exception getting shopping lists", e)
            android.util.Log.d("ShoppingRepositoryImpl", "=== END getShoppingListsByHome EXCEPTION ===")
            Result.failure(e)
        }
    }

    override suspend fun updateShoppingList(id: Int, shoppingList: ShoppingList): Result<ShoppingList> {
        return try {
            val response = api.updateShoppingList(id, shoppingList.toDto())
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success) {
                    Result.success(apiResponse.data.toDomain())
                } else {
                    Result.failure(Exception(apiResponse.message ?: "Failed to update shopping list"))
                }
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
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception(apiResponse.message ?: "Failed to delete shopping list"))
                }
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
                val apiResponse = response.body()!!
                if (apiResponse.success) {
                    Result.success(apiResponse.data.toDomain())
                } else {
                    Result.failure(Exception(apiResponse.message ?: "Failed to create shopping item"))
                }
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
                val apiResponse = response.body()!!
                if (apiResponse.success) {
                    Result.success(apiResponse.data.toDomain())
                } else {
                    Result.failure(Exception(apiResponse.message ?: "Failed to get shopping item"))
                }
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
                val apiResponse = response.body()!!
                if (apiResponse.success) {
                    Result.success(apiResponse.data.toDomain())
                } else {
                    Result.failure(Exception(apiResponse.message ?: "Failed to update shopping item"))
                }
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
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception(apiResponse.message ?: "Failed to delete shopping item"))
                }
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
                val apiResponse = response.body()!!
                if (apiResponse.success) {
                    Result.success(apiResponse.data.toDomain())
                } else {
                    Result.failure(Exception(apiResponse.message ?: "Failed to create item category"))
                }
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
                val apiResponse = response.body()!!
                if (apiResponse.success) {
                    val categories = apiResponse.data.map { it.toDomain() }
                    Result.success(categories)
                } else {
                    Result.failure(Exception(apiResponse.message ?: "Failed to get item categories"))
                }
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
                val apiResponse = response.body()!!
                if (apiResponse.success) {
                    Result.success(apiResponse.data.toDomain())
                } else {
                    Result.failure(Exception(apiResponse.message ?: "Failed to get item category"))
                }
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
                val apiResponse = response.body()!!
                if (apiResponse.success) {
                    Result.success(apiResponse.data.toDomain())
                } else {
                    Result.failure(Exception(apiResponse.message ?: "Failed to update item category"))
                }
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
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception(apiResponse.message ?: "Failed to delete item category"))
                }
            } else {
                Result.failure(Exception("Delete item category failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getItemsByShoppingList(shoppingListId: Int): Result<List<ShoppingItem>> {
        return try {
            val response = api.getItemsByShoppingList(shoppingListId)
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success) {
                    val items = apiResponse.data.map { it.toDomain() }
                    Result.success(items)
                } else {
                    Result.failure(Exception(apiResponse.message ?: "Failed to get shopping items by list"))
                }
            } else {
                Result.failure(Exception("Get shopping items by list failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}