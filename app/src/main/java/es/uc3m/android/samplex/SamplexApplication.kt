package es.uc3m.android.samplex

import android.app.Application
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import es.uc3m.android.samplex.utils.DailyNotificationWorker
import es.uc3m.android.samplex.utils.NotificationUtils
import java.util.Calendar
import java.util.concurrent.TimeUnit

class SamplexApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Inicializar Firebase
        FirebaseApp.initializeApp(this)
        
        // Configurar Firestore
        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)  // Habilitar caché offline
            .build()
        
        FirebaseFirestore.getInstance().firestoreSettings = settings
        
        // Inicializar el canal de notificaciones
        NotificationUtils.createNotificationChannel(this)
        
        // Programar el worker para las notificaciones diarias
        scheduleDailyNotificationWorker()
    }
    
    private fun scheduleDailyNotificationWorker() {
        // 1. Programar ejecución periódica a las 14:00
        val now = Calendar.getInstance()
        val targetTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 14)  // 14:00 (2 PM)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            
            // Si ya pasó la hora objetivo hoy, programar para mañana
            if (before(now)) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }
        
        // Calcular el retraso inicial
        val initialDelay = targetTime.timeInMillis - now.timeInMillis
        
        // Crear solicitud periódica
        val periodicWorkRequest = PeriodicWorkRequestBuilder<DailyNotificationWorker>(
            24, TimeUnit.HOURS
        )
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .build()
        
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "daily_reminder_notification",
            ExistingPeriodicWorkPolicy.KEEP,
            periodicWorkRequest
        )
        
        // 2. Ejecutar inmediatamente para pruebas
        val immediateWorkRequest = OneTimeWorkRequestBuilder<DailyNotificationWorker>()
            .build()
        
        WorkManager.getInstance(this).enqueue(immediateWorkRequest)
    }
} 