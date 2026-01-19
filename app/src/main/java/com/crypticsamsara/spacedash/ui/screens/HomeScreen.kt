package com.crypticsamsara.spacedash.ui.screens

import android.R.attr.label
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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.crypticsamsara.spacedash.ui.theme.NeonCyan
import com.crypticsamsara.spacedash.ui.theme.NeonPurple
import com.crypticsamsara.spacedash.ui.theme.SpaceBlack
import com.crypticsamsara.spacedash.ui.theme.SpaceBlue
import com.crypticsamsara.spacedash.ui.theme.StarWhite

@Composable
fun HomeScreen(
    highScore: Int,
    onStartGame: () -> Unit,
) {
    // Pulsing animation for title
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
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
                        SpaceBlue.copy(alpha = 0.5f),
                        SpaceBlack
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            // Animated game title
            Text(
                text = "SPACE",
                color = NeonCyan.copy(alpha = alpha),
                fontSize = 72.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.scale(scale)
            )

            Text(
                text = "DASH",
                color = NeonPurple.copy(alpha = alpha),
                fontSize = 72.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.scale(scale)
            )

            Spacer(modifier = Modifier.height(48.dp))

            // High score display
            if (highScore > 0) {
                Text(
                    text = "HIGH SCORE",
                    color = StarWhite.copy(alpha = 0.6f),
                    fontSize = 14.sp
                )
                Text(
                    text = "$highScore",
                    color = NeonCyan,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(32.dp))
            }

            // Play button
            Button(
                onClick = onStartGame,
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonCyan
                ),
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(70.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play",
                    tint = SpaceBlack,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = "START GAME",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = SpaceBlack
                )
            }

            Spacer(modifier = Modifier.height(24.dp))


            // Instructions
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.alpha(0.7f)
            ) {
                Text(
                    text = "HOW TO PLAY",
                    color = NeonPurple,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "• Use arrow buttons to move",
                    color = StarWhite,
                    fontSize = 12.sp
                )
                Text(
                    text = "• Dodge falling obstacles",
                    color = StarWhite,
                    fontSize = 12.sp
                )
                Text(
                    text = "• Survive as long as possible",
                    color = StarWhite,
                    fontSize = 12.sp
                )
            }
        }
    }
}