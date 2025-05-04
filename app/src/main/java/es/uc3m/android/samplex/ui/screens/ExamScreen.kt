package es.uc3m.android.samplex.ui.screens

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import es.uc3m.android.samplex.ui.components.RoadmapPath
import es.uc3m.android.samplex.ui.theme.BabyBlue
import es.uc3m.android.samplex.ui.theme.BabyBlueDark
import es.uc3m.android.samplex.ui.theme.LightBackground
import es.uc3m.android.samplex.ui.theme.LightText
import es.uc3m.android.samplex.viewmodel.ExamViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamScreen(
    examId: String,
    onBackClick: () -> Unit,
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
    
    val isLoading by examViewModel.isLoading.collectAsState()
    val error by examViewModel.error.collectAsState()
    val currentExam by examViewModel.currentExam.collectAsState()
    val roadmapDays by examViewModel.roadmapDays.collectAsState()
    
    // Fetch exam data when screen is displayed
    LaunchedEffect(examId) {
        examViewModel.fetchExamById(examId)
    }
    
    // Clean up when leaving screen
    DisposableEffect(Unit) {
        onDispose {
            examViewModel.clearCurrentExam()
        }
    }
    
    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier.shadow(2.dp),
                color = BabyBlue
            ) {
                TopAppBar(
                    title = { 
                        Text(
                            text = stringResource(id = R.string.exam_details),
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
            // Loading indicator
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = BabyBlueDark
                )
            } 
            // Error state
            else if (error != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .align(Alignment.Center),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Red.copy(alpha = 0.1f))
                ) {
                    Text(
                        text = error ?: "Unknown error",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        textAlign = TextAlign.Center,
                        color = Color.Red
                    )
                }
            }
            // Exam content
            else if (currentExam != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Exam title
                    Text(
                        text = currentExam?.displayName ?: "",
                        style = MaterialTheme.typography.headlineSmall,
                        color = LightText,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Check if content array is empty
                    if (currentExam?.content?.isEmpty() == true) {
                        // Show generating message for empty content
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.LightGray.copy(alpha = 0.3f)
                            )
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = stringResource(id = R.string.roadmap_generating),
                                    textAlign = TextAlign.Center,
                                    color = LightText
                                )
                            }
                        }
                    } else if (roadmapDays.isNotEmpty()) {
                        // Display roadmap
                        RoadmapPath(days = roadmapDays)
                    }
                }
            } else {
                // No exam loaded yet
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = BabyBlueDark)
                }
            }
        }
    }
} 