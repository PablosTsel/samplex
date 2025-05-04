package es.uc3m.android.samplex.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import es.uc3m.android.samplex.model.RoadmapDay
import es.uc3m.android.samplex.ui.theme.BabyBlueDark
import es.uc3m.android.samplex.ui.components.StarShape

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
    position: Int // 0 for left, 1 for right
) {
    val isEven = position % 2 == 0
    
    // Determine color based on completion status and today
    val nodeColor = when {
        day.isToday() -> DayPurpleColor    // Today is purple
        day.isCompleted -> DayGreenColor   // Completed days are green
        else -> DayBlueColor               // Future/incomplete days are blue
    }
    
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
                        .background(StarYellowColor), // Always yellow for final day
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
                        .background(nodeColor),
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
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        days.forEachIndexed { index, day ->
            // Render day node
            RoadmapDayNode(
                day = day,
                isFirst = index == 0,
                position = index
            )
            
            // Render connecting path (except for the last day)
            if (index < days.size - 1) {
                // A path is considered "past" if the current day is completed
                val isPastConnection = day.isCompleted
                
                ConnectingPath(
                    isPastConnection = isPastConnection,
                    isLeft = index % 2 == 0
                )
            }
        }
    }
} 