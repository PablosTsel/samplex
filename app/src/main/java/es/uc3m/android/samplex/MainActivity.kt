package es.uc3m.android.samplex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import es.uc3m.android.samplex.ui.screens.AuthScreen
import es.uc3m.android.samplex.ui.screens.HomeScreen
import es.uc3m.android.samplex.ui.theme.SamplexTheme
import es.uc3m.android.samplex.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            SamplexTheme {
                // Using the ViewModel to determine authentication state
                val authViewModel: AuthViewModel = viewModel()
                val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
                
                // Display either AuthScreen or HomeScreen based on auth state
                if (isLoggedIn) {
                    HomeScreen()
                } else {
                    AuthScreen(
                        onLoginSuccess = {
                            // This callback isn't necessary anymore as the HomeScreen
                            // will be shown automatically due to ViewModel state changes
                        }
                    )
                }
            }
        }
    }
}

