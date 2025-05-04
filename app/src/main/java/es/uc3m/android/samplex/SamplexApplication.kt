package es.uc3m.android.samplex

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings

class SamplexApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Inicializar Firebase
        FirebaseApp.initializeApp(this)
        
        // Configurar Firestore
        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)  // Habilitar caché offline
            .setDatabaseId("samplex")     // Especificar el nombre de la base de datos
            .build()
        
        FirebaseFirestore.getInstance().firestoreSettings = settings
    }
} 