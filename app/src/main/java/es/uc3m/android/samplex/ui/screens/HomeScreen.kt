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
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
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
import androidx.compose.ui.platform.LocalContext
import kotlin.random.Random

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
    
    // Motivational quotes
    val motivationalQuotes = listOf(
        "Study hard what interests you the most. That is where you will excel.",
        "The expert in anything was once a beginner.",
        "Success is the sum of small efforts, repeated day in and day out.",
        "The beautiful thing about learning is that no one can take it away from you.",
        "Education is the passport to the future, for tomorrow belongs to those who prepare for it today.",
        "The more that you read, the more things you will know. The more that you learn, the more places you'll go.",
        "The only way to do great work is to love what you do."
    )
    
    // Get today's date to select a quote
    val mainCalendar = Calendar.getInstance()
    val dayOfYear = mainCalendar.get(Calendar.DAY_OF_YEAR)
    val quoteIndex = dayOfYear % motivationalQuotes.size
    val todayQuote = motivationalQuotes[quoteIndex]
    
    // Simulated streak (in a real app, this would come from a data store)
    val studyStreak = remember { mutableIntStateOf(5) }
    
    // Generate days for the weekly calendar
    val weekCalendar = remember {
        val currentCalendar = Calendar.getInstance()
        val today = currentCalendar.get(Calendar.DAY_OF_WEEK)
        val days = mutableListOf<Pair<String, Date>>()
        
        // Adjust to start from Sunday if needed
        currentCalendar.add(Calendar.DAY_OF_WEEK, -today + 1)
        
        for (i in 0 until 7) {
            val dayName = SimpleDateFormat("EEE", Locale.getDefault()).format(currentCalendar.time)
            days.add(Pair(dayName, currentCalendar.time))
            currentCalendar.add(Calendar.DAY_OF_WEEK, 1)
        }
        days
    }

    var showProfileMenu by remember { mutableStateOf(false) }
    var showExamForm by remember { mutableStateOf(false) }
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

    if (showAccountScreen) {
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
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Motivational Quote Card
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = BabyBlueDark.copy(alpha = 0.1f)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = BabyBlueDark,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .padding(end = 8.dp)
                                )
                                
                                Text(
                                    text = todayQuote,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontStyle = FontStyle.Italic,
                                    color = LightText,
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )
                            }
                        }
                    }
                    
                    // Weekly Calendar View
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = LightSurface
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DateRange,
                                        contentDescription = null,
                                        tint = BabyBlueDark,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    
                                    Spacer(modifier = Modifier.width(8.dp))
                                    
                                    Text(
                                        text = "This Week",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = LightText
                                    )
                                }
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    val today = Calendar.getInstance().time
                                    val dateFormat = SimpleDateFormat("dd", Locale.getDefault())
                                    
                                    weekCalendar.forEach { (dayName, date) ->
                                        val isToday = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(date) == 
                                                      SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(today)
                                        
                                        // Check if this date has any exams
                                        val hasExam = exams.any { exam -> 
                                            val examCal = Calendar.getInstance().apply {
                                                time = exam.examDate
                                                set(Calendar.HOUR_OF_DAY, 0)
                                                set(Calendar.MINUTE, 0)
                                                set(Calendar.SECOND, 0)
                                                set(Calendar.MILLISECOND, 0)
                                            }
                                            
                                            val dateCal = Calendar.getInstance().apply {
                                                time = date
                                                set(Calendar.HOUR_OF_DAY, 0)
                                                set(Calendar.MINUTE, 0)
                                                set(Calendar.SECOND, 0)
                                                set(Calendar.MILLISECOND, 0)
                                            }
                                            
                                            examCal.get(Calendar.YEAR) == dateCal.get(Calendar.YEAR) &&
                                            examCal.get(Calendar.MONTH) == dateCal.get(Calendar.MONTH) &&
                                            examCal.get(Calendar.DAY_OF_MONTH) == dateCal.get(Calendar.DAY_OF_MONTH)
                                        }
                                        
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = dayName,
                                                color = if (isToday) BabyBlueDark else LightSecondaryText,
                                                fontSize = 12.sp
                                            )
                                            
                                            Box(
                                                modifier = Modifier
                                                    .padding(vertical = 4.dp)
                                                    .size(36.dp)
                                                    .background(
                                                        color = if (isToday) BabyBlueDark else Color.Transparent,
                                                        shape = CircleShape
                                                    ),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = dateFormat.format(date),
                                                    color = if (isToday) Color.White else LightText,
                                                    fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                                                )
                                            }
                                            
                                            // Show a dot indicator if there's an exam on this day
                                            if (hasExam) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(8.dp)
                                                        .background(
                                                            color = Color.Red.copy(alpha = 0.7f),
                                                            shape = CircleShape
                                                        )
                                                )
                                            } else {
                                                Spacer(modifier = Modifier.height(8.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    // Streak Counter
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = LightSurface
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = null,
                                    tint = BabyBlueDark,
                                    modifier = Modifier.size(48.dp)
                                )
                                
                                Spacer(modifier = Modifier.width(16.dp))
                                
                                Column {
                                    Text(
                                        text = "${studyStreak.intValue} Day Streak",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = LightText
                                    )
                                    
                                    Text(
                                        text = "Keep it up! You're doing great!",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = LightSecondaryText
                                    )
                                }
                            }
                        }
                    }
                    
                    // Exams section title
                    item {
                        Text(
                            text = stringResource(id = R.string.previous_ongoing_exams),
                            style = MaterialTheme.typography.titleMedium,
                            color = LightText,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp, horizontal = 4.dp)
                        )
                    }
                    
                    // Exams list
                    if (exams.isEmpty()) {
                        item {
                            // Enhanced empty state
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp, horizontal = 4.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color.LightGray.copy(alpha = 0.1f)
                                ),
                                border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f))
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DateRange,
                                        contentDescription = null,
                                        modifier = Modifier.size(48.dp),
                                        tint = BabyBlueDark.copy(alpha = 0.6f)
                                    )
                                    
                                    Spacer(modifier = Modifier.height(16.dp))
                                    
                                    Text(
                                        text = "No exams yet",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Medium,
                                        color = LightText
                                    )
                                    
                                    Spacer(modifier = Modifier.height(8.dp))
                                    
                                    Text(
                                        text = "Create your first exam roadmap using the + button",
                                        textAlign = TextAlign.Center,
                                        color = LightSecondaryText
                                    )
                                }
                            }
                        }
                    } else {
                        items(exams) { exam ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp, horizontal = 4.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color.White
                                ),
                                elevation = CardDefaults.cardElevation(2.dp),
                                onClick = {
                                    selectedExamId = exam.id
                                    showExamScreen = true
                                }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Circular icon with first letter
                                    Box(
                                        modifier = Modifier
                                            .size(50.dp)
                                            .background(
                                                if (exam.completed == true) Color(0xFF81C784) else BabyBlueDark, 
                                                CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = exam.displayName.take(1).uppercase(),
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 22.sp
                                        )
                                    }
                                    
                                    Spacer(modifier = Modifier.width(16.dp))
                                    
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = exam.displayName,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = Color.DarkGray,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        
                                        Text(
                                            text = "Tap to view roadmap",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = LightSecondaryText
                                        )
                                    }
                                    
                                    Icon(
                                        imageVector = Icons.Default.DateRange,
                                        contentDescription = null,
                                        tint = BabyBlueDark
                                    )
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
                            // Obtener contexto fuera del onClick
                            val context = LocalContext.current
                            
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
                                                context = context,
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