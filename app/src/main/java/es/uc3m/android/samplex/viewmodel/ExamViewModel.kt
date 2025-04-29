package es.uc3m.android.samplex.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Date
import java.util.UUID

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
    
    init {
        fetchUserExams()
    }
    
    fun createExam(subjectName: String, examDate: Date, isEmergency: Boolean = false) {
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
                
                val examId = UUID.randomUUID().toString()
                val exam = Exam(
                    id = examId,
                    subjectName = subjectName,
                    examDate = examDate,
                    userId = currentUser.uid,
                    isEmergency = isEmergency,
                    createdAt = Date()
                )
                
                // Add to Firestore
                db.collection("exams").document(examId).set(exam).await()
                
                // Add exam ID to user's exam list
                val userRef = db.collection("users").document(currentUser.uid)
                userRef.update("exams", com.google.firebase.firestore.FieldValue.arrayUnion(examId)).await()
                
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
    
    private fun fetchUserExams() {
        viewModelScope.launch {
            try {
                val currentUser = auth.currentUser ?: return@launch
                
                _isLoading.value = true
                
                // Get user's exam IDs
                val userDoc = db.collection("users").document(currentUser.uid).get().await()
                val examIds = userDoc.get("exams") as? List<String> ?: emptyList()
                
                if (examIds.isEmpty()) {
                    _exams.value = emptyList()
                    return@launch
                }
                
                // Fetch all exams
                val examsList = mutableListOf<Exam>()
                examIds.forEach { examId ->
                    val examDoc = db.collection("exams").document(examId).get().await()
                    examDoc.toObject(Exam::class.java)?.let { 
                        examsList.add(it)
                    }
                }
                
                // Sort by date (most recent first)
                _exams.value = examsList.sortedByDescending { it.examDate }
                
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
}

// Data class to represent an Exam
data class Exam(
    val id: String = "",
    val subjectName: String = "",
    val examDate: Date = Date(),
    val userId: String = "",
    val isEmergency: Boolean = false,
    val createdAt: Date = Date(),
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