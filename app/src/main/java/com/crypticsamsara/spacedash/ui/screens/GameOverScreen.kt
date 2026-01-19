package com.crypticsamsara.spacedash.ui.screens

import android.R.attr.text
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.crypticsamsara.spacedash.ui.theme.DangerRed
import com.crypticsamsara.spacedash.ui.theme.NeonCyan
import com.crypticsamsara.spacedash.ui.theme.NeonPurple
import com.crypticsamsara.spacedash.ui.theme.SpaceBlack
import com.crypticsamsara.spacedash.ui.theme.SpaceBlue
import com.crypticsamsara.spacedash.ui.theme.StarWhite

@Composable
fun GameOverScreen(
    score: Int,
    survivalTime: String,
    obstacleDodges: Int,
    highScore: Int,
    onRestart: () -> Unit,
    onHome: () -> Unit = {}
) {
    // Pulsing animation for "Game Over"
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        SpaceBlack,
                        SpaceBlue.copy(alpha = 0.3f),
                        SpaceBlack
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {

            // Animated "Game Over" title
            Text(
                text = "GAME OVER",
                color = DangerRed.copy(alpha = alpha),
                fontSize = 56.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Score card
            Card(
                modifier = Modifier.fillMaxWidth(0.9f),
                colors = CardDefaults.cardColors(
                    containerColor = SpaceBlue.copy(alpha = 0.6f)
                ),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // New High Score indicator
                    if (score >= highScore && highScore > 0) {
                        Text(
                            text = "🏆 NEW HIGH SCORE! 🏆",
                            color = Color(0xFFFFD700), // Gold
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Final Score
                    Text(
                        text = "FINAL SCORE",
                        color = StarWhite.copy(alpha = 0.7f),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Text(
                        text = "$score",
                        color = NeonCyan,
                        fontSize = 72.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Stats grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // Survival time stat
                        StatItem(
                            label = "TIME",
                            value = survivalTime,
                            color = StarWhite
                        )

                        // Obstacle dodged stat
                        StatItem(
                            label = "DODGED",
                            value = "$obstacleDodges",
                            color = NeonPurple
                        )

                        // High score stat
                        StatItem(
                            label = "BEST",
                            value = "$highScore",
                            color = Color(0xFFFFD700)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

        // Action buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Restart button
                Button(
                    onClick = onRestart,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonCyan
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(60.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Restart",
                        tint = SpaceBlack,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "PLAY AGAIN",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = SpaceBlack
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Performance message
            val message = getPerformanceMessage(score)
            Text(
                text = message,
                color = StarWhite.copy(alpha = 0.8f),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}


@Composable
private fun StatItem(
    label: String,
    value: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(8.dp)
    ) {
        Text(
            text = value,
            color = color,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            color = StarWhite.copy(alpha = 0.6f),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

private fun getPerformanceMessage(score: Int): String {
    return when {
        score >= 2000 -> "🌟 LEGENDARY PILOT! You're unstoppable!"
        score >= 1500 -> "⚡ INCREDIBLE! Master of the stars!"
        score >= 1000 -> "🚀 AMAZING! You're a natural!"
        score >= 500 -> "✨ GREAT JOB! Keep improving!"
        score >= 250 -> "💫 NICE TRY! You're getting better!"
        else -> "🎮 KEEP PRACTICING! You've got this!"
    }
}