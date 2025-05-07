package es.uc3m.android.samplex.utils

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class GoogleAuthUiClient(private val context: Context) {

    // Configuración con ID de cliente web y forzando selección de cuenta
    private val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken("26913052212-meu6chh5glgke7b5knjfrs1rm7nh2o8a.apps.googleusercontent.com") // Tu client ID web
        .requestEmail()
        .build()

    private val googleSignInClient: GoogleSignInClient = GoogleSignIn.getClient(context, gso)

    // Este intent mostrará el selector de cuentas disponibles
    fun getSignInIntent(): Intent {
        // Cerramos sesión antes de lanzar el intent para forzar selector de cuenta
        GoogleSignIn.getClient(context, gso).signOut()
        return GoogleSignIn.getClient(context, gso).signInIntent
    }


    suspend fun signInWithIntent(intent: Intent): FirebaseUser? {
        return try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(intent)
            val account: GoogleSignInAccount = task.await()
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            val result = FirebaseAuth.getInstance().signInWithCredential(credential).await()
            result.user
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun signOut() {
        FirebaseAuth.getInstance().signOut()
        googleSignInClient.signOut()
    }
}

// Extensión suspend para usar await con Tasks

suspend fun <T> Task<T>.await(): T =
    suspendCancellableCoroutine { cont ->
        addOnSuccessListener { cont.resume(it) }
        addOnFailureListener { cont.resumeWithException(it) }
    }
