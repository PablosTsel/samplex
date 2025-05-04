package es.uc3m.android.samplex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

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
}
