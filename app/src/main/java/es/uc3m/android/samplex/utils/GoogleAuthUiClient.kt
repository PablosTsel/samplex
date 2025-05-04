package es.uc3m.android.samplex.utils

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await

class GoogleAuthUiClient(
    private val context: Context,
    private val oneTapClient: SignInClient = Identity.getSignInClient(context)
) {
    private val signInRequest = BeginSignInRequest.builder()
        .setGoogleIdTokenRequestOptions(
            BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                .setSupported(true)
                .setServerClientId("26913052212-meu6chh5glgke7b5knjfrs1rm7nh2o8a.apps.googleusercontent.com")
                .setFilterByAuthorizedAccounts(false)
                .build()
        )
        .setAutoSelectEnabled(false) // <- Esto fuerza el pop-up de selección de cuenta
        .build()

    suspend fun signIn(): IntentSender? {
        val result = oneTapClient.beginSignIn(signInRequest).await()
        return result.pendingIntent.intentSender
    }

    suspend fun signInWithIntent(intent: Intent): FirebaseUser? {
        val credential = oneTapClient.getSignInCredentialFromIntent(intent)
        val idToken = credential.googleIdToken
        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
        return FirebaseAuth.getInstance().signInWithCredential(firebaseCredential).await().user
    }

    fun signOut() {
        FirebaseAuth.getInstance().signOut()
    }
}
