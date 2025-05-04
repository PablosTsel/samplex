package es.uc3m.android.samplex.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import es.uc3m.android.samplex.ui.theme.SuccessColor
import es.uc3m.android.samplex.viewmodel.AuthViewModel
import es.uc3m.android.samplex.viewmodel.ExamViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    authViewModel: AuthViewModel = viewModel(),
    examViewModel: ExamViewModel = viewModel()
) {
    // State
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

    var showProfileMenu by remember { mutableStateOf(false) }
    var showExamForm by remember { mutableStateOf(false) }
    var showSettingsScreen by remember { mutableStateOf(false) }
    var showAccountScreen by remember { mutableStateOf(false) }
    var subjectName by remember { mutableStateOf("") }
    var examDate by remember { mutableStateOf<Date?>(null) }
    var showExamsDropdown by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var isEmergencyExam by remember { mutableStateOf(false) }
    
    // For date picker
    val datePickerState = rememberDatePickerState()
    
    // View Model states
    val isLoading by examViewModel.isLoading.collectAsState()
    val error by examViewModel.error.collectAsState()
    val examCreated by examViewModel.examCreated.collectAsState()
    val exams by examViewModel.exams.collectAsState()

    val dropdownRotation by animateFloatAsState(
        targetValue = if (showExamsDropdown) 180f else 0f,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "dropdownRotation"
    )

    if (showSettingsScreen) {
        SettingsScreen(
            onBackClick = { showSettingsScreen = false }
        )
    } else if (showAccountScreen) {
        AccountScreen(
            onBackClick = { showAccountScreen = false }
        )
    } else {
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
                        // Logo/Title in center
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

                        // Profile menu button
                        Box(modifier = Modifier.align(Alignment.CenterEnd)) {
                            IconButton(
                                onClick = { showProfileMenu = !showProfileMenu }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = stringResource(id = R.string.profile),
                                    tint = LightText
                                )
                            }

                            DropdownMenu(
                                expanded = showProfileMenu,
                                onDismissRequest = { showProfileMenu = false },
                                modifier = Modifier
                                    .background(LightSurface)
                                    .width(180.dp)
                            ) {
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = stringResource(id = R.string.my_account),
                                            color = LightText
                                        )
                                    },
                                    onClick = {
                                        showProfileMenu = false
                                        showAccountScreen = true
                                    }
                                )
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = stringResource(id = R.string.settings),
                                            color = LightText
                                        )
                                    },
                                    onClick = {
                                        showProfileMenu = false
                                        showSettingsScreen = true
                                    }
                                )
                                Divider(color = Color.Gray.copy(alpha = 0.3f))
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = stringResource(id = R.string.log_out),
                                            color = ErrorColor
                                        )
                                    },
                                    onClick = {
                                        showProfileMenu = false
                                        authViewModel.signOut()
                                    }
                                )
                            }
                        }
                    }
                }
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        showExamForm = true
                        subjectName = ""
                        examDate = null
                        isEmergencyExam = false
                    },
                    containerColor = BabyBlueDark,
                    shape = CircleShape
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = stringResource(id = R.string.add_new_exam),
                        tint = Color.White
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
                ) {
                    // Welcome message
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
                                    text = stringResource(id = R.string.welcome_title),
                                    style = MaterialTheme.typography.headlineSmall.copy(
                                        fontWeight = FontWeight.Light
                                    ),
                                    color = LightText,
                                    textAlign = TextAlign.Center
                                )
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                Text(
                                    text = stringResource(id = R.string.upload_instruction),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = LightSecondaryText,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Exams section title with dropdown
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(id = R.string.previous_ongoing_exams),
                            style = MaterialTheme.typography.titleMedium,
                            color = LightText,
                            modifier = Modifier.weight(1f)
                        )
                        
                        IconButton(
                            onClick = { showExamsDropdown = !showExamsDropdown }
                        ) {
                            Icon(
                                Icons.Default.ArrowDropDown,
                                contentDescription = stringResource(id = R.string.menu),
                                tint = BabyBlueDark,
                                modifier = Modifier.rotate(dropdownRotation)
                            )
                        }
                    }
                    
                    // Exams list (expandable)
                    AnimatedVisibility(
                        visible = showExamsDropdown,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        if (exams.isEmpty()) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = BabyBlueLight.copy(alpha = 0.3f))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "No exams yet. Create your first exam roadmap!",
                                        color = LightSecondaryText
                                    )
                                }
                            }
                        } else {
                            Column {
                                exams.forEach { exam ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp),
                                        shape = RoundedCornerShape(16.dp),
                                        colors = CardDefaults.cardColors(containerColor = BabyBlueLight.copy(alpha = 0.3f))
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp)
                                        ) {
                                            Text(
                                                text = exam.subjectName,
                                                style = MaterialTheme.typography.titleMedium,
                                                color = BabyBlueDark
                                            )
                                            
                                            Spacer(modifier = Modifier.height(4.dp))
                                            
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    Icons.Default.DateRange,
                                                    contentDescription = null,
                                                    tint = LightSecondaryText,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                
                                                Spacer(modifier = Modifier.width(4.dp))
                                                
                                                val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                                                Text(
                                                    text = formatter.format(exam.examDate),
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = LightSecondaryText
                                                )
                                            }
                                            
                                            if (exam.isEmergency) {
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = "Emergency Exam",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = ErrorColor
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    // Show success message if exam was created
                    AnimatedVisibility(
                        visible = examCreated,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = SuccessColor.copy(alpha = 0.7f))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = stringResource(id = R.string.success_message),
                                    color = Color.White,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                            }
                        }
                        
                        // Auto-hide success message after a delay
                        LaunchedEffect(examCreated) {
                            kotlinx.coroutines.delay(3000)
                            examViewModel.resetExamCreated()
                        }
                    }
                }
                
                // Create Exam Form Dialog
                if (showExamForm) {
                    AlertDialog(
                        onDismissRequest = { showExamForm = false },
                        title = {
                            Text(
                                text = stringResource(id = R.string.exam_form_title),
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = LightText
                            )
                        },
                        text = {
                            Column {
                                // Subject Name
                                OutlinedTextField(
                                    value = subjectName,
                                    onValueChange = { subjectName = it },
                                    label = { Text(stringResource(id = R.string.subject_name)) },
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
                                
                                // Exam Date
                                OutlinedTextField(
                                    value = examDate?.let { 
                                        SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(it) 
                                    } ?: "",
                                    onValueChange = { },
                                    label = { Text(stringResource(id = R.string.exam_date)) },
                                    modifier = Modifier.fillMaxWidth(),
                                    readOnly = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = LightText,
                                        unfocusedTextColor = LightText,
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent,
                                        cursorColor = BabyBlueDark,
                                        focusedBorderColor = BabyBlueDark,
                                        unfocusedBorderColor = BabyBlue.copy(alpha = 0.5f)
                                    ),
                                    trailingIcon = {
                                        IconButton(onClick = { showDatePicker = true }) {
                                            Icon(
                                                Icons.Default.DateRange,
                                                contentDescription = stringResource(id = R.string.select_date),
                                                tint = BabyBlueDark
                                            )
                                        }
                                    }
                                )
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                // Emergency Exam Toggle
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.emergency_exam),
                                        modifier = Modifier.weight(1f),
                                        color = LightText
                                    )
                                    Switch(
                                        checked = isEmergencyExam,
                                        onCheckedChange = { isEmergencyExam = it },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = Color.White,
                                            checkedTrackColor = BabyBlueDark,
                                            uncheckedThumbColor = Color.White,
                                            uncheckedTrackColor = LightSecondaryText.copy(alpha = 0.3f)
                                        )
                                    )
                                }
                                
                                // Error message
                                AnimatedVisibility(
                                    visible = error != null,
                                    enter = fadeIn() + expandVertically(),
                                    exit = fadeOut() + shrinkVertically()
                                ) {
                                    error?.let {
                                        Text(
                                            text = it,
                                            color = ErrorColor,
                                            style = MaterialTheme.typography.bodySmall,
                                            modifier = Modifier.padding(top = 8.dp)
                                        )
                                    }
                                }
                            }
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    // Validate form
                                    when {
                                        subjectName.isBlank() -> {
                                            // Show error
                                        }
                                        examDate == null -> {
                                            // Show error
                                        }
                                        else -> {
                                            // Create exam
                                            examViewModel.createExam(
                                                subjectName = subjectName,
                                                examDate = examDate!!,
                                                isEmergency = isEmergencyExam
                                            )
                                            showExamForm = false
                                        }
                                    }
                                },
                                enabled = !isLoading,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = BabyBlueDark,
                                    contentColor = Color.White
                                )
                            ) {
                                if (isLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        strokeWidth = 2.dp,
                                        color = Color.White
                                    )
                                } else {
                                    Text(stringResource(id = R.string.create_roadmap))
                                }
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = { showExamForm = false },
                                colors = ButtonDefaults.textButtonColors(
                                    contentColor = BabyBlueDark
                                )
                            ) {
                                Text(stringResource(id = R.string.cancel))
                            }
                        },
                        containerColor = LightSurface
                    )
                }
                
                // Date Picker Dialog
                if (showDatePicker) {
                    // Use Material3 DatePickerDialog wrapper
                    DatePickerDialog(
                        onDismissRequest = { showDatePicker = false },
                        confirmButton = {
                            Button(
                                onClick = {
                                    datePickerState.selectedDateMillis?.let {
                                        val calendar = Calendar.getInstance()
                                        calendar.timeInMillis = it
                                        examDate = calendar.time
                                    }
                                    showDatePicker = false
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = BabyBlueDark,
                                    contentColor = Color.White
                                )
                            ) {
                                Text(stringResource(id = R.string.select_date))
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = { showDatePicker = false },
                                colors = ButtonDefaults.textButtonColors(
                                    contentColor = BabyBlueDark
                                )
                            ) {
                                Text(stringResource(id = R.string.cancel))
                            }
                        }
                    ) {
                        // DatePicker content passed as a lambda
                        DatePicker(
                            state = datePickerState,
                            colors = DatePickerDefaults.colors(
                                selectedDayContainerColor = BabyBlueDark,
                                todayContentColor = BabyBlueDark,
                                todayDateBorderColor = BabyBlueDark
                            )
                        )
                    }
                }
            }
        }
    }
} 