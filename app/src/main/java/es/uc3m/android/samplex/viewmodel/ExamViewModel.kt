package es.uc3m.android.samplex.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import es.uc3m.android.samplex.model.RoadmapDay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Date
import java.util.UUID
import java.text.SimpleDateFormat
import java.util.Locale
import android.util.Log
import java.util.Calendar

class ExamViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    
    // UI state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error
    
    private val _examCreated = MutableStateFlow(false)
    val examCreated: StateFlow<Boolean> = _examCreated
    
    private val _exams = MutableStateFlow<List<Exam>>(emptyList())
    val exams: StateFlow<List<Exam>> = _exams
    
    // Current exam state for exam detail screen
    private val _currentExam = MutableStateFlow<Exam?>(null)
    val currentExam: StateFlow<Exam?> = _currentExam
    
    // Roadmap days for the current exam
    private val _roadmapDays = MutableStateFlow<List<RoadmapDay>>(emptyList())
    val roadmapDays: StateFlow<List<RoadmapDay>> = _roadmapDays
    
    // Last created exam ID
    private val _lastCreatedExamId = MutableStateFlow<String?>(null)
    val lastCreatedExamId: StateFlow<String?> = _lastCreatedExamId
    
    init {
        fetchUserExams()
    }
    
    fun createExam(subjectName: String, examDate: Date, isEmergency: Boolean = false, contentUri: Uri? = null) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                _examCreated.value = false
                
                val currentUser = auth.currentUser
                if (currentUser == null) {
                    _error.value = "User not authenticated"
                    return@launch
                }
                
                // Generate a unique ID for the exam
                val examId = UUID.randomUUID().toString()
                
                // Format date for name field (e.g., "Math May 15")
                val dateFormatter = SimpleDateFormat("MMM d", Locale.getDefault())
                val formattedDate = dateFormatter.format(examDate)
                val examName = "$subjectName $formattedDate"
                
                // Create exam entry for user document
                val examEntry = hashMapOf(
                    "name" to examName,
                    "id" to examId
                )
                
                // Create exam document
                val examData = hashMapOf(
                    "content" to arrayListOf<Any>(),
                    "subjectName" to subjectName,
                    "examDate" to examDate,
                    "userId" to currentUser.uid,
                    "isEmergency" to isEmergency,
                    "createdAt" to Date(),
                    "contentUri" to contentUri?.toString()
                )
                
                // Add to Firestore: first create the exam document
                db.collection("exams").document(examId).set(examData).await()
                
                // Then add exam reference to user's exams array
                val userRef = db.collection("users").document(currentUser.uid)
                userRef.update("exams", com.google.firebase.firestore.FieldValue.arrayUnion(examEntry)).await()
                
                // Save the last created exam ID
                _lastCreatedExamId.value = examId
                _examCreated.value = true
                
                // Refresh exam list
                fetchUserExams()
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun fetchExamById(examId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                _roadmapDays.value = emptyList()
                
                val examDoc = db.collection("exams").document(examId).get().await()
                if (!examDoc.exists()) {
                    _error.value = "Exam not found"
                    _currentExam.value = null
                    return@launch
                }
                
                val content = examDoc.get("content") as? List<Map<String, Any>> ?: emptyList()
                val subjectName = examDoc.getString("subjectName") ?: ""
                val examDate = examDoc.getDate("examDate") ?: Date()
                val userId = examDoc.getString("userId") ?: ""
                val isEmergency = examDoc.getBoolean("isEmergency") ?: false
                val createdAt = examDoc.getDate("createdAt") ?: Date()
                val contentUri = examDoc.getString("contentUri")
                
                val exam = Exam(
                    id = examId,
                    displayName = "$subjectName ${SimpleDateFormat("MMM d", Locale.getDefault()).format(examDate)}",
                    subjectName = subjectName,
                    examDate = examDate,
                    userId = userId,
                    isEmergency = isEmergency,
                    createdAt = createdAt,
                    contentUri = contentUri,
                    content = content
                )
                
                _currentExam.value = exam
                
                // Parse roadmap days from content
                if (content.isNotEmpty()) {
                    parseRoadmapDays(content)
                }
                
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    private fun parseRoadmapDays(content: List<Map<String, Any>>) {
        try {
            val days = content.mapNotNull { dayMap ->
                try {
                    // Handle fecha as either Date or String
                    val fecha = when (val fechaValue = dayMap["fecha"]) {
                        is Date -> fechaValue
                        is String -> {
                            // Try to parse the string date (e.g., "May 5")
                            try {
                                val sdf = SimpleDateFormat("MMM d", Locale.getDefault())
                                // Set year to current year for proper comparison
                                val calendar = Calendar.getInstance()
                                val parsedDate = sdf.parse(fechaValue) ?: Date()
                                val parsedCalendar = Calendar.getInstance()
                                parsedCalendar.time = parsedDate
                                
                                // Keep the parsed month and day but use current year
                                parsedCalendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR))
                                parsedCalendar.time
                            } catch (e: Exception) {
                                Log.e("ExamViewModel", "Error parsing date: ${e.message}")
                                Date() // Fallback to current date if parsing fails
                            }
                        }
                        else -> Date() // Default to current date
                    }
                    
                    val descripcion = dayMap["descripcion"] as? String ?: ""
                    val isFinal = dayMap["final"] as? Boolean ?: false
                    val isCompleted = dayMap["completed"] as? Boolean ?: false
                    val tasks = dayMap["tasks"] as? List<Map<String, Any>> ?: emptyList()
                    
                    RoadmapDay(
                        fecha = fecha,
                        descripcion = descripcion,
                        final = isFinal,
                        isCompleted = isCompleted,
                        tasks = tasks
                    )
                } catch (e: Exception) {
                    Log.e("ExamViewModel", "Error creating RoadmapDay: ${e.message}")
                    null
                }
            }
            
            _roadmapDays.value = days
        } catch (e: Exception) {
            _error.value = "Error parsing roadmap: ${e.message}"
        }
    }
    
    fun clearCurrentExam() {
        _currentExam.value = null
        _roadmapDays.value = emptyList()
    }
    
    private fun fetchUserExams() {
        viewModelScope.launch {
            try {
                val currentUser = auth.currentUser ?: return@launch
                
                _isLoading.value = true
                
                // Get user's exam entries from the users collection
                val userDoc = db.collection("users").document(currentUser.uid).get().await()
                val examEntries = userDoc.get("exams") as? List<Map<String, Any>> ?: emptyList()
                
                if (examEntries.isEmpty()) {
                    _exams.value = emptyList()
                    return@launch
                }
                
                // Convert exam entries to Exam objects
                val examsList = examEntries.map { entry ->
                    Exam(
                        id = entry["id"] as String,
                        displayName = entry["name"] as String
                    )
                }
                
                _exams.value = examsList
                
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun resetExamCreated() {
        _examCreated.value = false
    }
    
    fun clearError() {
        _error.value = null
    }
    
    fun setError(errorMessage: String) {
        _error.value = errorMessage
    }
    
    fun clearLastCreatedExamId() {
        _lastCreatedExamId.value = null
    }
}

// Data class to represent an Exam
data class Exam(
    val id: String = "",
    val displayName: String = "",
    val subjectName: String = "",
    val examDate: Date = Date(),
    val userId: String = "",
    val isEmergency: Boolean = false,
    val createdAt: Date = Date(),
    val contentUri: String? = null,
    val content: List<Map<String, Any>> = emptyList(),
    val days: List<StudyDay> = emptyList()
)

// Data class to represent a day in the study plan
data class StudyDay(
    val day: Int = 0,
    val description: String = "",
    val tasks: List<StudyTask> = emptyList()
)

// Data class to represent a study task
data class StudyTask(
    val id: String = "",
    val type: String = "", // "lecture", "quiz", etc.
    val title: String = "",
    val content: String = "",
    val completed: Boolean = false
) 