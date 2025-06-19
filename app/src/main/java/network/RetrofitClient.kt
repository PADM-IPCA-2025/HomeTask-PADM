package pt.ipca.hometask.network

import android.content.Context
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import pt.ipca.hometask.data.local.AuthPreferences
import pt.ipca.hometask.data.remote.api.HomeTaskApi
import pt.ipca.hometask.data.remote.api.ShoppingApi
import pt.ipca.hometask.data.remote.api.UserAuthApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:2000/"

    // Interceptor para adicionar token de autenticação
    private fun createAuthInterceptor(context: Context): Interceptor {
        return Interceptor { chain ->
            val original = chain.request()
            val authPreferences = AuthPreferences(context)
            val token = authPreferences.getUser()?.token

            val requestBuilder = original.newBuilder()
            if (token != null && token.isNotEmpty()) {
                requestBuilder.addHeader("Authorization", "Bearer $token")
            }
            
            val request = requestBuilder.build()
            chain.proceed(request)
        }
    }

    private fun createOkHttpClient(context: Context): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(createAuthInterceptor(context))
            .build()
    }

    private fun createRetrofit(context: Context): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(createOkHttpClient(context))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun getUserAuthApi(context: Context): UserAuthApi {
        return createRetrofit(context).create(UserAuthApi::class.java)
    }

    fun getHomeTaskApi(context: Context): HomeTaskApi {
        return createRetrofit(context).create(HomeTaskApi::class.java)
    }

    fun getShoppingApi(context: Context): ShoppingApi {
        return createRetrofit(context).create(ShoppingApi::class.java)
    }

    // Mantém compatibilidade com código existente
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