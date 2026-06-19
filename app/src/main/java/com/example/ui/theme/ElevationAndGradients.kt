package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.softCardShadow(
    radius: Dp = 16.dp,
    elevation: Dp = 8.dp,
    color: Color = Color.Black
): Modifier = this.shadow(
    elevation = elevation,
    shape = RoundedCornerShape(radius),
    ambientColor = color.copy(alpha = 0.08f),
    spotColor = color.copy(alpha = 0.12f),
    clip = false
)

object AppGradients {
    val HeroGradient = Brush.linearGradient(
        colors = listOf(
            Slate900,
            Slate850,
            BrandTeal.copy(alpha = 0.18f)
        )
    )

    val PositiveGlow = Brush.linearGradient(
        colors = listOf(
            BrandTeal,
            BrandTealLight
        )
    )

    val AmberGlow = Brush.linearGradient(
        colors = listOf(
            BrandAmber,
            BrandAmberLight
        )
    )

    val RoseGlow = Brush.linearGradient(
        colors = listOf(
            BrandRose,
            BrandRoseLight
        )
    )
    
    val SlateGradient = Brush.linearGradient(
        colors = listOf(
            Slate900,
            Slate800
        )
    )
}
