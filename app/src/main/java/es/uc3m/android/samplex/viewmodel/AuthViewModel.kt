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
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser

private const val TAG = "AuthViewModel"

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val usersCollection = db.collection("users")

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError

    private val _isLoggedIn = MutableStateFlow(auth.currentUser != null)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    init {
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
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                Log.d(TAG, "Authentication account created successfully")

                val userId = result.user?.uid
                if (userId == null) throw Exception("Failed to get user ID after registration")

                val userData = hashMapOf(
                    "apellidos" to "",
                    "curso" to "",
                    "email" to email,
                    "exams" to emptyList<Map<String, Any>>(),
                    "nombre" to "",
                    "streak" to hashMapOf(
                        "count" to 0,
                        "lastCompletedDate" to null,
                        "currentDayCompleted" to false
                    )
                )

                try {
                    Log.d(TAG, "Creating Firestore document for user: $userId")
                    usersCollection.document(userId).set(userData).await()
                } catch (e: Exception) {
                    Log.e(TAG, "Error creating document, trying with email as ID")
                    usersCollection.document(email).set(userData).await()
                }

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
    }

    fun signInWithCredential(credential: AuthCredential) {
        _isLoading.value = true
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener { task ->
                _isLoading.value = false
                if (task.isSuccessful) {
                    _isLoggedIn.value = true
                    _authError.value = null
                } else {
                    _authError.value = task.exception?.message
                }
            }
    }

    fun onGoogleSignInSuccess(user: FirebaseUser) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _authError.value = null

                val userDoc = usersCollection.document(user.uid).get().await()
                if (!userDoc.exists()) {
                    val userData = mapOf(
                        "email" to user.email,
                        "nombre" to "",
                        "apellidos" to "",
                        "curso" to "",
                        "exams" to emptyList<Map<String, Any>>(),
                        "streak" to hashMapOf(
                            "count" to 0,
                            "lastCompletedDate" to null,
                            "currentDayCompleted" to false
                        )
                    )
                    usersCollection.document(user.uid).set(userData).await()
                    Log.d(TAG, "Nuevo usuario añadido a Firestore: ${user.uid}")
                } else {
                    Log.d(TAG, "Usuario ya existente en Firestore: ${user.uid}")
                }

                _isLoggedIn.value = true
            } catch (e: Exception) {
                Log.e(TAG, "Error al registrar o verificar usuario de Google", e)
                _authError.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
}
