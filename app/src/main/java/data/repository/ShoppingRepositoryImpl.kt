package pt.ipca.hometask.data.repository

import android.content.Context
import pt.ipca.hometask.data.remote.api.ShoppingApi
import pt.ipca.hometask.data.remote.model.*
import pt.ipca.hometask.data.remote.model.ShoppingListDto
import pt.ipca.hometask.domain.model.*
import pt.ipca.hometask.domain.repository.ShoppingRepository
import pt.ipca.hometask.network.RetrofitClient

class ShoppingRepositoryImpl(private val context: Context) : ShoppingRepository {
    private val api: ShoppingApi = RetrofitClient.getShoppingApi(context)

    // Mappers
    private fun ShoppingListDto.toDomain(): ShoppingList {
        return ShoppingList(
            id = id,
            title = title ?: "Untitled List",
            startDate = startDate,
            endDate = endDate,
            homeId = homeId,
            shoppingItems = shoppingItems?.map { it.toDomain() },
            total = total
        )
    }

    private fun ShoppingList.toDto(): ShoppingListDto {
        return ShoppingListDto(
            id = id,
            title = title ?: "Untitled List",
            startDate = startDate,
            endDate = endDate,
            homeId = homeId,
            shoppingItems = shoppingItems?.map { it.toDto() },
            total = total
        )
    }

    private fun ShoppingItemDto.toDomain(): ShoppingItem {
        android.util.Log.d("ShoppingRepositoryImpl", "Converting DTO to domain: $this")
        android.util.Log.d("ShoppingRepositoryImpl", "DTO description: '$description'")
        android.util.Log.d("ShoppingRepositoryImpl", "DTO quantity: $quantity")
        android.util.Log.d("ShoppingRepositoryImpl", "DTO state: '$state'")
        android.util.Log.d("ShoppingRepositoryImpl", "DTO price: '$price' (type: ${price?.javaClass?.simpleName})")
        android.util.Log.d("ShoppingRepositoryImpl", "DTO shoppingListId: $shoppingListId")
        android.util.Log.d("ShoppingRepositoryImpl", "DTO itemCategoryId: $itemCategoryId")
        
        val convertedPrice = price?.toFloatOrNull() ?: 0f
        android.util.Log.d("ShoppingRepositoryImpl", "Converted price: $convertedPrice")
        
        return ShoppingItem(
            id = id,
            description = description,
            quantity = quantity.toFloat(),
            state = state,
            price = convertedPrice,
            shoppingListId = shoppingListId,
            itemCategoryId = itemCategoryId
        )
    }

