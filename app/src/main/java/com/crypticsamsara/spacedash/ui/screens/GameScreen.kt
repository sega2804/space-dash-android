package com.crypticsamsara.spacedash.ui.screens

import android.R.attr.radius
import android.R.attr.x
import android.R.attr.y
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.crypticsamsara.spacedash.model.StarFactory
import com.crypticsamsara.spacedash.ui.components.ComboDisplay
import com.crypticsamsara.spacedash.ui.components.DifficultyIndicator
import com.crypticsamsara.spacedash.ui.components.DifficultyLevelUpPopup
import com.crypticsamsara.spacedash.ui.components.FloatingTextRenderer
import com.crypticsamsara.spacedash.ui.components.MilestonePopup
import com.crypticsamsara.spacedash.ui.components.ObstacleRenderer.drawObstacle
import com.crypticsamsara.spacedash.ui.components.ParticleSystem
import com.crypticsamsara.spacedash.ui.components.PlayerRenderer
import com.crypticsamsara.spacedash.ui.components.PlayerRenderer.drawPlayer
import com.crypticsamsara.spacedash.ui.theme.DangerRed
import com.crypticsamsara.spacedash.ui.theme.NeonCyan
import com.crypticsamsara.spacedash.ui.theme.NeonPurple
import com.crypticsamsara.spacedash.ui.theme.SpaceBlack
import com.crypticsamsara.spacedash.ui.theme.StarWhite
import com.crypticsamsara.spacedash.viewmodel.GameViewModel
import kotlinx.coroutines.delay
import java.nio.file.Files.size
import kotlin.random.Random

