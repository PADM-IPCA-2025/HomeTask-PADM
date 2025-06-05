package pt.ipca.hometask.data.remote.api

import pt.ipca.hometask.data.remote.model.*
import retrofit2.Response
import retrofit2.http.*

interface ShoppingApi {

    // Shopping List endpoints
    @POST("api/shopping/shopping-lists")
    suspend fun createShoppingList(@Body shoppingList: ShoppingListDto): Response<ShoppingListDto>

    @GET("api/shopping/shopping-lists/{id}")
    suspend fun getShoppingListById(@Path("id") id: Int): Response<ShoppingListDto>

    @GET("api/shopping/shopping-lists/home/{homeId}")
    suspend fun getShoppingListsByHome(@Path("homeId") homeId: Int): Response<List<ShoppingListDto>>

    @PUT("api/shopping/shopping-lists/{id}")
    suspend fun updateShoppingList(@Path("id") id: Int, @Body shoppingList: ShoppingListDto): Response<ShoppingListDto>

    @DELETE("api/shopping/shopping-lists/{id}")
    suspend fun deleteShoppingList(@Path("id") id: Int): Response<Unit>

    // Shopping Item endpoints
    @POST("api/shopping/shopping-items")
    suspend fun createShoppingItem(@Body item: ShoppingItemDto): Response<ShoppingItemDto>

    @GET("api/shopping/shopping-items/{id}")
    suspend fun getShoppingItemById(@Path("id") id: Int): Response<ShoppingItemDto>

    @PUT("api/shopping/shopping-items/{id}")
    suspend fun updateShoppingItem(@Path("id") id: Int, @Body item: ShoppingItemDto): Response<ShoppingItemDto>

    @DELETE("api/shopping/shopping-items/{id}")
    suspend fun deleteShoppingItem(@Path("id") id: Int): Response<Unit>

    // Item Category endpoints
    @POST("api/shopping/item-categories")
    suspend fun createItemCategory(@Body category: ItemCategoryDto): Response<ItemCategoryDto>

    @GET("api/shopping/item-categories")
    suspend fun getAllItemCategories(): Response<List<ItemCategoryDto>>

    @GET("api/shopping/item-categories/{id}")
    suspend fun getItemCategoryById(@Path("id") id: Int): Response<ItemCategoryDto>

    @PUT("api/shopping/item-categories/{id}")
    suspend fun updateItemCategory(@Path("id") id: Int, @Body category: ItemCategoryDto): Response<ItemCategoryDto>

    @DELETE("api/shopping/item-categories/{id}")
    suspend fun deleteItemCategory(@Path("id") id: Int): Response<Unit>

    @GET("api/shopping/shopping-items/list/{shoppingListId}")
    suspend fun getItemsByShoppingList(@Path("shoppingListId") shoppingListId: Int): Response<List<ShoppingItemDto>>

}