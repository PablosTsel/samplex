package es.uc3m.android.samplex.ui.components

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * A custom Shape implementation that draws a star
 * @param points The number of points the star has
 * @param innerRadiusRatio The ratio of inner radius to outer radius (between 0 and 1)
 */
class StarShape(
    private val points: Int = 5,
    private val innerRadiusRatio: Float = 0.5f
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Generic(
            createStarPath(size.width, size.height)
        )
    }
    
    private fun createStarPath(width: Float, height: Float): Path {
        val outerRadius = minOf(width, height) / 2
        val innerRadius = outerRadius * innerRadiusRatio
        val centerX = width / 2
        val centerY = height / 2
        
        return Path().apply {
            moveTo(
                centerX + outerRadius * cos(0.0).toFloat(),
                centerY + outerRadius * sin(0.0).toFloat()
            )
            
            // Draw the star points
            val angleStep = 2f * PI.toFloat() / points
            for (i in 1 until 2 * points) {
                val radius = if (i % 2 == 0) outerRadius else innerRadius
                val angle = i * angleStep / 2
                
                lineTo(
                    centerX + radius * cos(angle).toFloat(),
                    centerY + radius * sin(angle).toFloat()
                )
            }
            
            close()
        }
    }
} 