package com.crypticsamsara.spacedash.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crypticsamsara.spacedash.data.PreferencesManager
import com.crypticsamsara.spacedash.model.Obstacle
import com.crypticsamsara.spacedash.model.ObstacleFactory
import com.crypticsamsara.spacedash.model.Star
import com.crypticsamsara.spacedash.model.StarFactory
import com.crypticsamsara.spacedash.ui.audio.SoundManager
import com.crypticsamsara.spacedash.ui.components.PlayerRenderer
import com.crypticsamsara.spacedash.ui.haptics.HapticManager
import com.crypticsamsara.spacedash.utils.CollisionDetector
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class GameState(
    val isPlaying: Boolean = false,
    val score: Int = 0,
    val playerX: Float = 0.5f, // Position
    val isGameOver: Boolean = false,
    val survivalTime: Long = 0L,
    val obstaclesDodged: Int = 0,
    val highScore: Int = 0,
    val isPaused: Boolean = false
)
class GameViewModel(
     val soundManager: SoundManager? = null,
    val hapticManager: HapticManager? = null
): ViewModel() {
    var gameState by mutableStateOf(GameState())
    private set

    // obstacles list
    val obstacles = mutableStateListOf<Obstacle>()

    // tracking dodged obstacles
    private val dodgedObstacleIds = mutableSetOf<Int>()

    // Stars List (static)
    var stars = mutableStateListOf<Star>()

    // Screen dimensions
    var screenWidth by mutableFloatStateOf(0f)
        private set

    var screenHeight by mutableFloatStateOf(0f)
        private set


    // Game loop job
    private var gameLoopJob: Job? = null
    private var spawnJob: Job? = null
    private var scoreJob: Job? = null

    // High Score
    private var sessionHighScore = 0

    var onExplosion: ((Float, Float) -> Unit)? = null


    // Scoring constraints
    private companion object {
        const val POINTS_PER_SECOND = 10
        const val POINTS_PER_DODGE = 50
        const val NEAR_MISS_BONUS = 25
    }


    fun setScreenSize (width: Float, height: Float) {
        screenWidth = width
        screenHeight = height

        // Generation of stars once when screen size is known
        if (stars.isEmpty() && width > 0 && height > 0) {
            stars.addAll(StarFactory.generateStarfield(width, height, 100))
        }
    }

    fun startGame() {
        gameState = GameState(
            isPlaying = true,
            score = 0,
            playerX = 0.5f,
            isGameOver = false,
            survivalTime = 0L,
            obstaclesDodged = 0,
            highScore = sessionHighScore
        )
        obstacles.clear()
        dodgedObstacleIds.clear()

        soundManager?.startMusic()
        startGameLoop()
        startObstacleSpawner()
        startScoreTimer()
    }

    private fun startGameLoop() {
        gameLoopJob?.cancel()
        gameLoopJob = viewModelScope.launch {
            while (isActive && gameState.isPlaying) {
                updateGame()
                delay(16L) // 60 FPS
            }
        }
    }

    private fun startObstacleSpawner() {
        spawnJob?.cancel()
        spawnJob = viewModelScope.launch {
            while (isActive && gameState.isPlaying) {
                spawnObstacles()
                delay((1000L..2500L).random()) // spawn every 1-2.5 seconds
            }
        }
    }

    private fun startScoreTimer() {
        scoreJob?.cancel()
        val startTime = System.currentTimeMillis()

        scoreJob = viewModelScope.launch {
            while (isActive && gameState.isPlaying) {
                val currentTime = System.currentTimeMillis()
                val survivalTime = currentTime - startTime

                // Update survival time
                gameState = gameState.copy(survivalTime = survivalTime)

                // Add time-based score (10 points per second)
                val timeScore = (survivalTime / 100) // 1 point per 100ms = 10
                val totalScore = timeScore.toInt() + (gameState.obstaclesDodged * POINTS_PER_DODGE)

                gameState = gameState.copy(score = totalScore)

                delay(100L) // Update every 100ms
            }
        }
    }

    private fun updateGame() {
        // movement from down
        obstacles.forEach { obstacle ->
            obstacle.y += obstacle.speed
        }

        // Check for dodged obstacles
        checkDodgedObstacles()

        // Check collisions
        checkCollisions()

        // Remove obstacles that are off screen
        obstacles.removeAll {
            val isOffScreen = it.y > screenHeight + 100f
            if (isOffScreen) {
                dodgedObstacleIds.remove(it.id) // Clean up tracking
            }
            isOffScreen
        }
    }

    private fun checkDodgedObstacles() {
        val playerY = screenHeight - PlayerRenderer.PLAYER_HEIGHT - 100f

        obstacles.forEach { obstacle ->
            // check if obstacle has passed the player and hasn't been counted yet
            if (obstacle.y > playerY + PlayerRenderer.PLAYER_HEIGHT &&
                !dodgedObstacleIds.contains(obstacle.id)) {

                dodgedObstacleIds.add(obstacle.id)

                // Dodge sound
                soundManager?.playDodge()
                // Vibration
                hapticManager?.mediumVibration()

                // Increment dodged count
                val newDodgeCount = gameState.obstaclesDodged + 1
                gameState = gameState.copy(obstaclesDodged = newDodgeCount)

                // Add dodge bonus to score
                val dodgeBonus = POINTS_PER_DODGE * newDodgeCount
                val timeScore = (gameState.survivalTime / 100).toInt()
                gameState = gameState.copy(score = timeScore + dodgeBonus)
            }
        }
    }


    private fun spawnObstacles() {
        if (screenWidth > 0) {
            val newObstacle = ObstacleFactory.createRandomObstacle(screenWidth)
            obstacles.add(newObstacle)
        }
    }

    private fun checkCollisions() {
        if (!gameState.isPlaying || gameState.isGameOver) return

        val playerX = getPlayerPixelX()
        val playerY = screenHeight - PlayerRenderer.PLAYER_HEIGHT - 100f

        // Check each obstacle for collision
        obstacles.forEach { obstacle ->
            if (CollisionDetector.checkCollision(playerX, playerY, obstacle, screenWidth)) {

                // Explosion sound
                soundManager?.playExplosion()
                onExplosion?.invoke(playerX, playerY)
                // Vibration - strong
                hapticManager?.strongVibration()
                triggerGameOver()
                return
            }
        }
    }


    private fun triggerGameOver() {
        // Update high score if current is higher
        if (gameState.score > sessionHighScore) {
            sessionHighScore = gameState.score
        }

       gameState = gameState.copy(
            isPlaying = false,
            isGameOver = true,
            highScore = sessionHighScore
        )

        // Stop music
        soundManager?.pauseMusic()

        gameState = gameState.copy(
            isPlaying = false,
            isGameOver = true,
            highScore = sessionHighScore
            )
        gameLoopJob?.cancel()
        spawnJob?.cancel()
        scoreJob?.cancel()
    }
    fun movePlayerLeft() {
        if (!gameState.isPlaying || gameState.isGameOver) return

        // Move left, but don't go below 0
        val newX = (gameState.playerX - 0.05f).coerceAtLeast(0f)
        gameState = gameState.copy(playerX = newX)
    }

    fun movePlayerRight() {
        if (!gameState.isPlaying || gameState.isGameOver) return

        // Move right, but don't go above 1
        val newX = (gameState.playerX + 0.05f).coerceAtMost(1f)
        gameState = gameState.copy(playerX = newX)
    }

    fun restartGame() {
        startGame()
    }

    fun stopGame() {
        if (gameState.score > sessionHighScore) {
            sessionHighScore = gameState.score
        }
        gameState = gameState.copy(
            isPlaying = false,
            isGameOver = true,
            highScore = sessionHighScore)
        gameLoopJob?.cancel()
        spawnJob?.cancel()
        scoreJob?.cancel()
    }

    // Get player position in pixels
    fun getPlayerPixelX(): Float {
        return gameState.playerX * screenWidth
    }

    // Helper to format survival time
    fun getFormattedSurvivalTime(): String {
        val seconds = gameState.survivalTime / 1000
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return String.format("%02d:%02d", minutes, remainingSeconds)
    }

    fun onButtonClick() {
        soundManager?.playClick()
        hapticManager?.lightTap()
    }

    fun toggleHaptics(enabled: Boolean) {
        hapticManager?.isHapticEnabled = enabled
    }

    fun isHapticsEnabled(): Boolean {
        return hapticManager?.isHapticEnabled ?: false
    }

    override fun onCleared() {
        super.onCleared()
        soundManager?.release()
        gameLoopJob?.cancel()
        spawnJob?.cancel()
        scoreJob?.cancel()
    }
}