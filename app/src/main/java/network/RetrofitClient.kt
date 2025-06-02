package pt.ipca.hometask.network

import pt.ipca.hometask.data.remote.api.HomeTaskApi
import pt.ipca.hometask.data.remote.api.ShoppingApi
import pt.ipca.hometask.data.remote.api.UserAuthApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:2000/"

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val userAuthApi: UserAuthApi by lazy {
        retrofit.create(UserAuthApi::class.java)
    }

    val homeTaskApi: HomeTaskApi by lazy {
        retrofit.create(HomeTaskApi::class.java)
    }

    val shoppingApi: ShoppingApi by lazy {
        retrofit.create(ShoppingApi::class.java)
    }
}
