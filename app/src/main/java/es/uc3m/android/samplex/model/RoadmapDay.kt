package es.uc3m.android.samplex.model

import android.util.Log
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Data class representing a day in the exam roadmap
 */
data class RoadmapDay(
    val fecha: Date,
    val descripcion: String,
    val final: Boolean = false,
    val isCompleted: Boolean = false,
    val tasks: List<Map<String, Any>> = emptyList(),
    val examId: String = "",  // ID of the exam this day belongs to
    val dayIndex: Int = 0     // Index of this day in the content array
) {
    /**
     * Returns a formatted date string (e.g., "May 5")
     */
    fun getFormattedDate(): String {
        return try {
            val formatter = SimpleDateFormat("MMM d", Locale.getDefault())
            formatter.format(fecha)
        } catch (e: Exception) {
            Log.e("RoadmapDay", "Error formatting date: ${e.message}")
            "Unknown"
        }
    }
    
    /**
     * Compares the day part of two dates to see if they are the same day
     */
    private fun isSameDay(date1: Calendar, date2: Calendar): Boolean {
        return date1.get(Calendar.YEAR) == date2.get(Calendar.YEAR) &&
               date1.get(Calendar.MONTH) == date2.get(Calendar.MONTH) &&
               date1.get(Calendar.DAY_OF_MONTH) == date2.get(Calendar.DAY_OF_MONTH)
    }
    
    /**
     * Checks if this day is today
     */
    fun isToday(): Boolean {
        val today = Calendar.getInstance()
        val dayDate = Calendar.getInstance()
        dayDate.time = fecha
        
        return isSameDay(today, dayDate)
    }
    
    /**
     * Checks if this day is in the past
     */
    fun isPast(): Boolean {
        if (isToday()) return false
        
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        
        val dayDate = Calendar.getInstance().apply {
            time = fecha
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        
        return dayDate.before(today)
    }
    
    /**
     * Checks if this day is in the future
     */
    fun isFuture(): Boolean {
        return !isToday() && !isPast()
    }
} 