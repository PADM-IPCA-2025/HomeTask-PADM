package pt.ipca.hometask.data.remote.api

import pt.ipca.hometask.data.remote.model.*
import retrofit2.Response
import retrofit2.http.*

interface ShoppingApi {

    // Shopping List endpoints
    @POST("shopping/shopping-lists")
    suspend fun createShoppingList(@Body shoppingList: ShoppingListDto): Response<ApiResponse<ShoppingListDto>>

    @GET("shopping/shopping-lists/{id}")
    suspend fun getShoppingListById(@Path("id") id: Int): Response<ApiResponse<ShoppingListDto>>

    @GET("shopping/shopping-lists/home/{homeId}")
    suspend fun getShoppingListsByHome(@Path("homeId") homeId: Int): Response<ApiResponse<List<ShoppingListDto>>>

    @PUT("shopping/shopping-lists/{id}")
    suspend fun updateShoppingList(@Path("id") id: Int, @Body shoppingList: ShoppingListDto): Response<ApiResponse<ShoppingListDto>>

    @DELETE("shopping/shopping-lists/{id}")
    suspend fun deleteShoppingList(@Path("id") id: Int): Response<ApiResponse<Unit>>

    // Shopping Item endpoints
    @POST("shopping/shopping-items")
    suspend fun createShoppingItem(@Body item: ShoppingItemDto): Response<ApiResponse<ShoppingItemDto>>

    @GET("shopping/shopping-items/{id}")
    suspend fun getShoppingItemById(@Path("id") id: Int): Response<ApiResponse<ShoppingItemDto>>

    @PUT("shopping/shopping-items/{id}")
    suspend fun updateShoppingItem(@Path("id") id: Int, @Body item: ShoppingItemDto): Response<ApiResponse<ShoppingItemDto>>

    @DELETE("shopping/shopping-items/{id}")
    suspend fun deleteShoppingItem(@Path("id") id: Int): Response<ApiResponse<Unit>>

    // Item Category endpoints
    @POST("shopping/item-categories")
    suspend fun createItemCategory(@Body category: ItemCategoryDto): Response<ApiResponse<ItemCategoryDto>>

    // Endpoint para buscar todas as categorias de itens
    // Espera retornar: ApiResponse<List<ItemCategoryDto>>
    @GET("shopping/item-categories")
    suspend fun getAllItemCategories(): Response<ApiResponse<List<ItemCategoryDto>>>

    @GET("shopping/item-categories/{id}")
    suspend fun getItemCategoryById(@Path("id") id: Int): Response<ApiResponse<ItemCategoryDto>>

    @PUT("shopping/item-categories/{id}")
    suspend fun updateItemCategory(@Path("id") id: Int, @Body category: ItemCategoryDto): Response<ApiResponse<ItemCategoryDto>>

    @DELETE("shopping/item-categories/{id}")
    suspend fun deleteItemCategory(@Path("id") id: Int): Response<ApiResponse<Unit>>

    @GET("shopping/shopping-items/list/{shoppingListId}")
    suspend fun getItemsByShoppingList(@Path("shoppingListId") shoppingListId: Int): Response<ApiResponse<List<ShoppingItemDto>>>

}