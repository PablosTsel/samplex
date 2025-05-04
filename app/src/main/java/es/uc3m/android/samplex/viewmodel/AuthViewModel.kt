package es.uc3m.android.samplex.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

private const val TAG = "AuthViewModel"

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    
    // UI state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError
    
    private val _isLoggedIn = MutableStateFlow(auth.currentUser != null)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn
    
    init {
        // Initialize authentication state
        auth.addAuthStateListener { firebaseAuth ->
            _isLoggedIn.value = firebaseAuth.currentUser != null
            Log.d(TAG, "Auth state changed. User logged in: ${_isLoggedIn.value}")
        }
    }
    
    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _authError.value = null
                Log.d(TAG, "Attempting to sign in user: $email")
                
                auth.signInWithEmailAndPassword(email, password).await()
                Log.d(TAG, "Sign in successful for user: $email")
                // Success is handled by the AuthStateListener
            } catch (e: Exception) {
                Log.e(TAG, "Error signing in", e)
                _authError.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun createAccount(email: String, password: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _authError.value = null
                
                Log.d(TAG, "Attempting to create authentication account for: $email")
                // Create authentication account
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                Log.d(TAG, "Authentication account created successfully")
                
                // Get the user ID from the result
                val userId = result.user?.uid
                if (userId == null) {
                    throw Exception("Failed to get user ID after registration")
                }
                
                // Create user document in Firestore using user ID
                val userData = hashMapOf(
                    "apellidos" to "",
                    "curso" to "",
                    "email" to email,
                    "examenes" to emptyList<String>(),
                    "nombre" to ""
                )
                
                Log.d(TAG, "Attempting to create Firestore document for user: $userId")
                db.collection("users").document(userId).set(userData).await()
                Log.d(TAG, "Firestore document created successfully")
                
                // Success is handled by the AuthStateListener
            } catch (e: Exception) {
                Log.e(TAG, "Error creating account", e)
                _authError.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun signOut() {
        Log.d(TAG, "Signing out user")
        auth.signOut()
        // The state will be updated by the AuthStateListener
    }
} 