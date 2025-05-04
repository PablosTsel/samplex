package es.uc3m.android.samplex.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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
import es.uc3m.android.samplex.ui.theme.SuccessColor
import es.uc3m.android.samplex.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    userViewModel: UserViewModel = viewModel()
) {
    // State from ViewModel
    val userData by userViewModel.userData.collectAsState()
    val isLoading by userViewModel.isLoading.collectAsState()
    val error by userViewModel.error.collectAsState()
    val updateSuccess by userViewModel.updateSuccess.collectAsState()
    
    // UI state
    var nombre by remember { mutableStateOf("") }
    var apellidos by remember { mutableStateOf("") }
    var curso by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    
    // Load user data when it's available
    LaunchedEffect(userData) {
        userData?.let {
            nombre = it.nombre
            apellidos = it.apellidos
            curso = it.curso
            email = it.email
        }
    }
    
    // Refresh user data when entering screen
    LaunchedEffect(Unit) {
        userViewModel.fetchUserData()
    }
    
    // Auto-hide success message after a delay
    LaunchedEffect(updateSuccess) {
        if (updateSuccess) {
            kotlinx.coroutines.delay(3000)
            userViewModel.resetUpdateSuccess()
        }
    }
    
    // UI color schemes
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            LightBackground,
            LightBackground.copy(alpha = 0.95f),
            Color.White
        )
    )
    
    val cardBackground = Brush.linearGradient(
        colors = listOf(
            LightSurface,
            LightSurface
        )
    )
    
    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier.shadow(2.dp),
                color = BabyBlue
            ) {
                TopAppBar(
                    title = { 
                        Text(
                            text = stringResource(id = R.string.settings),
                            color = LightText
                        ) 
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(id = R.string.back),
                                tint = LightText
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = BabyBlue
                    )
                )
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
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
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
                            .padding(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = stringResource(id = R.string.profile_settings),
                                style = MaterialTheme.typography.headlineSmall,
                                color = LightText,
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.SemiBold
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Nombre field
                            OutlinedTextField(
                                value = nombre,
                                onValueChange = { nombre = it },
                                label = { 
                                    Text(
                                        text = stringResource(id = R.string.nombre),
                                        color = LightSecondaryText
                                    ) 
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = LightText,
                                    unfocusedTextColor = LightText,
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    cursorColor = BabyBlueDark,
                                    focusedBorderColor = BabyBlueDark,
                                    unfocusedBorderColor = BabyBlue.copy(alpha = 0.5f)
                                )
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Apellidos field
                            OutlinedTextField(
                                value = apellidos,
                                onValueChange = { apellidos = it },
                                label = { 
                                    Text(
                                        text = stringResource(id = R.string.apellidos),
                                        color = LightSecondaryText
                                    ) 
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = LightText,
                                    unfocusedTextColor = LightText,
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    cursorColor = BabyBlueDark,
                                    focusedBorderColor = BabyBlueDark,
                                    unfocusedBorderColor = BabyBlue.copy(alpha = 0.5f)
                                )
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Curso field
                            OutlinedTextField(
                                value = curso,
                                onValueChange = { curso = it },
                                label = { 
                                    Text(
                                        text = stringResource(id = R.string.curso),
                                        color = LightSecondaryText
                                    ) 
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = LightText,
                                    unfocusedTextColor = LightText,
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    cursorColor = BabyBlueDark,
                                    focusedBorderColor = BabyBlueDark,
                                    unfocusedBorderColor = BabyBlue.copy(alpha = 0.5f)
                                )
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Email field (read-only)
                            OutlinedTextField(
                                value = email,
                                onValueChange = { },
                                label = { 
                                    Text(
                                        text = stringResource(id = R.string.email),
                                        color = LightSecondaryText
                                    ) 
                                },
                                modifier = Modifier.fillMaxWidth(),
                                readOnly = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = LightText,
                                    unfocusedTextColor = LightText,
                                    disabledTextColor = LightSecondaryText,
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    disabledContainerColor = Color.Transparent,
                                    cursorColor = BabyBlueDark,
                                    focusedBorderColor = BabyBlueDark,
                                    unfocusedBorderColor = BabyBlue.copy(alpha = 0.5f),
                                    disabledBorderColor = BabyBlue.copy(alpha = 0.3f)
                                )
                            )
                            
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            // Save button
                            Button(
                                onClick = {
                                    userViewModel.updateUserProfile(
                                        nombre = nombre,
                                        apellidos = apellidos,
                                        curso = curso
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !isLoading,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = BabyBlueDark,
                                    contentColor = Color.White,
                                    disabledContainerColor = BabyBlue.copy(alpha = 0.5f),
                                    disabledContentColor = Color.White.copy(alpha = 0.7f)
                                )
                            ) {
                                if (isLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.padding(8.dp),
                                        color = Color.White,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Text(stringResource(id = R.string.save_changes))
                                }
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Error message
                AnimatedVisibility(
                    visible = error != null,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = ErrorColor.copy(alpha = 0.8f)
                        )
                    ) {
                        Text(
                            text = error ?: "",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                
                // Success message
                AnimatedVisibility(
                    visible = updateSuccess,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = SuccessColor.copy(alpha = 0.8f)
                        )
                    ) {
                        Text(
                            text = stringResource(id = R.string.profile_updated),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
} 