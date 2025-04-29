package es.uc3m.android.samplex.utils

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * Utility class containing common UI components and constants used throughout the app.
 */
object UiUtils {
    
    /**
     * Common colors used in the app
     */
    object Colors {
        val goldColor = Color(0xFFD4AF37)
        val darkBlue1 = Color(0xFF1A1A2E)
        val darkBlue2 = Color(0xFF16213E)
        val darkBlue3 = Color(0xFF0F3460)
        val darkNavy1 = Color(0xFF1E293B)
        val darkNavy2 = Color(0xFF0F172A)
        
        val textPrimary = Color.White
        val textSecondary = Color.White.copy(alpha = 0.7f)
        val borderColor = Color(0xFF94A3B8)
        val dividerColor = Color(0xFF334155)
        val successColor = Color(0xFF15803D)
        val errorColor = Color(0xFFDC2626)
    }
    
    /**
     * Common gradients used in the app
     */
    object Gradients {
        val backgroundGradient = Brush.verticalGradient(
            colors = listOf(
                Colors.darkBlue1,
                Colors.darkBlue2,
                Colors.darkBlue3
            )
        )
        
        val cardBackground = Brush.linearGradient(
            colors = listOf(
                Colors.darkNavy1.copy(alpha = 0.9f),
                Colors.darkNavy2.copy(alpha = 0.9f)
            )
        )
        
        val goldGradient = Brush.linearGradient(
            colors = listOf(
                Colors.goldColor,
                Color(0xFFF9F295),
                Colors.goldColor,
                Color(0xFFFFD700)
            )
        )
    }
    
    /**
     * Common dimensions used in the app
     */
    object Dimensions {
        // Corner radiuses
        const val CORNER_RADIUS_SMALL = 8
        const val CORNER_RADIUS_MEDIUM = 16
        const val CORNER_RADIUS_LARGE = 24
        
        // Spacing
        const val SPACING_SMALL = 8
        const val SPACING_MEDIUM = 16
        const val SPACING_LARGE = 24
        const val SPACING_XLARGE = 32
        
        // Icon sizes
        const val ICON_SIZE_SMALL = 16
        const val ICON_SIZE_MEDIUM = 24
        const val ICON_SIZE_LARGE = 32
        const val ICON_SIZE_XLARGE = 48
    }
    
    /**
     * Animation constants
     */
    object Animations {
        const val ANIMATION_DURATION_SHORT = 300
        const val ANIMATION_DURATION_MEDIUM = 500
        const val ANIMATION_DURATION_LONG = 800
    }
} 