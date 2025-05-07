package es.uc3m.android.samplex.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import es.uc3m.android.samplex.MainActivity
import es.uc3m.android.samplex.R

object NotificationUtils {
    private const val CHANNEL_ID = "samplex_reminders"
    private const val NOTIFICATION_ID = 1
    private const val TAG = "NotificationUtils"

    fun createNotificationChannel(context: Context) {
        try {
            // Crear el canal solo para Android 8.0 (API 26) y superior
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Log.d(TAG, "Creando canal de notificaciones en Android 8+")
                val name = "Recordatorios Samplex"
                val description = "Canal para recordatorios de actividades diarias"
                val importance = NotificationManager.IMPORTANCE_HIGH // Cambiar a HIGH para más visibilidad
                val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                    this.description = description
                    enableLights(true) // Activar luz LED de notificación
                    lightColor = context.resources.getColor(R.color.blue_500, null) // Color para LED
                }
                
                // Registrar el canal con el sistema
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
                Log.d(TAG, "Canal de notificaciones creado correctamente")
            } else {
                Log.d(TAG, "No es necesario crear canal en versiones < Android 8.0")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al crear canal de notificaciones", e)
        }
    }

    fun showReminderNotification(context: Context) {
        try {
            Log.d(TAG, "Preparando notificación...")
            
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            
            val pendingIntent = PendingIntent.getActivity(
                context, 0, intent, 
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

            // Cargar ícono grande para la notificación
            val largeIcon = BitmapFactory.decodeResource(context.resources, R.mipmap.logo_samplex_round)
            
            // Color de acento para la notificación
            val accentColor = context.resources.getColor(R.color.blue_500, null)
            
            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification) // Ícono específico para notificaciones
                .setLargeIcon(largeIcon) // Ícono grande a color
                .setContentTitle("Samplex reminder")
                .setContentText("Remember to complete your activities for today!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setColor(accentColor) // Color para tintes en notificaciones
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setStyle(NotificationCompat.BigTextStyle()
                    .bigText("Remember to complete your activities for today! Keep your daily streak going by completing your pending exercises."))
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
            
            // En Android 8+, añadir badge
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                builder.setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                    .setNumber(1)
            }
            
            Log.d(TAG, "Notificación construida, intentando mostrarla...")

            with(NotificationManagerCompat.from(context)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                    ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
                ) {
                    Log.e(TAG, "No hay permiso para mostrar notificaciones en Android 13+")
                    return
                }
                
                try {
                    // Cancelar notificaciones anteriores para evitar duplicados
                    cancel(NOTIFICATION_ID)
                    // Mostrar la nueva notificación
                    notify(NOTIFICATION_ID, builder.build())
                    Log.d(TAG, "Notificación mostrada correctamente")
                } catch (e: SecurityException) {
                    Log.e(TAG, "Error de seguridad al mostrar notificación", e)
                } catch (e: Exception) {
                    Log.e(TAG, "Error general al mostrar notificación", e)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error general al preparar notificación", e)
        }
    }
} 