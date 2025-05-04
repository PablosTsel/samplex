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
import es.uc3m.android.samplex.ui.theme.BabyBlue
import es.uc3m.android.samplex.ui.theme.BabyBlueDark
import es.uc3m.android.samplex.ui.theme.BabyBlueLight
import es.uc3m.android.samplex.ui.theme.ErrorColor
import es.uc3m.android.samplex.ui.theme.LightBackground
import es.uc3m.android.samplex.ui.theme.LightSurface
import es.uc3m.android.samplex.ui.theme.LightText
import es.uc3m.android.samplex.ui.theme.LightSecondaryText
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
            LightBackground,
            LightBackground.copy(alpha = 0.95f),
            Color.White
        )
    )
    
    Scaffold(
        topBar = {
            // Top bar with the app name "Samplex"
            Surface(
                modifier = Modifier.shadow(2.dp),
                color = BabyBlue
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BabyBlue)
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
                                        color = LightText
                                    )
                                ) {
                                    append("sample")
                                }
                                withStyle(
                                    style = SpanStyle(
                                        fontWeight = FontWeight.Bold,
                                        color = BabyBlueDark
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
                    color = LightText
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Card with email and password fields
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = LightSurface)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    ) {
                        Column {
                            OutlinedTextField(
                                value = email,
                                onValueChange = { email = it },
                                label = {
                                    Text(
                                        stringResource(id = R.string.email),
                                        color = LightSecondaryText
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = BabyBlueDark,
                                    unfocusedBorderColor = BabyBlue,
                                    focusedTextColor = LightText,
                                    unfocusedTextColor = LightText,
                                    cursorColor = BabyBlueDark,
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent
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
                                        color = LightSecondaryText
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = BabyBlueDark,
                                    unfocusedBorderColor = BabyBlue,
                                    focusedTextColor = LightText,
                                    unfocusedTextColor = LightText,
                                    cursorColor = BabyBlueDark,
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent
                                ),
                                shape = RoundedCornerShape(8.dp),
                                visualTransformation = PasswordVisualTransformation()
                            )
                            
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            // Main action button (Sign In or Create Account)
                            Button(
                                onClick = {
                                    if (isRegisterMode) {
                                        authViewModel.createAccount(email, password)
                                    } else {
                                        authViewModel.signIn(email, password)
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !isLoading,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = BabyBlueDark,
                                    contentColor = Color.White,
                                    disabledContainerColor = BabyBlue.copy(alpha = 0.6f)
                                )
                            ) {
                                if (isLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        color = Color.White,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Text(
                                        text = stringResource(
                                            id = if (isRegisterMode) R.string.create_account else R.string.sign_in
                                        )
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Toggle button between Sign In and Create Account
                            TextButton(
                                onClick = { isRegisterMode = !isRegisterMode },
                                modifier = Modifier.align(Alignment.CenterHorizontally),
                                colors = ButtonDefaults.textButtonColors(
                                    contentColor = BabyBlueDark
                                )
                            ) {
                                Text(
                                    text = stringResource(
                                        id = if (isRegisterMode) R.string.login_register_toggle else R.string.register_login_toggle
                                    )
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            // Alternative sign in methods (like Google)
                            // This could be a divider with "or" text
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Divider(
                                    modifier = Modifier.weight(1f),
                                    color = Color.Gray.copy(alpha = 0.3f)
                                )
                                Text(
                                    text = "or",
                                    color = LightSecondaryText,
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )
                                Divider(
                                    modifier = Modifier.weight(1f),
                                    color = Color.Gray.copy(alpha = 0.3f)
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Google sign in button
                            OutlinedButton(
                                onClick = { /* TODO: Implement Google Sign In */ },
                                modifier = Modifier.fillMaxWidth(),
                                border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.3f)),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = LightText
                                )
                            ) {
                                Text(stringResource(id = R.string.sign_in_google))
                            }
                            
                            // Error message
                            AnimatedVisibility(
                                visible = authError != null,
                                enter = fadeIn(),
                                exit = fadeOut()
                            ) {
                                Text(
                                    text = authError ?: "",
                                    color = ErrorColor,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(top = 16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
} 