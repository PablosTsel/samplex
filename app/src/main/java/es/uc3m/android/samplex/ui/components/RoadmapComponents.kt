package es.uc3m.android.samplex.ui.components

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import es.uc3m.android.samplex.model.RoadmapDay
import es.uc3m.android.samplex.ui.theme.BabyBlueDark
import es.uc3m.android.samplex.ui.components.StarShape
import es.uc3m.android.samplex.viewmodel.ExamViewModel
import kotlinx.coroutines.tasks.await

// Colors
private val DayBlueColor = BabyBlueDark
private val DayPurpleColor = Color(0xFF9C27B0) // Changed from red to purple
private val DayGreenColor = Color(0xFF81C784)
private val StarYellowColor = Color(0xFFFFD54F)
private val PathGreyColor = Color(0xFF9E9E9E)
private val PathGreenColor = Color(0xFF81C784)

@Composable
fun RoadmapDayNode(
    day: RoadmapDay,
    isFirst: Boolean,
    position: Int, // 0 for left, 1 for right
    onDayClick: (RoadmapDay) -> Unit
) {
    val isEven = position % 2 == 0
    
    // Determine color based on completion status and today
    // Changed priority: completed takes precedence over today
    val nodeColor = when {
        day.isCompleted -> DayGreenColor   // Completed days are green (highest priority)
        day.isToday() -> DayPurpleColor    // Today is purple (only if not completed)
        else -> DayBlueColor               // Future/incomplete days are blue
    }
    
    // For final days, we'll keep the star shape but still respect the color priority
    val finalDayColor = if (day.isCompleted) {
        DayGreenColor  // Final completed days are still green
    } else {
        StarYellowColor // Final non-completed days keep the yellow color
    }
    
    // Check if the day is clickable (past days or today)
    val isClickable = day.isCompleted || day.isToday() || day.isPast()
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .align(if (isEven) Alignment.CenterStart else Alignment.CenterEnd)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = if (isEven) Arrangement.Start else Arrangement.End
        ) {
            // Day node
            if (day.final) {
                // Star shape for final day - make it larger
                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .clip(StarShape(5, 0.5f))
                        .background(finalDayColor) // Use conditional color for final days
                        .let { mod ->
                            if (isClickable) {
                                mod.clickable { onDayClick(day) }
                            } else {
                                mod
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = day.getFormattedDate(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }
            } else {
                // Circle for regular day
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(nodeColor)
                        .let { mod ->
                            if (isClickable) {
                                mod.clickable { onDayClick(day) }
                            } else {
                                mod
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = day.getFormattedDate(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun ConnectingPath(
    isPastConnection: Boolean,
    isLeft: Boolean
) {
    val pathColor = if (isPastConnection) PathGreenColor else PathGreyColor
    
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
    ) {
        val width = size.width
        val height = size.height
        
        val path = Path().apply {
            if (isLeft) {
                // Left to right curve - much more pronounced curve
                moveTo(width * 0.2f, 0f)
                cubicTo(
                    width * 0.2f, height * 0.6f,  // More extreme control point 1
                    width * 0.8f, height * 0.4f,  // More extreme control point 2
                    width * 0.8f, height          // End point remains the same
                )
            } else {
                // Right to left curve - much more pronounced curve
                moveTo(width * 0.8f, 0f)
                cubicTo(
                    width * 0.8f, height * 0.6f,  // More extreme control point 1
                    width * 0.2f, height * 0.4f,  // More extreme control point 2
                    width * 0.2f, height          // End point remains the same
                )
            }
        }
        
        // Draw footprints effect with thicker lines
        val pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f)
        drawPath(
            path = path,
            color = pathColor,
            style = Stroke(width = 8f, pathEffect = pathEffect)
        )
    }
}

@Composable
fun RoadmapPath(days: List<RoadmapDay>) {
    var selectedDay by remember { mutableStateOf<RoadmapDay?>(null) }
    var showActivitiesDialog by remember { mutableStateOf(false) }
    var reloadRequired by remember { mutableStateOf(false) }
    var showCongratulationsDialog by remember { mutableStateOf(false) }
    
    // Use rememberUpdatedState to force recomposition when the days list changes
    val currentDays by rememberUpdatedState(days)
    
    // Keep track of last completed day for forcing updates
    var lastCompletedDayIndex by remember { mutableStateOf(-1) }
    
    // Initialize the viewModel at the Composable level
    val examViewModel: ExamViewModel = viewModel()
    
    // Handle reload after completion
    LaunchedEffect(reloadRequired) {
        if (reloadRequired) {
            // Fetch updated data using the viewModel initialized above
            selectedDay?.examId?.let { examId ->
                // Make a direct Firestore query to get the latest data immediately
                val db = FirebaseFirestore.getInstance()
                try {
                    val snapshot = db.collection("exams").document(examId).get().await()
                    if (snapshot.exists()) {
                        // Update the last completed day in the current list to show as completed
                        if (lastCompletedDayIndex >= 0 && lastCompletedDayIndex < currentDays.size) {
                            val updatedDay = currentDays[lastCompletedDayIndex].copy(isCompleted = true)
                            val mutableDays = currentDays.toMutableList()
                            mutableDays[lastCompletedDayIndex] = updatedDay
                            // This triggers the view model to update its internal state
                            examViewModel.updateRoadmapDaysManually(mutableDays)
                        }
                        // Also fetch from the server to ensure everything is in sync
                        examViewModel.fetchExamById(examId)
                    }
                } catch (e: Exception) {
                    Log.e("RoadmapPath", "Error fetching updated exam data: ${e.message}")
                    // Fall back to regular fetch
                    examViewModel.fetchExamById(examId)
                }
            }
            reloadRequired = false
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        currentDays.forEachIndexed { index, day ->
            // Render day node
            RoadmapDayNode(
                day = day,
                isFirst = index == 0,
                position = index,
                onDayClick = { clickedDay ->
                    selectedDay = clickedDay
                    showActivitiesDialog = true
                }
            )
            
            // Render connecting path (except for the last day)
            if (index < currentDays.size - 1) {
                // A path is considered "past" if the current day is completed
                val isPastConnection = day.isCompleted
                
                ConnectingPath(
                    isPastConnection = isPastConnection,
                    isLeft = index % 2 == 0
                )
            }
        }
    }
    
    // Show congratulations dialog if a final day was completed
    if (showCongratulationsDialog) {
        CongratulationsDialog(
            onDismiss = {
                showCongratulationsDialog = false
            }
        )
    }
    
    // Show dialog if a day is selected
    if (showActivitiesDialog && selectedDay != null) {
        val currentSelectedDay = selectedDay!!
        val dayIndex = currentDays.indexOf(currentSelectedDay)
        
        DayActivitiesDialog(
            day = currentSelectedDay,
            onDismiss = { 
                showActivitiesDialog = false
                selectedDay = null
            },
            onDayCompleted = { isFinalDay ->
                // Store the index of the day that was just completed
                lastCompletedDayIndex = dayIndex
                
                // Set flag to reload data
                reloadRequired = true
                
                // Close dialog
                showActivitiesDialog = false
                selectedDay = null
                
                // Show congratulations if it was the final day
                if (isFinalDay) {
                    showCongratulationsDialog = true
                }
            }
        )
    }
}

@Composable
fun DayActivitiesDialog(
    day: RoadmapDay,
    onDismiss: () -> Unit,
    onDayCompleted: (Boolean) -> Unit
) {
    val activities = day.actividades
    var showIntro by remember { mutableStateOf(true) }
    var currentActivityIndex by remember { mutableIntStateOf(0) }
    var canNavigateNext by remember { mutableStateOf(false) }
    val db = FirebaseFirestore.getInstance()
    val scrollState = rememberScrollState()
    
    // Function to determine if we can navigate to next activity
    fun checkCanNavigateNext(activityType: String, hasSubmitted: Boolean) {
        canNavigateNext = when (activityType) {
            "Resumen" -> true
            "Quiz", "Pregunta" -> hasSubmitted
            else -> true
        }
    }
    
    // Reset scroll position when activity changes
    LaunchedEffect(currentActivityIndex) {
        scrollState.scrollTo(0)
    }
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Close button (X) in the top-right corner
                Box(modifier = Modifier.fillMaxWidth()) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.Gray
                        )
                    }
                }
                
                if (showIntro) {
                    // Intro screen
                    IntroContent(
                        activities = activities,
                        onStartClick = { 
                            showIntro = false
                            // For Resumen type, we can navigate next immediately
                            if (activities.isNotEmpty()) {
                                val firstType = activities.firstOrNull()?.get("tipo") as? String ?: ""
                                checkCanNavigateNext(firstType, false)
                            }
                        }
                    )
                } else {
                    // Activity content
                    if (activities.isNotEmpty()) {
                        val activity = activities[currentActivityIndex]
                        val activityType = activity["tipo"] as? String ?: ""
                        
                        // Check if activity has been submitted using a simpler approach
                        LaunchedEffect(currentActivityIndex) {
                            if (activityType == "Quiz" || activityType == "Pregunta") {
                                // Check if this activity already has an answer stored in the exam document
                                if (day.examId.isNotEmpty()) {
                                    db.collection("exams").document(day.examId).get()
                                        .addOnSuccessListener { document ->
                                            if (document.exists()) {
                                                val content = document.get("content") as? List<Map<String, Any>> ?: emptyList()
                                                
                                                if (content.size > day.dayIndex) {
                                                    val dayMap = content[day.dayIndex] as? Map<String, Any> ?: emptyMap()
                                                    val activities = dayMap["actividades"] as? List<Map<String, Any>> ?: emptyList()
                                                    
                                                    if (activities.size > currentActivityIndex) {
                                                        val activityMap = activities[currentActivityIndex] as? Map<String, Any> ?: emptyMap()
                                                        
                                                        // Check for answer based on activity type
                                                        val hasAnswer = if (activityType == "Quiz") {
                                                            activityMap.containsKey("user_answer")
                                                        } else {
                                                            activityMap.containsKey("answer")
                                                        }
                                                        
                                                        // If there's an answer, we can navigate forward
                                                        checkCanNavigateNext(activityType, hasAnswer)
                                                    }
                                                }
                                            }
                                        }
                                        .addOnFailureListener { e ->
                                            Log.e("DayActivitiesDialog", "Error checking activity answers: ${e.message}")
                                            checkCanNavigateNext(activityType, false)
                                        }
                                } else {
                                    checkCanNavigateNext(activityType, false)
                                }
                            } else {
                                // For Resumen, always allow navigation
                                checkCanNavigateNext(activityType, true)
                            }
                        }
                        
                        // Content area - scrollable if needed
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        ) {
                            // Display current activity
                            ActivityContent(
                                activity = activity,
                                examId = day.examId,
                                dayIndex = day.dayIndex,
                                activityIndex = currentActivityIndex,
                                db = db,
                                scrollState = scrollState,
                                onSubmitComplete = { 
                                    // When Quiz or Question is submitted, enable Next navigation
                                    checkCanNavigateNext(activityType, true)
                                }
                            )
                        }
                        
                        // Clear separation between content and navigation buttons
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        // Navigation buttons in clearly separated section
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = Color(0xFFF5F5F5),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                // Back button (not shown for first activity)
                                if (currentActivityIndex > 0) {
                                    Button(onClick = { currentActivityIndex-- }) {
                                        Text("Back")
                                    }
                                } else {
                                    Spacer(modifier = Modifier.width(88.dp)) // Width of a Button
                                }
                                
                                // Counter showing current position
                                Text(
                                    text = "${currentActivityIndex + 1}/${activities.size}",
                                    modifier = Modifier.align(Alignment.CenterVertically)
                                )
                                
                                // Next or Finish button
                                if (currentActivityIndex < activities.size - 1) {
                                    Button(
                                        onClick = { currentActivityIndex++ },
                                        enabled = canNavigateNext
                                    ) {
                                        Text("Next")
                                    }
                                } else {
                                    // Show Exit if already completed, otherwise show Finish
                                    Button(
                                        onClick = {
                                            // If not completed, mark day as completed in Firestore
                                            if (!day.isCompleted && day.examId.isNotEmpty()) {
                                                // First create a local copy with the completed flag set to true
                                                // This allows the UI to update immediately
                                                val updatedDay = day.copy(isCompleted = true)
                                                
                                                // Then update the database
                                                db.collection("exams").document(day.examId)
                                                    .get()
                                                    .addOnSuccessListener { document ->
                                                        if (document != null && document.exists()) {
                                                            val content = document.get("content") as? List<Map<String, Any>> ?: emptyList()
                                                            if (content.size > day.dayIndex) {
                                                                val updatedContent = content.toMutableList()
                                                                val dayMap = updatedContent[day.dayIndex] as? MutableMap<String, Any> ?: mutableMapOf()
                                                                dayMap["completed"] = true
                                                                updatedContent[day.dayIndex] = dayMap
                                                                
                                                                db.collection("exams").document(day.examId)
                                                                    .update("content", updatedContent)
                                                                    .addOnSuccessListener {
                                                                        Log.d("DayActivitiesDialog", "Day marked as completed successfully")
                                                                        
                                                                        // If this is the final day, also mark the exam as completed in the user document
                                                                        if (day.final) {
                                                                            // Get the userId from the exam document
                                                                            val userId = document.getString("userId")
                                                                            if (userId != null) {
                                                                                // Find the exam in the user's exams array and update it
                                                                                db.collection("users").document(userId)
                                                                                    .get()
                                                                                    .addOnSuccessListener { userDoc ->
                                                                                        if (userDoc.exists()) {
                                                                                            val exams = userDoc.get("exams") as? List<Map<String, Any>> ?: emptyList()
                                                                                            val updatedExams = exams.map { exam ->
                                                                                                if (exam["id"] == day.examId) {
                                                                                                    // Add completed=true to this exam
                                                                                                    val updatedExam = exam.toMutableMap()
                                                                                                    updatedExam["completed"] = true
                                                                                                    updatedExam
                                                                                                } else {
                                                                                                    exam
                                                                                                }
                                                                                            }
                                                                                            
                                                                                            // Update the user document
                                                                                            db.collection("users").document(userId)
                                                                                                .update("exams", updatedExams)
                                                                                                .addOnSuccessListener {
                                                                                                    Log.d("DayActivitiesDialog", "Exam marked as completed in user document")
                                                                                                }
                                                                                                .addOnFailureListener { e ->
                                                                                                    Log.e("DayActivitiesDialog", "Error updating user document: ${e.message}")
                                                                                                }
                                                                                        }
                                                                                    }
                                                                                    .addOnFailureListener { e ->
                                                                                        Log.e("DayActivitiesDialog", "Error fetching user document: ${e.message}")
                                                                                    }
                                                                            }
                                                                        }
                                                                    }
                                                            }
                                                        }
                                                    }
                                            }
                                            // Notify completion and pass the final status
                                            onDayCompleted(day.final)
                                        },
                                        enabled = canNavigateNext
                                    ) {
                                        Text(if (day.isCompleted) "Exit" else "Finish")
                                    }
                                }
                            }
                        }
                    } else {
                        Text(
                            text = "No activities for today",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun IntroContent(
    activities: List<Map<String, Any>>,
    onStartClick: () -> Unit
) {
    // Count activities by type
    var summaryCount = 0
    var quizCount = 0
    var questionCount = 0
    
    // Add logging to see what we're working with
    Log.d("IntroContent", "Activities size: ${activities.size}")
    activities.forEachIndexed { index, activity ->
        Log.d("IntroContent", "Activity $index type: ${activity["tipo"]}")
        
        when (activity["tipo"] as? String) {
            "Resumen" -> summaryCount++
            "Quiz" -> quizCount++
            "Pregunta" -> questionCount++
        }
    }
    
    // Format the introduction text
    val intro = StringBuilder("Hey! For today, you've got ")
    
    if (summaryCount > 0) {
        intro.append("$summaryCount ${if (summaryCount == 1) "summary" else "summaries"} to go over")
        
        if (quizCount > 0 && questionCount > 0) {
            intro.append(", ")
        } else if (quizCount > 0 || questionCount > 0) {
            intro.append(" and ")
        }
    }
    
    if (quizCount > 0) {
        intro.append("$quizCount ${if (quizCount == 1) "multiple choice question" else "multiple choice questions"} to tackle")
        
        if (questionCount > 0) {
            intro.append(", and ")
        }
    }
    
    if (questionCount > 0) {
        intro.append("$questionCount ${if (questionCount == 1) "long answer" else "long answers"} to nail")
    }
    
    intro.append(".")
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = intro.toString(),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(16.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(onClick = onStartClick) {
            Text("Start!")
        }
    }
}

@Composable
fun ActivityContent(
    activity: Map<String, Any>,
    examId: String,
    dayIndex: Int,
    activityIndex: Int,
    db: FirebaseFirestore,
    scrollState: ScrollState,
    onSubmitComplete: () -> Unit
) {
    val activityType = activity["tipo"] as? String ?: ""
    
    // Add logging to debug activity content
    Log.d("ActivityContent", "Activity type: $activityType")
    activity.forEach { (key, value) ->
        Log.d("ActivityContent", "Key: $key, Value: $value")
    }
    
    when (activityType) {
        "Resumen" -> {
            // Wrap SummaryActivity in a scrollable Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
            ) {
                SummaryActivity(activity)
            }
        }
        "Quiz" -> QuizActivity(activity, examId, dayIndex, activityIndex, db, scrollState, onSubmitComplete)
        "Pregunta" -> QuestionActivity(activity, examId, dayIndex, activityIndex, db, scrollState, onSubmitComplete)
        else -> Text("Unknown activity type: $activityType")
    }
}

@Composable
fun SummaryActivity(
    activity: Map<String, Any>
) {
    val title = activity["titulo"] as? String ?: "Summary"
    val content = activity["contenido"] as? String ?: ""
    
    // Replace \n with actual line breaks for display
    val formattedContent = content.replace("\\n", "\n")
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Text(
            text = formattedContent,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )
    }
}

@Composable
fun QuizActivity(
    activity: Map<String, Any>,
    examId: String,
    dayIndex: Int,
    activityIndex: Int,
    db: FirebaseFirestore,
    scrollState: ScrollState,
    onSubmitComplete: () -> Unit
) {
    val question = activity["pregunta"] as? String ?: "Question"
    val optionA = activity["opcion_a"] as? String ?: "Option A"
    val optionB = activity["opcion_b"] as? String ?: "Option B"
    val optionC = activity["opcion_c"] as? String ?: "Option C"
    val optionD = activity["opcion_d"] as? String ?: "Option D"
    val correctAnswer = activity["respuesta_correcta"] as? String ?: "a"
    
    // Get user_answer from the activity if it exists
    val savedUserAnswer = activity["user_answer"] as? String ?: ""
    
    // State for user selection and submission - use activity-specific key to prevent selections persisting
    var selectedOption by remember(activityIndex) { mutableStateOf(savedUserAnswer) }
    var hasSubmitted by remember(activityIndex) { mutableStateOf(savedUserAnswer.isNotEmpty()) }
    
    // If already answered, enable next navigation
    LaunchedEffect(activityIndex) {
        if (hasSubmitted) {
            onSubmitComplete()
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
            .padding(bottom = 16.dp)
    ) {
        Text(
            text = question,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // Option A
        OptionRow(
            option = "a",
            text = optionA,
            selected = selectedOption == "a",
            correct = correctAnswer == "a",
            showResult = hasSubmitted,
            onSelect = { if (!hasSubmitted) selectedOption = "a" }
        )
        
        // Option B
        OptionRow(
            option = "b",
            text = optionB,
            selected = selectedOption == "b",
            correct = correctAnswer == "b",
            showResult = hasSubmitted,
            onSelect = { if (!hasSubmitted) selectedOption = "b" }
        )
        
        // Option C
        OptionRow(
            option = "c",
            text = optionC,
            selected = selectedOption == "c",
            correct = correctAnswer == "c",
            showResult = hasSubmitted,
            onSelect = { if (!hasSubmitted) selectedOption = "c" }
        )
        
        // Option D
        OptionRow(
            option = "d",
            text = optionD,
            selected = selectedOption == "d",
            correct = correctAnswer == "d",
            showResult = hasSubmitted,
            onSelect = { if (!hasSubmitted) selectedOption = "d" }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Submit button - only show if not submitted yet and an option is selected
        if (!hasSubmitted && selectedOption.isNotEmpty()) {
            Button(
                onClick = {
                    hasSubmitted = true
                    
                    // Save answer directly to the exam document
                    if (examId.isNotEmpty()) {
                        // Get the current exam document
                        db.collection("exams").document(examId).get()
                            .addOnSuccessListener { document ->
                                if (document.exists()) {
                                    // Get the content array
                                    val content = document.get("content") as? List<Map<String, Any>> ?: emptyList()
                                    
                                    if (content.size > dayIndex) {
                                        // Get the day's map and activities
                                        val updatedContent = content.toMutableList()
                                        val dayMap = updatedContent[dayIndex] as? MutableMap<String, Any> ?: mutableMapOf()
                                        val activities = dayMap["actividades"] as? MutableList<Map<String, Any>> ?: mutableListOf()
                                        
                                        if (activities.size > activityIndex) {
                                            // Update the activity with the user's answer
                                            val updatedActivities = activities.toMutableList()
                                            val activityMap = updatedActivities[activityIndex] as? MutableMap<String, Any> ?: mutableMapOf()
                                            
                                            // Add user answer fields
                                            activityMap["user_answer"] = selectedOption
                                            activityMap["is_correct"] = (selectedOption == correctAnswer)
                                            
                                            // Update the activity in the list
                                            updatedActivities[activityIndex] = activityMap
                                            
                                            // Update the day map
                                            dayMap["actividades"] = updatedActivities
                                            updatedContent[dayIndex] = dayMap
                                            
                                            // Update the exam document
                                            db.collection("exams").document(examId)
                                                .update("content", updatedContent)
                                                .addOnSuccessListener {
                                                    Log.d("QuizActivity", "Answer saved successfully")
                                                }
                                                .addOnFailureListener { e ->
                                                    Log.e("QuizActivity", "Error saving answer: ${e.message}")
                                                }
                                        }
                                    }
                                }
                            }
                    }
                    
                    onSubmitComplete()
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            ) {
                Text("Submit")
            }
        }
    }
}

@Composable
fun OptionRow(
    option: String,
    text: String,
    selected: Boolean,
    correct: Boolean,
    showResult: Boolean,
    onSelect: () -> Unit
) {
    val backgroundColor = when {
        showResult && correct -> Color(0xFFDCEDC8) // Light green
        showResult && selected && !correct -> Color(0xFFFFCDD2) // Light red
        selected -> Color(0xFFE3F2FD) // Light blue
        else -> Color.Transparent
    }
    
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onSelect),
        color = backgroundColor,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = selected,
                onClick = onSelect
            )
            
            Text(
                text = "$option. $text",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(start = 8.dp)
            )
            
            if (showResult && correct) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Correct",
                    tint = Color.Green,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}

@Composable
fun QuestionActivity(
    activity: Map<String, Any>,
    examId: String,
    dayIndex: Int,
    activityIndex: Int,
    db: FirebaseFirestore,
    scrollState: ScrollState,
    onSubmitComplete: () -> Unit
) {
    val question = activity["pregunta"] as? String ?: "Question"
    
    // Get answer from the activity if it exists
    val savedAnswer = activity["answer"] as? String ?: ""
    
    // State for user answer and submission - use activity-specific key to prevent answers persisting
    var answer by remember(activityIndex) { mutableStateOf(savedAnswer) }
    var hasSubmitted by remember(activityIndex) { mutableStateOf(savedAnswer.isNotEmpty()) }
    
    // If already answered, enable next navigation
    LaunchedEffect(activityIndex) {
        if (hasSubmitted) {
            onSubmitComplete()
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
            .padding(bottom = 16.dp)
    ) {
        // Use normal text style for question instead of headline
        Text(
            text = question,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        if (hasSubmitted) {
            // Show saved answer
            Text(
                text = "Your answer:",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 8.dp)
            )
            
            Text(
                text = answer,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .background(Color(0xFFE3F2FD), RoundedCornerShape(8.dp))
                    .padding(16.dp)
            )
        } else {
            // Text field for answer
            TextField(
                value = answer,
                onValueChange = { answer = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                placeholder = { Text("Enter your answer here...") }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Submit button - only show if not submitted yet and the answer is not empty
            Button(
                onClick = {
                    if (answer.isNotEmpty()) {
                        hasSubmitted = true
                        
                        // Save answer directly to the exam document
                        if (examId.isNotEmpty()) {
                            // Get the current exam document
                            db.collection("exams").document(examId).get()
                                .addOnSuccessListener { document ->
                                    if (document.exists()) {
                                        // Get the content array
                                        val content = document.get("content") as? List<Map<String, Any>> ?: emptyList()
                                        
                                        if (content.size > dayIndex) {
                                            // Get the day's map and activities
                                            val updatedContent = content.toMutableList()
                                            val dayMap = updatedContent[dayIndex] as? MutableMap<String, Any> ?: mutableMapOf()
                                            val activities = dayMap["actividades"] as? MutableList<Map<String, Any>> ?: mutableListOf()
                                            
                                            if (activities.size > activityIndex) {
                                                // Update the activity with the user's answer
                                                val updatedActivities = activities.toMutableList()
                                                val activityMap = updatedActivities[activityIndex] as? MutableMap<String, Any> ?: mutableMapOf()
                                                
                                                // Add answer field
                                                activityMap["answer"] = answer
                                                
                                                // Update the activity in the list
                                                updatedActivities[activityIndex] = activityMap
                                                
                                                // Update the day map
                                                dayMap["actividades"] = updatedActivities
                                                updatedContent[dayIndex] = dayMap
                                                
                                                // Update the exam document
                                                db.collection("exams").document(examId)
                                                    .update("content", updatedContent)
                                                    .addOnSuccessListener {
                                                        Log.d("QuestionActivity", "Answer saved successfully")
                                                    }
                                                    .addOnFailureListener { e ->
                                                        Log.e("QuestionActivity", "Error saving answer: ${e.message}")
                                                    }
                                            }
                                        }
                                    }
                                }
                        }
                        
                        onSubmitComplete()
                    }
                },
                modifier = Modifier
                    .align(Alignment.End),
                enabled = answer.isNotEmpty()
            ) {
                Text("Submit")
            }
        }
    }
}

@Composable
fun CongratulationsDialog(
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Congratulations!",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4CAF50), // Green color
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                Text(
                    text = "You have completed the study plan! Good luck in your exam, if you need any extra study, please look again at the past tasks.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
                
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Close")
                }
            }
        }
    }
} 