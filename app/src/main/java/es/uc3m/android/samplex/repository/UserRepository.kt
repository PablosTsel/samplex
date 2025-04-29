package es.uc3m.android.samplex.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    
    suspend fun createUserProfile(userId: String, name: String = "", age: Int? = null, telephone: String = "") {
        val user = hashMapOf(
            "name" to name,
            "age" to age,
            "telephone" to telephone,
            "exams" to emptyList<String>()
        )
        
        db.collection("users").document(userId).set(user).await()
    }
    
    suspend fun getUserProfile(userId: String): UserProfile? {
        val document = db.collection("users").document(userId).get().await()
        return if (document.exists()) {
            UserProfile(
                userId = userId,
                name = document.getString("name") ?: "",
                age = document.getLong("age")?.toInt(),
                telephone = document.getString("telephone") ?: "",
                examIds = document.get("exams") as? List<String> ?: emptyList()
            )
        } else {
            null
        }
    }
    
    suspend fun updateUserProfile(userProfile: UserProfile) {
        val updates = hashMapOf<String, Any>()
        
        if (userProfile.name.isNotBlank()) updates["name"] = userProfile.name
        userProfile.age?.let { updates["age"] = it }
        if (userProfile.telephone.isNotBlank()) updates["telephone"] = userProfile.telephone
        
        db.collection("users").document(userProfile.userId).update(updates).await()
    }
    
    suspend fun getCurrentUser(): UserProfile? {
        val currentUser = auth.currentUser ?: return null
        return getUserProfile(currentUser.uid)
    }
}

data class UserProfile(
    val userId: String,
    val name: String = "",
    val age: Int? = null,
    val telephone: String = "",
    val examIds: List<String> = emptyList()
) 