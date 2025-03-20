package es.uc3m.android.samplex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import es.uc3m.android.samplex.ui.theme.SamplexTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SamplexTheme {
                LuxuryHomeScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LuxuryHomeScreen() {
    // Premium color palette
    val goldGradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFFD4AF37),
            Color(0xFFF9F295),
            Color(0xFFD4AF37),
            Color(0xFFFFD700)
        )
    )

    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
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

    var showProfileMenu by remember { mutableStateOf(false) }
    var showExamForm by remember { mutableStateOf(false) }
    var subjectName by remember { mutableStateOf("") }
    var examDate by remember { mutableStateOf("") }
    var showExamsDropdown by remember { mutableStateOf(false) }

    val dropdownRotation by animateFloatAsState(
        targetValue = if (showExamsDropdown) 180f else 0f,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "dropdownRotation"
    )

    Scaffold(
        topBar = {
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
                                        color = Color(0xFFD4AF37)
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

                    // Profile button at the right
                    Box(
                        modifier = Modifier.align(Alignment.CenterEnd)
                    ) {
                        IconButton(
                            onClick = { showProfileMenu = true },
                            modifier = Modifier
                                .size(48.dp)
                                .shadow(4.dp, CircleShape)
                                .background(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            Color(0xFF1E293B),
                                            Color(0xFF0F172A)
                                        )
                                    ),
                                    shape = CircleShape
                                )
                                .border(
                                    BorderStroke(1.dp, Color(0xFFD4AF37)),
                                    CircleShape
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = stringResource(R.string.profile),
                                tint = Color(0xFFD4AF37),
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = showProfileMenu,
                            onDismissRequest = { showProfileMenu = false },
                            modifier = Modifier
                                .background(
                                    brush = cardBackground
                                )
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        "My Account",
                                        color = Color.White
                                    )
                                },
                                onClick = {
                                    showProfileMenu = false
                                    /* TODO: Navigate to account screen */
                                }
                            )
                            Divider(color = Color(0xFF334155))
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        "Settings",
                                        color = Color.White
                                    )
                                },
                                onClick = {
                                    showProfileMenu = false
                                    /* TODO: Navigate to settings screen */
                                }
                            )
                            Divider(color = Color(0xFF334155))
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        "Log Out",
                                        color = Color(0xFFD4AF37)
                                    )
                                },
                                onClick = {
                                    showProfileMenu = false
                                    /* TODO: Handle logout */
                                }
                            )
                        }
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
            if (showExamForm) {
                // Exam creation form with luxury styling
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Form title with premium styling
                    Text(
                        text = "Create New Exam Roadmap",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Light,
                            letterSpacing = 1.sp
                        ),
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 32.dp)
                    )

                    // Subject name field with luxury styling
                    OutlinedTextField(
                        value = subjectName,
                        onValueChange = { subjectName = it },
                        label = { Text("Subject Name", color = Color.White.copy(alpha = 0.7f)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFD4AF37),
                            unfocusedBorderColor = Color(0xFF94A3B8),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = Color(0xFFD4AF37),
                            focusedContainerColor = Color(0xFF1E293B),
                            unfocusedContainerColor = Color(0xFF1E293B)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )

                    // Exam date field with luxury styling
                    OutlinedTextField(
                        value = examDate,
                        onValueChange = { examDate = it },
                        label = { Text("Exam Date (DD/MM/YYYY)", color = Color.White.copy(alpha = 0.7f)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Select Date",
                                tint = Color(0xFFD4AF37),
                                modifier = Modifier.clickable {
                                    // TODO: Show date picker
                                }
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFD4AF37),
                            unfocusedBorderColor = Color(0xFF94A3B8),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = Color(0xFFD4AF37),
                            focusedContainerColor = Color(0xFF1E293B),
                            unfocusedContainerColor = Color(0xFF1E293B)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // PDF Drop zone with premium styling
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .shadow(16.dp, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(brush = cardBackground)
                                .clickable { /* Handle PDF selection */ }
                                .border(
                                    border = BorderStroke(
                                        width = 1.dp,
                                        color = Color(0xFFD4AF37).copy(alpha = 0.5f)
                                    ),
                                    shape = RoundedCornerShape(16.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .background(
                                            brush = Brush.radialGradient(
                                                colors = listOf(
                                                    Color(0xFF1E293B),
                                                    Color(0xFF0F172A)
                                                )
                                            ),
                                            shape = CircleShape
                                        )
                                        .border(
                                            width = 1.dp,
                                            brush = goldGradient,
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = null,
                                        tint = Color(0xFFD4AF37),
                                        modifier = Modifier.size(32.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = stringResource(R.string.upload_instruction),
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        letterSpacing = 0.5.sp,
                                        fontWeight = FontWeight.Light
                                    ),
                                    color = Color.White,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Buttons with premium styling
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Cancel button
                        val cancelInteractionSource = remember { MutableInteractionSource() }
                        val cancelIsPressed by cancelInteractionSource.collectIsPressedAsState()
                        val cancelScale by animateFloatAsState(
                            targetValue = if (cancelIsPressed) 0.95f else 1f,
                            label = "cancelScale"
                        )

                        OutlinedButton(
                            onClick = { showExamForm = false },
                            modifier = Modifier
                                .weight(1f)
                                .graphicsLayer {
                                    scaleX = cancelScale
                                    scaleY = cancelScale
                                },
                            interactionSource = cancelInteractionSource,
                            border = BorderStroke(1.dp, Color(0xFFD4AF37)),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Color.Transparent,
                                contentColor = Color(0xFFD4AF37)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                "Cancel",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    letterSpacing = 1.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }

                        // Create button
                        val createInteractionSource = remember { MutableInteractionSource() }
                        val createIsPressed by createInteractionSource.collectIsPressedAsState()
                        val createScale by animateFloatAsState(
                            targetValue = if (createIsPressed) 0.95f else 1f,
                            label = "createScale"
                        )

                        Button(
                            onClick = {
                                // TODO: Save exam roadmap
                                showExamForm = false
                            },
                            modifier = Modifier
                                .weight(1f)
                                .graphicsLayer {
                                    scaleX = createScale
                                    scaleY = createScale
                                },
                            interactionSource = createInteractionSource,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFD4AF37),
                                contentColor = Color(0xFF0F172A)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                "Create",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    letterSpacing = 1.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }
                }
            } else {
                // Main screen with "Create Exam Roadmap" button with luxury styling
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Instruction text with premium styling
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
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
                            Text(
                                text = stringResource(R.string.welcome_title),
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    letterSpacing = 0.5.sp,
                                    fontWeight = FontWeight.Light,
                                    lineHeight = 32.sp
                                ),
                                textAlign = TextAlign.Center,
                                color = Color.White,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(48.dp))

                    // Create Exam Roadmap button with luxury styling
                    val interactionSource = remember { MutableInteractionSource() }
                    val isPressed by interactionSource.collectIsPressedAsState()
                    val scale by animateFloatAsState(
                        targetValue = if (isPressed) 0.95f else 1f,
                        label = "buttonScale"
                    )

                    Box(
                        modifier = Modifier
                            .size(200.dp)
                            .shadow(16.dp, CircleShape)
                            .graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                            }
                            .clip(CircleShape)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xFF1E293B),
                                        Color(0xFF0F172A)
                                    )
                                )
                            )
                            .border(
                                BorderStroke(1.dp, Color(0xFFD4AF37)),
                                CircleShape
                            )
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null
                            ) {
                                showExamForm = true
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .background(
                                        Color(0xFF0F172A),
                                        CircleShape
                                    )
                                    .border(
                                        width = 1.dp,
                                        brush = goldGradient,
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add",
                                    modifier = Modifier.size(36.dp),
                                    tint = Color(0xFFD4AF37)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Create\nExam Roadmap",
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Light,
                                    letterSpacing = 1.sp,
                                    lineHeight = 24.sp
                                ),
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Click to create a new exam roadmap",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            letterSpacing = 0.5.sp,
                            fontWeight = FontWeight.Light
                        ),
                        color = Color.White.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    // Previous/Ongoing Exams dropdown with luxury styling
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                            .shadow(16.dp, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(brush = cardBackground)
                        ) {
                            // Dropdown header with luxury styling
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showExamsDropdown = !showExamsDropdown }
                                    .padding(20.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Previous/Ongoing Exams",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        letterSpacing = 0.5.sp,
                                        fontWeight = FontWeight.Light
                                    ),
                                    color = Color.White
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Expand",
                                    tint = Color(0xFFD4AF37),
                                    modifier = Modifier
                                        .rotate(dropdownRotation)
                                        .size(28.dp)
                                )
                            }

                            // Dropdown content with luxury styling and animation
                            AnimatedVisibility(
                                visible = showExamsDropdown,
                                enter = fadeIn() + expandVertically(),
                                exit = fadeOut() + shrinkVertically()
                            ) {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Divider(color = Color(0xFF334155))
                                    // Example exams
                                    listOf("Geography", "History", "Literature").forEach { subject ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable { /* TODO: Navigate to exam details */ }
                                                .padding(20.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = subject,
                                                style = MaterialTheme.typography.bodyLarge.copy(
                                                    fontWeight = FontWeight.Light
                                                ),
                                                color = Color.White
                                            )
                                            Text(
                                                text = "View",
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    fontWeight = FontWeight.Medium,
                                                    letterSpacing = 0.5.sp
                                                ),
                                                color = Color(0xFFD4AF37)
                                            )
                                        }
                                        if (subject != "Literature") {  // Don't add divider after the last item
                                            Divider(
                                                color = Color(0xFF334155),
                                                modifier = Modifier.padding(horizontal = 20.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LuxuryHomeScreenPreview() {
    SamplexTheme {
        LuxuryHomeScreen()
    }
}