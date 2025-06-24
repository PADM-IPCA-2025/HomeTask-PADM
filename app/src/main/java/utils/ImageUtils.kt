package pt.ipca.hometask.utils

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.UUID

object ImageUtils {
    
    /**
     * Copia uma imagem de um content URI para o armazenamento interno da app
     * e retorna um URI permanente
     */
    fun copyImageToInternalStorage(context: Context, contentUri: String?): String? {
        if (contentUri.isNullOrEmpty()) {
            Log.w("ImageUtils", "Content URI is null or empty")
            return null
        }
        
        return try {
            Log.d("ImageUtils", "🔄 Starting image copy process for: $contentUri")
            val uri = Uri.parse(contentUri)
            Log.d("ImageUtils", "📋 Parsed URI: $uri")
            
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            
            if (inputStream == null) {
                Log.e("ImageUtils", "❌ Não foi possível abrir o input stream para: $contentUri")
                return null
            }
            
            Log.d("ImageUtils", "✅ Input stream opened successfully")
            
            // Criar diretório para imagens se não existir
            val imagesDir = File(context.filesDir, "images")
            if (!imagesDir.exists()) {
                val created = imagesDir.mkdirs()
                Log.d("ImageUtils", "📁 Images directory created: $created")
            } else {
                Log.d("ImageUtils", "📁 Images directory already exists")
            }
            
            // Gerar nome único para o arquivo
            val fileName = "task_image_${UUID.randomUUID()}.jpg"
            val imageFile = File(imagesDir, fileName)
            Log.d("ImageUtils", "📄 Target file: ${imageFile.absolutePath}")
            
            // Copiar o arquivo
            val outputStream = FileOutputStream(imageFile)
            val bytesCopied = inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()
            
            Log.d("ImageUtils", "📊 Bytes copied: $bytesCopied")
            
            // Verificar se o arquivo foi criado
            if (imageFile.exists()) {
                val fileSize = imageFile.length()
                Log.d("ImageUtils", "✅ File created successfully, size: $fileSize bytes")
            } else {
                Log.e("ImageUtils", "❌ File was not created")
                return null
            }
            
            // Retornar URI permanente
            val permanentUri = "file://${imageFile.absolutePath}"
            Log.d("ImageUtils", "🎉 Imagem copiada com sucesso para: $permanentUri")
            permanentUri
            
        } catch (e: Exception) {
            Log.e("ImageUtils", "💥 Erro ao copiar imagem: ${e.message}", e)
            null
        }
    }
    
    /**
     * Verifica se um URI é um content URI temporário
     */
    fun isContentUri(uri: String?): Boolean {
        return uri?.startsWith("content://") == true
    }
    
    /**
     * Verifica se um URI é um file URI permanente
     */
    fun isFileUri(uri: String?): Boolean {
        return uri?.startsWith("file://") == true
    }
    
    /**
     * Limpa imagens antigas do armazenamento interno
     */
    fun cleanupOldImages(context: Context) {
        try {
            val imagesDir = File(context.filesDir, "images")
            if (imagesDir.exists()) {
                val files = imagesDir.listFiles()
                files?.forEach { file ->
                    // Remover arquivos mais antigos que 7 dias
                    val lastModified = file.lastModified()
                    val currentTime = System.currentTimeMillis()
                    val sevenDaysInMillis = 7 * 24 * 60 * 60 * 1000L
                    
                    if (currentTime - lastModified > sevenDaysInMillis) {
                        file.delete()
                        Log.d("ImageUtils", "Arquivo antigo removido: ${file.name}")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("ImageUtils", "Erro ao limpar imagens antigas: ${e.message}")
        }
    }
} 