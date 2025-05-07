package es.uc3m.android.samplex.utils

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.CountDownLatch

class DailyNotificationWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : Worker(appContext, workerParams) {

    companion object {
        private const val TAG = "DailyNotificationWorker"
    }

    override fun doWork(): Result {
        Log.d(TAG, "=== INICIANDO VERIFICACIÓN DE NOTIFICACIONES ===")

        try {
            // Crear canal de notificaciones
            NotificationUtils.createNotificationChannel(applicationContext)
            Log.d(TAG, "Canal de notificaciones creado/actualizado")
            
            // Verificar si hay un usuario autenticado
            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser == null) {
                Log.d(TAG, "No hay usuario autenticado, no se pueden verificar notificaciones")
                return Result.success() // No es un error, simplemente no hay nada que hacer
            }
            
            val userId = currentUser.uid
            Log.d(TAG, "Verificando notificaciones para usuario: ${currentUser.email} (ID: $userId)")
            
            // Usar CountDownLatch para esperar que la operación asíncrona termine
            val latch = CountDownLatch(1)
            var workResult = Result.success()
            
            // Consultar solo el documento del usuario actual
            FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    try {
                        if (!documentSnapshot.exists()) {
                            Log.d(TAG, "Documento de usuario no encontrado")
                            latch.countDown()
                            return@addOnSuccessListener
                        }
                        
                        Log.d(TAG, "Documento de usuario obtenido correctamente")
                        
                        val today = getTodayAtMidnight()
                        Log.d(TAG, "Fecha actual (medianoche): ${formatDate(today)}")
                        
                        // Verificar si tiene streak
                        val streak = documentSnapshot.get("streak") as? Map<String, Any>
                        if (streak == null) {
                            Log.d(TAG, "Usuario no tiene datos de streak")
                            latch.countDown()
                            return@addOnSuccessListener
                        }
                        
                        // Verificar si tiene lastCompletedDate
                        val lastCompletedDate = streak["lastCompletedDate"] as? Timestamp
                        if (lastCompletedDate == null) {
                            Log.d(TAG, "Usuario no tiene lastCompletedDate, enviando notificación")
                            NotificationUtils.showReminderNotification(applicationContext)
                            latch.countDown()
                            return@addOnSuccessListener
                        }
                        
                        // Convertir timestamp a fecha
                        val lastDate = lastCompletedDate.toDate()
                        Log.d(TAG, "Usuario lastCompletedDate: ${formatDate(lastDate)}")
                        
                        // Verificar si es el mismo día
                        val sameDay = isSameDay(lastDate, today)
                        Log.d(TAG, "¿Es la misma fecha que hoy? $sameDay")
                        
                        if (!sameDay) {
                            Log.d(TAG, "Enviando notificación al usuario porque no ha completado actividades hoy")
                            NotificationUtils.showReminderNotification(applicationContext)
                        } else {
                            Log.d(TAG, "Usuario ya completó actividades hoy, no se envía notificación")
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error procesando documento de usuario", e)
                    } finally {
                        latch.countDown()
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Error obteniendo documento de usuario", exception)
                    latch.countDown()
                }

            // Esperar a que termine la operación
            try {
                latch.await()
            } catch (e: InterruptedException) {
                Log.e(TAG, "Worker interrumpido mientras esperaba resultados", e)
                return Result.failure()
            }
            
            Log.d(TAG, "=== VERIFICACIÓN DE NOTIFICACIONES COMPLETADA ===")
            return workResult
        } catch (e: Exception) {
            Log.e(TAG, "Error general en el worker", e)
            return Result.failure()
        }
    }
    
    private fun getTodayAtMidnight(): Date {
        return Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time
    }
    
    private fun isSameDay(date1: Date, date2: Date): Boolean {
        val cal1 = Calendar.getInstance().apply { time = date1 }
        val cal2 = Calendar.getInstance().apply { time = date2 }
        
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
               cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }
    
    private fun formatDate(date: Date): String {
        val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        return formatter.format(date)
    }
} 