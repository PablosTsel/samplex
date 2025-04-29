package es.uc3m.android.samplex.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import es.uc3m.android.samplex.R
import es.uc3m.android.samplex.viewmodel.AuthViewModel

@Composable
fun AuthScreen(
    authViewModel: AuthViewModel = viewModel(),
    onLoginSuccess: () -> Unit
) {
    // State variables
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isRegisterMode by remember { mutableStateOf(false) }
    
    // Collect ViewModel states
    val isLoading by authViewModel.isLoading.collectAsState()
    val authError by authViewModel.authError.collectAsState()
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
    
    // Check if user is logged in
    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            onLoginSuccess()
        }
    }
    
    // Gradient definitions
    val backgroundGradient = Brush.verticalGradient(
        listOf(
            Color(0xFF1A1A2E),
            Color(0xFF16213E),
            Color(0xFF0F3460)
        )
    )
    val cardBackground = Brush.linearGradient(
        colors = listOf(
            Color(0xFF1E293B).copy(alpha = 0.9f),
            Color(0xFF0F172A).copy(alpha = 0.9f)
        )
    )
    val goldColor = Color(0xFFD4AF37)
    
    Scaffold(
        topBar = {
            // Top bar with the app name "Samplex"
            Surface(
                modifier = Modifier.shadow(8.dp),
                color = Color.Transparent
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(brush = cardBackground)
                        .padding(12.dp)
                ) {
                    // Logo/Title in the center
                    Row(
                        modifier = Modifier.align(Alignment.Center),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = buildAnnotatedString {
                                withStyle(
                                    style = SpanStyle(
                                        fontWeight = FontWeight.Light,
                                        color = Color.White
                                    )
                                ) {
                                    append("sample")
                                }
                                withStyle(
                                    style = SpanStyle(
                                        fontWeight = FontWeight.Bold,
                                        color = goldColor
                                    )
                                ) {
                                    append("x")
                                }
                            },
                            style = MaterialTheme.typography.headlineMedium.copy(
                                letterSpacing = 2.sp
                            )
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = backgroundGradient)
                .padding(paddingValues)
        ) {
            // Content centered
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Title that changes based on login/register mode
                Text(
                    text = stringResource(
                        id = if (isRegisterMode) R.string.create_account else R.string.sign_in
                    ),
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Light,
                        letterSpacing = 1.sp
                    ),
                    color = Color.White
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Card with email and password fields
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(16.dp, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(brush = cardBackground)
                            .padding(24.dp)
                    ) {
                        Column {
                            OutlinedTextField(
                                value = email,
                                onValueChange = { email = it },
                                label = {
                                    Text(
                                        stringResource(id = R.string.email),
                                        color = Color.White.copy(alpha = 0.7f)
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = goldColor,
                                    unfocusedBorderColor = Color(0xFF94A3B8),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    cursorColor = goldColor,
                                    focusedContainerColor = Color(0xFF1E293B),
                                    unfocusedContainerColor = Color(0xFF1E293B)
                                ),
                                shape = RoundedCornerShape(8.dp)
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            OutlinedTextField(
                                value = password,
                                onValueChange = { password = it },
                                label = {
                                    Text(
                                        stringResource(id = R.string.password),
                                        color = Color.White.copy(alpha = 0.7f)
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = goldColor,
                                    unfocusedBorderColor = Color(0xFF94A3B8),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    cursorColor = goldColor,
                                    focusedContainerColor = Color(0xFF1E293B),
                                    unfocusedContainerColor = Color(0xFF1E293B)
                                ),
                                shape = RoundedCornerShape(8.dp),
                                visualTransformation = PasswordVisualTransformation()
                            )
                            
                            // Show error message if there is one
                            AnimatedVisibility(
                                visible = authError != null,
                                enter = fadeIn(),
                                exit = fadeOut()
                            ) {
                                authError?.let {
                                    Text(
                                        text = it,
                                        color = MaterialTheme.colorScheme.error,
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.padding(top = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Button for "Login" or "Create Account"
                Button(
                    onClick = {
                        if (isRegisterMode) {
                            authViewModel.createAccount(email, password)
                        } else {
                            authViewModel.signIn(email, password)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = goldColor,
                        contentColor = Color(0xFF0F172A)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color(0xFF0F172A),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = stringResource(
                                id = if (isRegisterMode) R.string.create_account else R.string.sign_in
                            ),
                            style = MaterialTheme.typography.bodyLarge.copy(
                                letterSpacing = 1.sp,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Button to toggle between login/register
                TextButton(onClick = { isRegisterMode = !isRegisterMode }) {
                    Text(
                        text = stringResource(
                            id = if (isRegisterMode) 
                                R.string.login_register_toggle
                            else 
                                R.string.register_login_toggle
                        ),
                        color = goldColor
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Button sign in with Google (not fully implemented)
                OutlinedButton(
                    onClick = {
                        // TODO: Implement Google Sign-In
                    },
                    modifier = Modifier.fillMaxWidth(),
                    border = BorderStroke(1.dp, goldColor),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = goldColor
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.sign_in_google),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            letterSpacing = 1.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
        }
    }
} 