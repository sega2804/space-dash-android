package com.crypticsamsara.spacedash.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun MilestonePopup(
    milestone: Int,
    onDismiss: () -> Unit
) {
    var isVisible by remember { mutableStateOf(true) }

    // 2 second auto dismissal
    LaunchedEffect(Unit) {
        delay(2000)
        isVisible = false
        delay(300) // Wait for animation
        onDismiss()
    }

    // scale animation
    val scale by animateFloatAsState(
            targetValue = if (isVisible) 1f else 0.5f,
            animationSpec = tween(300, easing = FastOutSlowInEasing),
            label = "scale"
    )

    // Alpha animation
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(300),
        label = "alpha"
    )

    val milestoneColor = when (milestone) {
        5 -> Color(0xFF00F0FF) // Cyan
        10 -> Color(0xFFFFD700) // Gold
        25 -> Color(0xFFFF6B35) // Orange
        50 -> Color(0xFFFF1744) // Red
        else -> Color(0xFFBF40BF) // Purple
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .alpha(alpha)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        milestoneColor.copy(alpha = 0.4f),
                        Color.Transparent
                    )
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🔥",
                fontSize = 48.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${milestone}x COMBO!",
                color = milestoneColor,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "MILESTONE REACHED!",
                color = milestoneColor.copy(alpha = 0.8f),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}