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
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.crypticsamsara.spacedash.data.game.CreditsManager
import com.crypticsamsara.spacedash.data.game.PreferencesManager
import com.crypticsamsara.spacedash.ui.audio.SoundManager
import com.crypticsamsara.spacedash.ui.haptics.HapticManager
import com.crypticsamsara.spacedash.ui.theme.NeonCyan
import com.crypticsamsara.spacedash.ui.theme.NeonPurple
import com.crypticsamsara.spacedash.ui.theme.SpaceBlack
import com.crypticsamsara.spacedash.ui.theme.SpaceBlue
import com.crypticsamsara.spacedash.ui.theme.SpaceDashTheme
import com.crypticsamsara.spacedash.ui.theme.StarWhite

@Composable
fun HomeScreen(
    highScore: Int,
    currentCredits: Int,
    onStartGame: () -> Unit,
    onOpenStore: () -> Unit
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
            modifier = Modifier.padding(24.dp)
                .fillMaxSize()
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

            // Start Game Button
            MenuButton(
                text = "START GAME",
                onClick = onStartGame,
                icon = "🚀",
                color = Color(0xFF00F0FF)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Store Button
            MenuButton(
                text = "WEAPON STORE",
                onClick = onOpenStore,
                icon = "🛒",
                color = Color(0xFFFFD700)
            )

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
        CreditsDisplay(
            credits = currentCredits,
            large = true,
            modifier = Modifier.align(Alignment.TopEnd)
                .padding(16.dp)
        )
    }
}

@Composable
private fun MenuButton(
    text: String,
    onClick: () -> Unit,
    icon: String,
    color: Color
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth(0.75f)
            .height(64.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = color
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 8.dp,
            pressedElevation = 4.dp
        )
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = icon,
                fontSize = 28.sp
            )
            Text(
                text = text,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    val context = LocalContext.current
    SpaceDashTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            HomeScreen(
                highScore = 800,
                currentCredits = 98,
                // FIX: Pass empty lambdas for the onStartGame and onOpenStore parameters.
                // The original code was passing `()` and `null`, which are not valid
                // values for a parameter of type `() -> Unit`.
                onStartGame = {},
                onOpenStore = {}
            )
        }
    }
}
