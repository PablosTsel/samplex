package es.uc3m.android.samplex

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import es.uc3m.android.samplex.ui.screens.AuthScreen
import es.uc3m.android.samplex.ui.screens.HomeScreen
import es.uc3m.android.samplex.ui.theme.SamplexTheme
import es.uc3m.android.samplex.viewmodel.AuthViewModel
import es.uc3m.android.samplex.viewmodel.UserViewModel

class MainActivity : ComponentActivity() {
    
    private val TAG = "MainActivity"
    
    // Lanzador para solicitar permisos
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.d(TAG, "Permiso de notificaciones concedido")
        } else {
            Log.w(TAG, "Permiso de notificaciones denegado")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Solicitar permiso de notificaciones en Android 13+
        requestNotificationPermission()

        setContent {
            SamplexTheme {
                val navController = rememberNavController()
                val authViewModel: AuthViewModel = viewModel()
                val userViewModel: UserViewModel = viewModel()
                val isLoggedIn by authViewModel.isLoggedIn.collectAsState()

                NavHost(
                    navController = navController,
                    startDestination = if (isLoggedIn) "home" else "auth"
                ) {
                    composable("auth") {
                        AuthScreen(
                            navController = navController,
                            authViewModel = authViewModel
                        )
                    }
                    composable("home") {
                        userViewModel.fetchUserData()
                        HomeScreen()
                    }
                }
            }
        }
    }
    
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Log.d(TAG, "Verificando permisos de notificación en Android 13+")
            
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    Log.d(TAG, "Permiso de notificaciones ya concedido")
                }
                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    // Podríamos mostrar un diálogo explicativo aquí antes de solicitar
                    Log.d(TAG, "Debería mostrar explicación antes de solicitar permiso")
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
                else -> {
                    Log.d(TAG, "Solicitando permiso de notificaciones")
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            Log.d(TAG, "No es necesario solicitar permiso en versiones < Android 13")
        }
    }
}
