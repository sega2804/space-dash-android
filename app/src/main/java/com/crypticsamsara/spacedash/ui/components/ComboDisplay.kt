package com.crypticsamsara.spacedash.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.crypticsamsara.spacedash.ui.theme.NeonCyan
import com.crypticsamsara.spacedash.ui.theme.StarWhite

@Composable
fun ComboDisplay(
    combo: Int,
    comboMessage: String,
    modifier: Modifier = Modifier
) {
    // only show when combo > 0
    if (combo == 0) return

    // pulse animation
    val infiniteTransition = rememberInfiniteTransition(label = "combo_pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    // Color based on combo level
    val comboColor = when (combo) {
        in 0..4 -> StarWhite
        in 5..9 -> NeonCyan
        in 10..24 -> Color(0xFFFFD700) // Gold
        in 25..49 -> Color(0xFFFF6B35) // Orange
        else -> Color(0xFFFF1744) // Red
    }

    Box(
        modifier = Modifier
            .scale(scale)
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        comboColor.copy(alpha = 0.3f),
                        comboColor.copy(alpha = 0.1f)
                    )
                ),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Combo count
            Text(
                text = comboMessage,
                color = comboColor,
                fontSize = if (combo >= 10) 32.sp else 24.sp,
                fontWeight = FontWeight.Bold
            )

            // Bonus indicator
            if (combo >= 5) {
                Text(
                    text = "+${combo * 10} pts/dodge",
                    color = comboColor.copy(alpha = 0.8f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}