package es.uc3m.android.samplex.ui.screens

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import es.uc3m.android.samplex.R
import es.uc3m.android.samplex.ui.theme.*
import es.uc3m.android.samplex.viewmodel.AuthViewModel
import es.uc3m.android.samplex.utils.GoogleAuthUiClient
import kotlinx.coroutines.launch

@Composable
fun AuthScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isRegisterMode by remember { mutableStateOf(false) }

    val isLoading by authViewModel.isLoading.collectAsState()
    val authError by authViewModel.authError.collectAsState()
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val googleAuthUiClient = remember { GoogleAuthUiClient(context) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        scope.launch {
            result.data?.let { intent ->
                val user = googleAuthUiClient.signInWithIntent(intent)
                user?.let {
                    authViewModel.onGoogleSignInSuccess(it)
                }
            }
        }
    }

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            navController.navigate("home")
        }
    }

    val backgroundGradient = Brush.verticalGradient(
        listOf(
            LightBackground,
            LightBackground.copy(alpha = 0.95f),
            Color.White
        )
    )

    Scaffold(
        topBar = {
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
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
                                singleLine = true,
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
                                singleLine = true,
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

                            OutlinedButton(
                                onClick = {
                                    scope.launch {
                                        val intentSender = googleAuthUiClient.signIn()
                                        intentSender?.let {
                                            launcher.launch(IntentSenderRequest.Builder(it).build())
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.3f)),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = LightText
                                )
                            ) {
                                Text(stringResource(id = R.string.sign_in_google))
                            }

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
