package es.uc3m.android.samplex.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.text.style.TextOverflow
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
import androidx.compose.material3.HorizontalDivider

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

    var showProfileMenu by remember { mutableStateOf(false) }
    var showExamForm by remember { mutableStateOf(false) }
    var showSettingsScreen by remember { mutableStateOf(false) }
    var showAccountScreen by remember { mutableStateOf(false) }
    var showExamScreen by remember { mutableStateOf(false) }
    var selectedExamId by remember { mutableStateOf("") }
    var subjectName by remember { mutableStateOf("") }
    var examDate by remember { mutableStateOf<Date?>(null) }
    var showExamsDropdown by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var isEmergencyExam by remember { mutableStateOf(false) }
    var pdfUri by remember { mutableStateOf<Uri?>(null) }
    
    // View Model states
    val isLoading by examViewModel.isLoading.collectAsState()
    val error by examViewModel.error.collectAsState()
    val examCreated by examViewModel.examCreated.collectAsState()
    val exams by examViewModel.exams.collectAsState()
    val lastCreatedExamId by examViewModel.lastCreatedExamId.collectAsState()

    // When an exam is created, navigate to the exam screen
    LaunchedEffect(examCreated) {
        if (examCreated) {
            showExamForm = false
            
            // If we have the ID of the last created exam, navigate to it
            lastCreatedExamId?.let { examId ->
                selectedExamId = examId
                showExamScreen = true
                examViewModel.clearLastCreatedExamId()
            }
        }
    }

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
    } else if (showExamScreen && selectedExamId.isNotEmpty()) {
        ExamScreen(
            examId = selectedExamId,
            onBackClick = {
                showExamScreen = false
                selectedExamId = ""
            }
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
                                HorizontalDivider(color = Color.Gray.copy(alpha = 0.3f))
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
                        pdfUri = null
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
                                    text = "",
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
                                    .padding(vertical = 8.dp, horizontal = 4.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color.LightGray.copy(alpha = 0.3f)
                                )
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
                                            .padding(vertical = 8.dp, horizontal = 4.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = Color.LightGray.copy(alpha = 0.3f)
                                        ),
                                        onClick = {
                                            selectedExamId = exam.id
                                            showExamScreen = true
                                        }
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp)
                                        ) {
                                            Text(
                                                text = exam.displayName,
                                                style = MaterialTheme.typography.titleMedium,
                                                color = LightText
                                            )
                                        }
                                    }
                                }
                            }
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
                                
                                // PDF Attachment
                                val pdfPicker = rememberLauncherForActivityResult(
                                    contract = ActivityResultContracts.GetContent()
                                ) { uri: Uri? ->
                                    uri?.let {
                                        pdfUri = it
                                    }
                                }
                                
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = pdfUri?.let { 
                                            pdfUri.toString().substringAfterLast("/")
                                        } ?: stringResource(id = R.string.attach_content),
                                        modifier = Modifier.weight(1f),
                                        color = LightText,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    
                                    IconButton(
                                        onClick = { 
                                            pdfPicker.launch("application/pdf") 
                                        }
                                    ) {
                                        Icon(
                                            Icons.Default.Add,
                                            contentDescription = stringResource(id = R.string.attach_content),
                                            tint = BabyBlueDark
                                        )
                                    }
                                }
                                
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
                                        onCheckedChange = { 
                                            isEmergencyExam = it
                                            
                                            // Set tomorrow's date if emergency toggle is on
                                            if (it) {
                                                val calendar = Calendar.getInstance()
                                                calendar.add(Calendar.DAY_OF_YEAR, 1)
                                                calendar.set(Calendar.HOUR_OF_DAY, 0)
                                                calendar.set(Calendar.MINUTE, 0)
                                                calendar.set(Calendar.SECOND, 0)
                                                calendar.set(Calendar.MILLISECOND, 0)
                                                examDate = calendar.time
                                            }
                                        },
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
                            // Extract string resources before using them in the onClick
                            val emptyFieldsError = stringResource(id = R.string.error_empty_fields)
                            val dateRequiredError = stringResource(id = R.string.error_date_required)
                            
                            Button(
                                onClick = {
                                    // Validate form
                                    when {
                                        subjectName.isBlank() -> {
                                            // Show error
                                            examViewModel.setError(emptyFieldsError)
                                        }
                                        examDate == null -> {
                                            // Show error
                                            examViewModel.setError(dateRequiredError)
                                        }
                                        else -> {
                                            // Create exam
                                            examViewModel.createExam(
                                                subjectName = subjectName,
                                                examDate = examDate!!,
                                                isEmergency = isEmergencyExam,
                                                contentUri = pdfUri
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
                    val dateDialogState = rememberDatePickerState()
                    
                    DatePickerDialog(
                        onDismissRequest = { showDatePicker = false },
                        confirmButton = {
                            TextButton(onClick = {
                                dateDialogState.selectedDateMillis?.let {
                                    val calendar = Calendar.getInstance()
                                    calendar.timeInMillis = it
                                    examDate = calendar.time
                                }
                                showDatePicker = false
                            }) {
                                Text(stringResource(id = R.string.select_date))
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDatePicker = false }) {
                                Text(stringResource(id = R.string.cancel))
                            }
                        }
                    ) {
                        DatePicker(
                            state = dateDialogState,
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