package com.crypticsamsara.spacedash.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.crypticsamsara.spacedash.data.game.DifficultyManager
import kotlinx.coroutines.delay

@Composable
fun DifficultyLevelUpPopup(
    level: Int,
    onDismiss: () -> Unit
) {
    var isVisible by remember { mutableStateOf(true) }

    // Automatic dismissal after 2 seconds
    LaunchedEffect(Unit) {
        delay(2000)
        isVisible = false
        delay(300)
        onDismiss()
    }

    // Scale animation
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

    val color = DifficultyManager.getDifficultyColor((level - 1) * 18000L)

    Box(
        modifier = Modifier.scale(scale)
            .alpha(alpha)
            .background(
                color = Color.Black.copy(alpha = 0.8f),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "⚠️",
                fontSize = 48.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "DIFFICULTY",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "LEVEL $level",
                color = color,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = DifficultyManager.getDifficultyDescription((level - 1) * 18000L).uppercase(),
                color = color.copy(alpha = 0.8f),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}