    private fun ShoppingItem.toDto(): ShoppingItemDto {
        return ShoppingItemDto(
            id = id,
            description = description,
            quantity = quantity.toInt(),
            state = state,
            price = price.toString(),
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
        android.util.Log.d("ShoppingRepositoryImpl", "=== START createShoppingList ===")
        android.util.Log.d("ShoppingRepositoryImpl", "Creating shopping list: $shoppingList")
        
        return try {
            val dto = shoppingList.toDto()
            android.util.Log.d("ShoppingRepositoryImpl", "DTO to send: $dto")
            android.util.Log.d("ShoppingRepositoryImpl", "DTO title: ${dto.title}")
            android.util.Log.d("ShoppingRepositoryImpl", "DTO homeId: ${dto.homeId}")
            
            val response = api.createShoppingList(dto)
            android.util.Log.d("ShoppingRepositoryImpl", "API response received")
            android.util.Log.d("ShoppingRepositoryImpl", "Response successful: ${response.isSuccessful}")
            android.util.Log.d("ShoppingRepositoryImpl", "Response code: ${response.code()}")
            android.util.Log.d("ShoppingRepositoryImpl", "Response message: ${response.message()}")
            android.util.Log.d("ShoppingRepositoryImpl", "Response body is null: ${response.body() == null}")
            
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                android.util.Log.d("ShoppingRepositoryImpl", "ApiResponse: $apiResponse")
                android.util.Log.d("ShoppingRepositoryImpl", "ApiResponse.data: ${apiResponse.data}")
                android.util.Log.d("ShoppingRepositoryImpl", "ApiResponse.data.title: ${apiResponse.data.title}")
                android.util.Log.d("ShoppingRepositoryImpl", "ApiResponse.data.homeId: ${apiResponse.data.homeId}")
                android.util.Log.d("ShoppingRepositoryImpl", "ApiResponse.data.id: ${apiResponse.data.id}")
                
                if (apiResponse.success) {
                    val createdList = apiResponse.data.toDomain()
                    android.util.Log.d("ShoppingRepositoryImpl", "List created successfully: $createdList")
                    android.util.Log.d("ShoppingRepositoryImpl", "=== END createShoppingList SUCCESS ===")
                    Result.success(createdList)
                } else {
                    android.util.Log.e("ShoppingRepositoryImpl", "ApiResponse not successful: ${apiResponse.message}")
                    android.util.Log.d("ShoppingRepositoryImpl", "=== END createShoppingList ERROR ===")
                    Result.failure(Exception(apiResponse.message ?: "Failed to create shopping list"))
                }
            } else {
                android.util.Log.e("ShoppingRepositoryImpl", "API call failed: ${response.code()}")
                android.util.Log.d("ShoppingRepositoryImpl", "=== END createShoppingList ERROR ===")
                Result.failure(Exception("Create shopping list failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            android.util.Log.e("ShoppingRepositoryImpl", "Exception creating shopping list", e)
            android.util.Log.d("ShoppingRepositoryImpl", "=== END createShoppingList EXCEPTION ===")
            Result.failure(e)
        }
    }

    override suspend fun getShoppingListById(id: Int): Result<ShoppingList> {
        android.util.Log.d("ShoppingRepositoryImpl", "--- getShoppingListById START for id: $id ---")
        return try {
            val response = api.getShoppingListById(id)
            android.util.Log.d("ShoppingRepositoryImpl", "API response for getShoppingListById: code=${response.code()}, successful=${response.isSuccessful}")

            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                android.util.Log.d("ShoppingRepositoryImpl", "API response body: $apiResponse")

                if (apiResponse.success) {
                    val shoppingList = apiResponse.data
                    android.util.Log.i("ShoppingRepositoryImpl", "Found shopping list: $shoppingList")
                    Result.success(shoppingList.toDomain())
                } else {
                    android.util.Log.w("ShoppingRepositoryImpl", "API response was not successful: ${apiResponse.message}")
                    Result.failure(Exception(apiResponse.message ?: "Failed to get shopping list"))
                }
            } else {
                android.util.Log.e("ShoppingRepositoryImpl", "Get shopping list failed: code=${response.code()}, message=${response.message()}")
                Result.failure(Exception("Get shopping list failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            android.util.Log.e("ShoppingRepositoryImpl", "Exception in getShoppingListById", e)
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
                        
                        val shoppingListDtos = apiResponse.data
                        android.util.Log.d("ShoppingRepositoryImpl", "ShoppingListDtos count: ${shoppingListDtos.size}")
                        android.util.Log.d("ShoppingRepositoryImpl", "ShoppingListDtos: $shoppingListDtos")
                        
                        val shoppingLists = shoppingListDtos.map { it.toDomain() }
                        android.util.Log.d("ShoppingRepositoryImpl", "Converted to domain count: ${shoppingLists.size}")
                        android.util.Log.d("ShoppingRepositoryImpl", "Converted to domain: $shoppingLists")
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
            val shoppingListDto = shoppingList.toDto().copy(
                id = null, // Remove ID from body for update
                shoppingItems = null, // Remove shopping items from update
                total = null // Remove total from update
            )
            val response = api.updateShoppingList(id, shoppingListDto)
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
        android.util.Log.d("ShoppingRepositoryImpl", "=== START createShoppingItem ===")
        android.util.Log.d("ShoppingRepositoryImpl", "Creating shopping item: $item")
        android.util.Log.d("ShoppingRepositoryImpl", "Item description: ${item.description}")
        android.util.Log.d("ShoppingRepositoryImpl", "Item quantity: ${item.quantity}")
        android.util.Log.d("ShoppingRepositoryImpl", "Item state: ${item.state}")
        android.util.Log.d("ShoppingRepositoryImpl", "Item price: ${item.price}")
        android.util.Log.d("ShoppingRepositoryImpl", "Item shoppingListId: ${item.shoppingListId}")
        android.util.Log.d("ShoppingRepositoryImpl", "Item itemCategoryId: ${item.itemCategoryId}")
        
        // Validações adicionais
        if (item.description.isNullOrBlank()) {
            android.util.Log.e("ShoppingRepositoryImpl", "Description is blank or null")
            return Result.failure(Exception("Description cannot be blank"))
        }
        
        if (item.quantity <= 0) {
            android.util.Log.e("ShoppingRepositoryImpl", "Invalid quantity: ${item.quantity}")
            return Result.failure(Exception("Quantity must be greater than 0"))
        }
        
        if (item.price < 0) {
            android.util.Log.e("ShoppingRepositoryImpl", "Invalid price: ${item.price}")
            return Result.failure(Exception("Price cannot be negative"))
        }
        
        if (item.shoppingListId <= 0) {
            android.util.Log.e("ShoppingRepositoryImpl", "Invalid shoppingListId: ${item.shoppingListId}")
            return Result.failure(Exception("ShoppingListId must be greater than 0"))
        }
        
        if (item.itemCategoryId <= 0) {
            android.util.Log.e("ShoppingRepositoryImpl", "Invalid itemCategoryId: ${item.itemCategoryId}")
            return Result.failure(Exception("ItemCategoryId must be greater than 0"))
        }
        
        return try {
            val itemDto = item.toDto()
            android.util.Log.d("ShoppingRepositoryImpl", "Converted to DTO: $itemDto")
            android.util.Log.d("ShoppingRepositoryImpl", "DTO description: ${itemDto.description}")
            android.util.Log.d("ShoppingRepositoryImpl", "DTO quantity: ${itemDto.quantity}")
            android.util.Log.d("ShoppingRepositoryImpl", "DTO state: ${itemDto.state}")
            android.util.Log.d("ShoppingRepositoryImpl", "DTO price: ${itemDto.price}")
            android.util.Log.d("ShoppingRepositoryImpl", "DTO shoppingListId: ${itemDto.shoppingListId}")
            android.util.Log.d("ShoppingRepositoryImpl", "DTO itemCategoryId: ${itemDto.itemCategoryId}")
            
            val response = api.createShoppingItem(itemDto)
            android.util.Log.d("ShoppingRepositoryImpl", "API response received")
            android.util.Log.d("ShoppingRepositoryImpl", "Response successful: ${response.isSuccessful}")
            android.util.Log.d("ShoppingRepositoryImpl", "Response code: ${response.code()}")
            android.util.Log.d("ShoppingRepositoryImpl", "Response message: ${response.message()}")
            android.util.Log.d("ShoppingRepositoryImpl", "Response body is null: ${response.body() == null}")
            
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                android.util.Log.d("ShoppingRepositoryImpl", "ApiResponse: $apiResponse")
                android.util.Log.d("ShoppingRepositoryImpl", "ApiResponse.success: ${apiResponse.success}")
                android.util.Log.d("ShoppingRepositoryImpl", "ApiResponse.message: ${apiResponse.message}")
                android.util.Log.d("ShoppingRepositoryImpl", "ApiResponse.data: ${apiResponse.data}")
                
                if (apiResponse.success) {
                    // Verificar se a resposta da API tem dados válidos
                    val responseData = apiResponse.data
                    android.util.Log.d("ShoppingRepositoryImpl", "Response data: $responseData")
                    
                    // Se a API retornou dados válidos, usar eles
                    if (responseData.description != null && responseData.id != null) {
                        val createdItem = responseData.toDomain()
                        android.util.Log.d("ShoppingRepositoryImpl", "Item created successfully with API data: $createdItem")
                        android.util.Log.d("ShoppingRepositoryImpl", "=== END createShoppingItem SUCCESS ===")
                        Result.success(createdItem)
                    } else {
                        // Se a API retornou sucesso mas sem dados válidos, usar os dados originais
                        android.util.Log.w("ShoppingRepositoryImpl", "API returned success but with empty data, using original item")
                        val originalItemWithId = item.copy(id = responseData.id ?: 0)
                        android.util.Log.d("ShoppingRepositoryImpl", "=== END createShoppingItem SUCCESS (using original data) ===")
                        Result.success(originalItemWithId)
                    }
                } else {
                    android.util.Log.e("ShoppingRepositoryImpl", "ApiResponse not successful: ${apiResponse.message}")
                    android.util.Log.d("ShoppingRepositoryImpl", "=== END createShoppingItem ERROR ===")
                    Result.failure(Exception(apiResponse.message ?: "Failed to create shopping item"))
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: "No error body"
                android.util.Log.e("ShoppingRepositoryImpl", "API call failed. Error body: $errorBody")
                android.util.Log.d("ShoppingRepositoryImpl", "=== END createShoppingItem ERROR ===")
                Result.failure(Exception("Create shopping item failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            android.util.Log.e("ShoppingRepositoryImpl", "Exception in createShoppingItem", e)
            android.util.Log.d("ShoppingRepositoryImpl", "=== END createShoppingItem EXCEPTION ===")
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
        android.util.Log.d("ShoppingRepositoryImpl", "--- Preparing to update item ID: $id ---")
        android.util.Log.d("ShoppingRepositoryImpl", "Domain object received: $item")
        return try {
            val itemDto = item.toDto().copy(id = null)
            android.util.Log.d("ShoppingRepositoryImpl", "Converted to DTO for sending (ID removed from body): $itemDto")
            val response = api.updateShoppingItem(id, itemDto)

            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success) {
                    android.util.Log.i("ShoppingRepositoryImpl", "Update successful for item $id. Server data: ${apiResponse.data}")
                    Result.success(apiResponse.data.toDomain())
                } else {
                     android.util.Log.e("ShoppingRepositoryImpl", "API returned success=false. Message: ${apiResponse.message}")
                    Result.failure(Exception(apiResponse.message ?: "Failed to update shopping item"))
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: "No error body"
                android.util.Log.e("ShoppingRepositoryImpl", "Update API call failed. Code: ${response.code()}, Message: ${response.message()}, Error Body: $errorBody")
                Result.failure(Exception("Update shopping item failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            android.util.Log.e("ShoppingRepositoryImpl", "Exception during item update for ID $id", e)
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