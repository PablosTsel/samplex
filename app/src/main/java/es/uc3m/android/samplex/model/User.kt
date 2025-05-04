package es.uc3m.android.samplex.model

/**
 * Data class representing user information stored in Firestore
 */
data class User(
    val nombre: String = "",
    val apellidos: String = "",
    val curso: String = "",
    val email: String = "",
    val examenes: List<String> = emptyList()
) 