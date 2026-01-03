package com.crypticsamsara.spacedash.ui.screens

import android.R.attr.radius
import android.R.attr.x
import android.R.attr.y
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.crypticsamsara.spacedash.ui.components.ObstacleRenderer.drawObstacle
import com.crypticsamsara.spacedash.ui.components.PlayerRenderer
import com.crypticsamsara.spacedash.ui.components.PlayerRenderer.drawPlayer
import com.crypticsamsara.spacedash.ui.theme.NeonCyan
import com.crypticsamsara.spacedash.ui.theme.SpaceBlack
import com.crypticsamsara.spacedash.ui.theme.StarWhite
import com.crypticsamsara.spacedash.viewmodel.GameViewModel
import java.nio.file.Files.size
import kotlin.random.Random

@Composable
fun GameScreen (
    viewModel: GameViewModel = viewModel()
) {
    val gameState = viewModel.gameState
    val obstacles = viewModel.obstacles
    val stars = viewModel.stars

    // For testing to start game automatically
    LaunchedEffect(Unit) {
        viewModel.startGame()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SpaceBlack)
    ) {

        // Canvas for game rendering
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .onSizeChanged { size ->
                    viewModel.setScreenSize(size.width.toFloat(), size.height.toFloat())
                }
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            // Starfield background (simple stars)
            stars.forEach { star ->
                drawCircle(
                    color = StarWhite.copy(alpha = Random.nextFloat() * 0.5f + 0.5f),
                    radius = star.radius,
                    center = star.position
                )
            }

            // obstacles
            obstacles.forEach { obstacle ->
                drawObstacle(obstacle, canvasWidth)
            }

            // Player
            if (gameState.isPlaying) {
                val playerX = viewModel.getPlayerPixelX()
                val playerY = canvasHeight - PlayerRenderer.PLAYER_HEIGHT - 100f

                drawPlayer(playerX, playerY)
            }
        }

            // Score Display
            if (gameState.isPlaying) {
                Text(
                    text = "Score: ${gameState.score}",
                    color = NeonCyan,
                    fontSize = 24.sp,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 40.dp)
                )
            }

        // Control buttons at bottom
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Left button
            Button(
                onClick = { viewModel.movePlayerLeft() },
                modifier = Modifier.size( 80.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonCyan.copy(alpha = 0.3f)
                ),
                enabled = gameState.isPlaying && !gameState.isGameOver
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Move Left",
                    tint = StarWhite,
                    modifier = Modifier.size(40.dp)
                )
            }

            // Right button
            Button(
                onClick = { viewModel.movePlayerRight() },
                modifier = Modifier.size( 80.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonCyan.copy(alpha = 0.3f)
                ),
                enabled = gameState.isPlaying && !gameState.isGameOver
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Move Right",
                    tint = StarWhite,
                    modifier = Modifier.size(40.dp)
                )
            }
        }
    }
}
