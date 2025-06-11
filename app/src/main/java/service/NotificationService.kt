package pt.ipca.hometask.service

import android.content.Context
import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await
import pt.ipca.hometask.data.remote.api.HomeTaskApi
import pt.ipca.hometask.network.RetrofitClient

class NotificationService(private val context: Context) {
    private val api: HomeTaskApi = RetrofitClient.homeTaskApi

    suspend fun sendHouseCreatedNotification(houseName: String, userId: Int) {
        try {
            // Obter o token FCM do usuário
            val token = getFCMToken()
            if (token == null) {
                Log.e("NotificationService", "FCM token não encontrado")
                return
            }

            // Enviar notificação via API
            val response = api.sendNotification(
                mapOf(
                    "token" to token,
                    "title" to "Nova Casa Criada",
                    "body" to "A casa '$houseName' foi criada com sucesso!",
                    "type" to "HOUSE_CREATED",
                    "data" to mapOf(
                        "houseName" to houseName,
                        "userId" to userId
                    )
                )
            )

            if (response.isSuccessful) {
                Log.d("NotificationService", "Notificação enviada com sucesso")
            } else {
                Log.e("NotificationService", "Erro ao enviar notificação: ${response.message()}")
            }
        } catch (e: Exception) {
            Log.e("NotificationService", "Erro ao enviar notificação", e)
        }
    }

    private suspend fun getFCMToken(): String? {
        return try {
            FirebaseMessaging.getInstance().token.await()
        } catch (e: Exception) {
            Log.e("NotificationService", "Erro ao obter FCM token", e)
            null
        }
    }
} 