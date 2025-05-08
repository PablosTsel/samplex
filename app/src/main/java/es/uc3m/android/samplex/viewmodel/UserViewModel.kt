package es.uc3m.android.samplex.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import es.uc3m.android.samplex.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

private const val TAG = "UserViewModel"

class UserViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val usersCollection = db.collection("users")
    
    // UI state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error
    
    private val _userData = MutableStateFlow<User?>(null)
    val userData: StateFlow<User?> = _userData
    
    private val _updateSuccess = MutableStateFlow(false)
    val updateSuccess: StateFlow<Boolean> = _updateSuccess
    
    private var userListener: ListenerRegistration? = null
    
    init {
        setupUserListener()
    }
    
    private fun setupUserListener() {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            _error.value = "User not authenticated"
            return
        }
        
        // Cancelar listener anterior si existe
        userListener?.remove()
        
        // Configurar nuevo listener en tiempo real
        userListener = usersCollection.document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Error listening for user updates", error)
                    _error.value = error.message
                    return@addSnapshotListener
                }
                
                if (snapshot != null && snapshot.exists()) {
                    val user = snapshot.toObject(User::class.java)
                    _userData.value = user
                    Log.d(TAG, "User data updated in real-time: ${user?.email}")
                }
            }
    }
    
    fun fetchUserData() {
        viewModelScope.launch {
            val userId = auth.currentUser?.uid
            if (userId == null) {
                _error.value = "User not authenticated"
                return@launch
            }
            
            try {
                _isLoading.value = true
                _error.value = null
                Log.d(TAG, "Fetching user data for ID: $userId")
                
                val userDoc = usersCollection.document(userId).get().await()
                if (userDoc.exists()) {
                    val user = userDoc.toObject(User::class.java)
                    
                    // If the email is empty or doesn't match the auth email, update it with auth email
                    if (user != null && (user.email.isEmpty() || user.email != auth.currentUser?.email)) {
                        auth.currentUser?.email?.let { authEmail ->
                            Log.d(TAG, "Updating email from auth: $authEmail")
                            
                            // Update email in Firestore
                            usersCollection.document(userId).update("email", authEmail).await()
                            
                            // Update local user object
                            _userData.value = user.copy(email = authEmail)
                        }
                    } else {
                        _userData.value = user
                        Log.d(TAG, "User data fetched successfully: ${user?.email}")
                    }
                } else {
                    // User document not found, create it with information from auth
                    auth.currentUser?.let { firebaseUser ->
                        val email = firebaseUser.email ?: ""
                        Log.d(TAG, "Creating new user document with email: $email")
                        
                        val newUser = User(
                            nombre = "",
                            apellidos = "",
                            curso = "",
                            email = email,
                            exams = emptyList()
                        )
                        
                        usersCollection.document(userId).set(newUser).await()
                        _userData.value = newUser
                    } ?: run {
                        Log.e(TAG, "User document not found and no authenticated user")
                        _error.value = "User profile not found"
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching user data", e)
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun updateUserProfile(nombre: String, apellidos: String, curso: String) {
        viewModelScope.launch {
            val userId = auth.currentUser?.uid
            if (userId == null) {
                _error.value = "User not authenticated"
                return@launch
            }
            
            try {
                _isLoading.value = true
                _error.value = null
                _updateSuccess.value = false
                Log.d(TAG, "Updating user profile for ID: $userId")
                
                // Get the current auth email
                val currentEmail = auth.currentUser?.email ?: ""
                
                // Only update the fields that can be modified
                val userData = _userData.value
                val updatedData = hashMapOf<String, Any>(
                    "nombre" to nombre,
                    "apellidos" to apellidos,
                    "curso" to curso,
                    "email" to currentEmail // Always use the current auth email
                )
                
                // Keep exams array if it exists
                userData?.exams?.let {
                    if (it.isNotEmpty()) {
                        updatedData["exams"] = it
                    }
                }
                
                usersCollection.document(userId).update(updatedData).await()
                Log.d(TAG, "User profile updated successfully")
                
                // Update the local state
                _userData.value = User(
                    nombre = nombre,
                    apellidos = apellidos,
                    curso = curso,
                    email = currentEmail,
                    exams = userData?.exams ?: emptyList()
                )
                
                _updateSuccess.value = true
            } catch (e: Exception) {
                Log.e(TAG, "Error updating user profile", e)
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun resetUpdateSuccess() {
        _updateSuccess.value = false
    }
    
    override fun onCleared() {
        // Importante: remover los listeners cuando el ViewModel se destruye
        userListener?.remove()
        super.onCleared()
    }
} 