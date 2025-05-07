package es.uc3m.android.samplex.utils

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 * Cliente para realizar llamadas a Cloud Functions de Firebase
 */
object CloudFunctionClient {
    
    private val TAG = "CloudFunctionClient"
    
    // Cliente HTTP compartido con configuración común
    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    
    /**
     * Clase para representar la evaluación de una respuesta
     */
    data class AnswerEvaluation(
        val grade: Double,
        val correction: String
    )
    
    /**
     * Evalúa una respuesta a una pregunta a través de la Cloud Function
     * 
     * @param question La pregunta que se ha hecho
     * @param answer La respuesta proporcionada por el usuario
     * @return Resultado con la evaluación o un error
     */
    suspend fun evaluateAnswer(question: String, answer: String): Result<AnswerEvaluation> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Llamando al servicio de evaluación de respuestas")
                
                // Crear JSON para la petición con los nombres de campo correctos
                val jsonObject = JSONObject()
                jsonObject.put("pregunta", question)
                jsonObject.put("respuesta", answer)
                
                val requestBody = jsonObject.toString()
                    .toRequestBody("application/json; charset=utf-8".toMediaType())
                
                // Crear la petición HTTP
                val request = Request.Builder()
                    .url("https://evaluate-answer-zfxlj2bydq-uc.a.run.app")
                    .post(requestBody)
                    .build()
                
                // Ejecutar la petición
                val response = httpClient.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseBody = response.body?.string() ?: "{}"
                    val jsonResponse = JSONObject(responseBody)
                    
                    // Leer los campos con los nombres correctos de la respuesta
                    val grade = jsonResponse.optDouble("nota", 0.0)
                    val correction = jsonResponse.optString("correccion", "")
                    
                    Log.d(TAG, "Evaluación recibida: nota=$grade")
                    Result.success(AnswerEvaluation(grade, correction))
                } else {
                    Log.e(TAG, "Error en el servicio: ${response.code}")
                    Result.failure(Exception("Error en el servicio: ${response.code}"))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error al evaluar respuesta: ${e.message}")
                Result.failure(e)
            }
        }
    }
} 