@Composable
fun GameScreen (
    viewModel: GameViewModel = viewModel(),
    onBackToHome: (() -> Unit)? = null
) {
    val gameState = viewModel.gameState
    val obstacles = viewModel.obstacles
    val stars = viewModel.stars
    val particleSystem = remember { ParticleSystem() }
    val shakeOffset = viewModel.screenShakeController.shakeOffset
    val floatingTexts = viewModel.floatingTextManager.floatingTexts

    var currentMilestone by remember { mutableStateOf<Int?>(null) }
    var difficultyLevelUp by remember { mutableStateOf<Int?>(null) }

    // Setting up explosion
    LaunchedEffect(Unit) {
        viewModel.onExplosion = { x, y ->
            particleSystem.createExplosion(x, y, particleCount = 30)
        }

        // milestone callback
        viewModel.onComboMilestone = {milestone ->
            currentMilestone = milestone
        }

        // difficulty level up callback
        viewModel.onDifficultyLevelUp = {level ->
            difficultyLevelUp = level
        }
    }

    // MOVING STARS
    LaunchedEffect(gameState.isPlaying, gameState.isPaused) {
        while (gameState.isPlaying && !gameState.isPaused) {
            StarFactory.updateStars(stars, viewModel.screenHeight)
            delay(16L) // 60 FPS
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SpaceBlack)
            .offset(shakeOffset.x.dp, shakeOffset.y.dp) // shake application
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
            if (gameState.isPlaying || gameState.isGameOver) {
                val playerX = viewModel.getPlayerPixelX()
                val playerY = canvasHeight - PlayerRenderer.PLAYER_HEIGHT - 100f

                drawPlayer(playerX, playerY)
            }

            // UPDATE AND DRAW PARTICLES
            particleSystem.update()
            with(particleSystem) {
                drawParticles()
            }

            with(FloatingTextRenderer) {
                drawFloatingTexts(floatingTexts)
            }

        }

        // back button in top-left
        if (onBackToHome != null && !gameState.isPlaying) {
            IconButton(
                onClick = {
                    viewModel.onButtonClick()
                    onBackToHome()
                },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back to Home",
                    tint = StarWhite
                )
            }
        }

        // difficulty level up notifs
        difficultyLevelUp?.let { level ->
            if (level >= 5) {
                Box(
                    modifier = Modifier
                        .align (Alignment.Center)
                        .padding(32.dp)
                ) {
                    DifficultyLevelUpPopup(
                        level = level,
                        onDismiss = { difficultyLevelUp = null }
                    )
                }
            } else {
                // automatic dismissal for lower levels
                LaunchedEffect(level) {
                    delay(1000)
                    difficultyLevelUp = null
                }
            }
        }

        // Milestone popup
        currentMilestone?.let { milestone ->
            Box (
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center)
                    .padding(horizontal = 32.dp)
            ) {
                MilestonePopup(
                    milestone = milestone,
                    onDismiss = { currentMilestone = null }
                )
            }
        }

        // Score Display
        if (gameState.isPlaying && !gameState.isGameOver && !gameState.isPaused) {
           Column(
               modifier = Modifier
                   .align(Alignment.TopCenter)
                   .padding(top = 32.dp),
               horizontalAlignment = Alignment.CenterHorizontally
           ) {

               // Main Score
               Text(
                   text = "Score: ${gameState.score}",
                   color = NeonCyan,
                   fontSize = 48.sp,
                   fontWeight = FontWeight.Bold
               )

               // Combo Display
               ComboDisplay(
                   combo = gameState.currentCombo,
                   comboMessage = viewModel.getComboMessage(),
                   modifier = Modifier.padding(top = 8.dp)
               )

               // difficulty indicator
               DifficultyIndicator(
                   level = viewModel.getCurrentDifficultyLevel(),
                   description = viewModel.getCurrentDifficultyDescription(),
                   color = viewModel.getCurrentDifficultyColor(),
                   modifier = Modifier.padding(top = 8.dp)
               )

               // Stats row
               Row(
                   horizontalArrangement = Arrangement.spacedBy(24.dp),
                   modifier = Modifier.padding(top = 8.dp)
               ) {
                   // Time survived
                   Column(horizontalAlignment = Alignment.CenterHorizontally) {
                       Text(
                           text = viewModel.getFormattedSurvivalTime(),
                           color = StarWhite,
                           fontSize = 16.sp,
                           fontWeight = FontWeight.Bold
                       )
                       Text(
                           text = "TIME",
                           color = StarWhite.copy(alpha = 0.6f),
                           fontSize = 10.sp
                       )
                   }

                   // Obstacles dodged
                   Column(horizontalAlignment = Alignment.CenterHorizontally) {
                       Text(
                           text = "${gameState.obstaclesDodged}",
                           color = NeonPurple,
                           fontSize = 16.sp,
                           fontWeight = FontWeight.Bold
                       )
                       Text(
                           text = "DODGED",
                           color = StarWhite.copy(alpha = 0.6f),
                           fontSize = 10.sp
                       )
                   }
               }
           }
       }

        // Pause button
        if (gameState.isPlaying && !gameState.isGameOver && !gameState.isPaused) {
            IconButton(
                onClick = {
                    viewModel.onButtonClick()
                    viewModel.pauseGame()
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Pause,
                    contentDescription = "Pause",
                    tint = NeonCyan,
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        // Pause menu overlay
        if (gameState.isPaused){
            /*
            PauseMenuOverlay(
                onResume = { viewModel.resumeGame() },
                onRestart = { viewModel.restartFromPause() },
                onMainMenu = {
                    viewModel.stopGame()
                    onBackToHome?.invoke()
                             },
                onButtonClick = { viewModel.onButtonClick() }
            )
             */
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.7f))
                    .clickable{ viewModel.resumeGame() },
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "PAUSED",
                        color = NeonCyan,
                        fontSize = 56.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Tap to Resume",
                        color = StarWhite,
                        fontSize = 20.sp
                    )
                }
            }
        }

        // GameOverScreen
        if (!gameState.isPlaying && gameState.isGameOver) {

            GameOverScreen(
                score = gameState.score,
                survivalTime = viewModel.getFormattedSurvivalTime(),
                obstacleDodges = gameState.obstaclesDodged,
                highScore = gameState.highScore,
                maxCombo = gameState.maxComboReached,
                maxDifficultyLevel = viewModel.getCurrentDifficultyLevel(),
                onRestart = {
                    particleSystem.clear() // Clear particles on restart
                    viewModel.restartGame()
                            },
                onButtonClick = { viewModel.onButtonClick()},
                onHome = onBackToHome
            )
        }

        // Control buttons at bottom
        if (gameState.isPlaying && !gameState.isGameOver && !gameState.isPaused) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 40.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Left button
                Button(
                    onClick = {
                        viewModel.onButtonClick()
                        viewModel.movePlayerLeft() },
                    modifier = Modifier.size(80.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonCyan.copy(alpha = 0.3f)
                    )
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
                    onClick = {
                        viewModel.onButtonClick()
                        viewModel.movePlayerRight() },
                    modifier = Modifier.size(80.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonCyan.copy(alpha = 0.3f)
                    ),
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
}