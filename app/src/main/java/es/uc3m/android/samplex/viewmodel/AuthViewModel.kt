package es.uc3m.android.samplex.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    
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
        }
    }
    
    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _authError.value = null
                
                auth.signInWithEmailAndPassword(email, password).await()
                // Success is handled by the AuthStateListener
            } catch (e: Exception) {
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
                
                auth.createUserWithEmailAndPassword(email, password).await()
                // Success is handled by the AuthStateListener
            } catch (e: Exception) {
                _authError.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun signOut() {
        auth.signOut()
        // The state will be updated by the AuthStateListener
    }
} 