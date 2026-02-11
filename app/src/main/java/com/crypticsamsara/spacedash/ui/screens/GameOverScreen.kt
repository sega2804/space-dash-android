package com.crypticsamsara.spacedash.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.material.icons.filled.Home
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.crypticsamsara.spacedash.data.game.DifficultyManager
import com.crypticsamsara.spacedash.ui.theme.DangerRed
import com.crypticsamsara.spacedash.ui.theme.NeonCyan
import com.crypticsamsara.spacedash.ui.theme.NeonPurple
import com.crypticsamsara.spacedash.ui.theme.SpaceBlack
import com.crypticsamsara.spacedash.ui.theme.SpaceBlue
import com.crypticsamsara.spacedash.ui.theme.SpaceDashTheme
import com.crypticsamsara.spacedash.ui.theme.StarWhite

@Composable
fun GameOverScreen(
    score: Int,
    survivalTime: String,
    obstacleDodged: Int,
    obstaclesDestroyed: Int,
    highScore: Int,
    onRestart: () -> Unit,
    onHome: (() -> Unit)? = null,
    onButtonClick: () -> Unit = {},
    maxCombo: Int = 0,
    maxDifficultyLevel: Int = 1
) {
    // Pulsing animation for "Game Over"
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    // scale animation for score display
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
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
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {

            // Animated "Game Over" title
            Text(
                text = "GAME OVER",
                color = DangerRed.copy(alpha = alpha),
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )

            // Score card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .scale(scale),
                colors = CardDefaults.cardColors(
                    containerColor = SpaceBlue.copy(alpha = 0.6f)
                ),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // New High Score indicator
                    if (score >= highScore && highScore > 0) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .background(
                                    color = Color(0xFFFFD700).copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "🏆",
                                fontSize = 20.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "NEW HIGH SCORE!",
                                color = Color(0xFFFFD700),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "🏆",
                                fontSize = 20.sp
                            )
                        }
                    }

                    // Final Score Label
                    Text(
                        text = "FINAL SCORE",
                        color = StarWhite.copy(alpha = 0.6f),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 2.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Score Number
                    Text(
                        text = score.toString(),
                        color = NeonCyan,
                        fontSize = 56.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

                    Spacer(modifier = Modifier.height(15.dp))

                    // Stats grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Survival Time
                StatItem(
                    icon = "⏱️",
                    value = survivalTime,
                    label = "TIME",
                    color = StarWhite
                )

                // Obstacles Dodged
                StatItem(
                    icon = "🎯",
                    value = obstacleDodged.toString(),
                    label = "DODGED",
                    color = NeonPurple
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Max Combo
                StatItem(
                    icon = "🔥",
                    value = "${maxCombo}x",
                    label = "COMBO",
                    color = Color(0xFFFF6B35)
                )

                // High Score
                StatItem(
                    icon = "⭐",
                    value = highScore.toString(),
                    label = "BEST",
                    color = Color(0xFFFFD700)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Performance Message
            PerformanceMessage(score = score)

            Spacer(modifier = Modifier.height(4.dp))

            // Action buttons
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Restart Button
                Button(
                    onClick = {
                        onButtonClick()
                        onRestart()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonCyan
                    ),
                    shape = RoundedCornerShape(14.dp),
                    elevation = ButtonDefaults.buttonElevation(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Restart",
                        tint = SpaceBlack,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "PLAY AGAIN",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = SpaceBlack
                    )
                }

                // Main Menu Button (if provided)
                onHome?.let { homeAction ->
                    Button(
                        onClick = {
                            onButtonClick()
                            homeAction()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = StarWhite.copy(alpha = 0.15f)
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Home",
                            tint = StarWhite,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "MAIN MENU",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = StarWhite
                        )
                    }
                }
            }
        }
    }
}



@Composable
private fun StatItem(
    icon: String,
    label: String,
    value: String,
    color: Color
) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .height(100.dp),
        colors = CardDefaults.cardColors(
            containerColor = SpaceBlue.copy(alpha = 0.4f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Icon
            Text(
                text = icon,
                fontSize = 24.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Value
            Text(
                text = value,
                color = color,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )

            // Label
            Text(
                text = label,
                color = StarWhite.copy(alpha = 0.5f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
private fun PerformanceMessage(score: Int) {
    val (message, emoji, color) = getPerformanceMessage(score)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.15f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = emoji,
                fontSize = 32.sp
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = message,
                    color = color,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = getPerformanceSubtext(score),
                    color = StarWhite.copy(alpha = 0.6f),
                    fontSize = 12.sp
                )
            }
        }
    }
}

private fun getPerformanceMessage(score: Int): Triple<String, String, Color> {
    return when (score) {
        in 0..99 -> Triple("Keep Practicing!", "🎮", Color(0xFF9E9E9E))
        in 100..249 -> Triple("Not Bad!", "👍", Color(0xFF8BC34A))
        in 250..499 -> Triple("Good Job!", "💪", Color(0xFF4CAF50))
        in 500..999 -> Triple("Great Work!", "✨", Color(0xFF00BCD4))
        in 1000..1499 -> Triple("Excellent!", "🌟", Color(0xFF2196F3))
        in 1500..1999 -> Triple("Amazing!", "🔥", Color(0xFFFF9800))
        in 2000..2999 -> Triple("Incredible!", "⚡", Color(0xFFFF6B35))
        in 3000..4999 -> Triple("Legendary!", "👑", Color(0xFFFFD700))
        else -> Triple("GODLIKE!", "💎", Color(0xFFE91E63))
    }
}

private fun getPerformanceSubtext(score: Int): String {
    return when (score) {
        in 0..99 -> "You're just getting started!"
        in 100..249 -> "Keep improving!"
        in 250..499 -> "You're getting the hang of it!"
        in 500..999 -> "You're a natural!"
        in 1000..1499 -> "Impressive skills!"
        in 1500..1999 -> "You're unstoppable!"
        in 2000..2999 -> "Master of the stars!"
        in 3000..4999 -> "You're a legend!"
        else -> "Hall of fame material!"
    }
}


@Preview(showBackground = true)
@Composable
fun GameOverScreenPreview() {
    SpaceDashTheme {
        GameOverScreen(
            score = 1234,
            survivalTime = "02:34",
            obstacleDodged = 42,
            obstaclesDestroyed = 10,
            highScore = 2000,
            onRestart = {},
            onHome = {},
            maxCombo = 15,
            maxDifficultyLevel = 5
        )
    }
}
