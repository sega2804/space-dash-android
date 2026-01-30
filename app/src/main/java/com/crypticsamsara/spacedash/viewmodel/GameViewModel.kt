package com.crypticsamsara.spacedash.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crypticsamsara.spacedash.data.PreferencesManager
import com.crypticsamsara.spacedash.model.FloatingTextFactory
import com.crypticsamsara.spacedash.model.Obstacle
import com.crypticsamsara.spacedash.model.ObstacleFactory
import com.crypticsamsara.spacedash.model.Star
import com.crypticsamsara.spacedash.model.StarFactory
import com.crypticsamsara.spacedash.ui.audio.SoundManager
import com.crypticsamsara.spacedash.ui.components.PlayerRenderer
import com.crypticsamsara.spacedash.ui.effects.FloatingTextManager
import com.crypticsamsara.spacedash.ui.effects.ScreenShakeController
import com.crypticsamsara.spacedash.ui.haptics.HapticManager
import com.crypticsamsara.spacedash.utils.CollisionDetector
import com.crypticsamsara.spacedash.utils.CollisionDetector.checkNearMiss
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
    val isPaused: Boolean = false,
    val currentCombo: Int = 0,
    val maxComboReached: Int = 0
)
class GameViewModel(
     val soundManager: SoundManager? = null,
    val hapticManager: HapticManager? = null
): ViewModel() {
    var gameState by mutableStateOf(GameState())
    private set

    // obstacles list
    val obstacles = mutableStateListOf<Obstacle>()

    // screen shake controller
    val screenShakeController = ScreenShakeController()

    // floating text manager
    val floatingTextManager = FloatingTextManager()


    // tracking dodged obstacles
    private val dodgedObstacleIds = mutableSetOf<Int>()

    // track which obstacles we have checked for near miss
    private val nearMissedObstacleIds = mutableSetOf<Int>()

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

    var onComboMilestone: ((Int) -> Unit)? = null

    var isScreenShakeEnabled: Boolean = true

    // Scoring constraints
    private companion object {
        const val POINTS_PER_SECOND = 10
        const val POINTS_PER_DODGE = 50
        const val NEAR_MISS_BONUS = 25
        const val COMBO_MULTIPLIER = 10
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
            highScore = sessionHighScore,
            isPaused = false,
            currentCombo = 0,
            maxComboReached = 0
        )

        obstacles.clear()
        dodgedObstacleIds.clear()
        nearMissedObstacleIds.clear()
        floatingTextManager.clear()

        soundManager?.startMusic()

        // floating text updates
        floatingTextManager.startUpdating(viewModelScope)

        startGameLoop()
        startObstacleSpawner()
        startScoreTimer()
    }

    fun pauseGame() {
        if (!gameState.isPlaying || gameState.isGameOver || gameState.isPaused)

            gameState = gameState.copy(isPaused = true)

        // To pause music
        soundManager?.pauseMusic()
    }

    fun resumeGame() {
        if (!gameState.isPlaying || gameState.isGameOver || gameState.isPaused)

            gameState = gameState.copy(isPaused = true)

        // Resume music
        soundManager?.resumeMusic()
    }

    fun restartFromPause() {
        // unpause first
        gameState = gameState.copy(isPaused = false)
        // then restart
        restartGame()
    }

    fun toggleScreenShake(enabled: Boolean) {
        isScreenShakeEnabled = enabled
        if (!enabled) {
            screenShakeController.stopShake()
        }
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
                // spawn if not paused
                if (!gameState.isPaused) {
                    spawnObstacles()
                    delay((1000L..2500L).random()) // spawn every 1-2.5 seconds
                } else {
                    delay(100L) // pause for 0.1 second

                }
            }
        }
    }

    private fun startScoreTimer() {
        scoreJob?.cancel()
        val startTime = System.currentTimeMillis()
        var pauseStateTime: Long = 0
        var totalPausedTime: Long = 0


        scoreJob = viewModelScope.launch {
            while (isActive && gameState.isPlaying) {
                // handle pause state
                if (gameState.isPaused) {
                    if (pauseStateTime == 0L) {
                    pauseStateTime = System.currentTimeMillis()
                }
                delay(100L)
                continue
            } else {
                if (pauseStateTime != 0L) {
                    totalPausedTime += System.currentTimeMillis() - pauseStateTime
                    pauseStateTime = 0L
            }
        }

                val currentTime = System.currentTimeMillis()
                val survivalTime = currentTime - startTime - totalPausedTime

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

        // check for near miss
        checkNearMisses()


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

                // combo system
                val newCombo = gameState.currentCombo + 1
                val newMaxCombo = maxOf(newCombo, gameState.maxComboReached)

                // POINTS FOR THIS DODGE
                // base points calculation
                val basePoints = POINTS_PER_DODGE
                // combo bonus calculation
                val comboBonus = newCombo * COMBO_MULTIPLIER
                // dodge points calculation
                val dodgePoints = basePoints + comboBonus

                // OBSTACLE POSITION FOR FLOATING TEXT
                val obstaclePixelX = obstacle.x * screenWidth
                val obstaclePixelY = obstacle.y

                // FLOATING TEXT FOR POINTS
                val scoreText = FloatingTextFactory.createScoreText(
                    x = obstaclePixelX,
                    y = obstaclePixelY,
                    points = dodgePoints,
                    combo = newCombo
                )
                floatingTextManager.addFloatingText(scoreText)

                // Check for milestones
                if (newCombo in listOf(5, 10, 25, 50, 100)) {
                    onComboMilestone?.invoke(newCombo)
                    // haptic addition for milestone
                    hapticManager?.successVibration()
                    // only shake if enabled
                    if (isScreenShakeEnabled) {
                        // screen shake for milestone
                        screenShakeController.mediumShake(viewModelScope)
                    }

                    // MILESTONE FLOATING TEXT
                    val milestoneText = FloatingTextFactory.createMilestoneText(
                        x = screenWidth / 2,
                        y = screenHeight / 2,
                        milestone = newCombo
                    )
                    floatingTextManager.addFloatingText(milestoneText)
                } else {
                    hapticManager?.mediumVibration()


                //  COMBO FLOATING TEXT (every 3rd dodge if combo > 3)
                if (newCombo > 3 && newCombo % 3 == 0) {
                    val comboText = FloatingTextFactory.createComboText(
                        x = screenWidth / 2,
                        y = screenHeight / 3,
                        combo = newCombo
                    )
                    floatingTextManager.addFloatingText(comboText)
                }
            }

                // Dodge sound
                soundManager?.playDodge()

                // Increment dodged count
                val newDodgeCount = gameState.obstaclesDodged + 1
                gameState = gameState.copy(
                    obstaclesDodged = newDodgeCount,
                    currentCombo = newCombo,
                    maxComboReached = newMaxCombo
                )

                // Add dodge bonus to score
                val dodgeBonus = POINTS_PER_DODGE * newDodgeCount
                val timeScore = (gameState.survivalTime / 100).toInt()
                val totalComboBonus = (1..newCombo).sum() * COMBO_MULTIPLIER

                gameState = gameState.copy(
                    score = timeScore + dodgeBonus + totalComboBonus)
            }
        }
    }


    private fun checkNearMisses() {
        if (!gameState.isPlaying || gameState.isGameOver || !isScreenShakeEnabled) return

        val playerX = getPlayerPixelX()
        val playerY = screenHeight - PlayerRenderer.PLAYER_HEIGHT - 100f

        obstacles.forEach { obstacle ->
            if (!nearMissedObstacleIds.contains(obstacle.id)) {
                if (CollisionDetector.checkNearMiss(playerX, playerY, obstacle, screenWidth)) {
                    nearMissedObstacleIds.add(obstacle.id)
                    screenShakeController.lightShake(viewModelScope)
                }
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

                // Reset combo on collision
                gameState = gameState.copy(currentCombo = 0)

                // sound management
                // Explosion sound
                soundManager?.playExplosion()
                onExplosion?.invoke(playerX, playerY)
                // Vibration - strong
                hapticManager?.strongVibration()
                // only shake if enabled
                if (isScreenShakeEnabled) {
                    // screen shake - strong
                    screenShakeController.strongShake(viewModelScope)
                }

                val obstaclePixelX  = obstacle.x * screenWidth
                screenShakeController.directionalShake(
                    viewModelScope,
                    impactX = obstaclePixelX,
                    playerX = playerX
                )

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
        floatingTextManager.stop()

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

    // helper function for combo
    fun getComboMultiplier(): Float {
        return when (gameState.currentCombo) {
            in 0..4 -> 1f
            in 5..9 -> 1.5f
            in 10..24 -> 2f
            in 25..49 -> 2.5f
            else -> 3f
        }
    }

    fun getComboMessage(): String {
        return when (gameState.currentCombo) {
            0 -> ""
            in 1..4 -> "${gameState.currentCombo}x"
            in 5..9 -> "${gameState.currentCombo}x COMBO!"
            in 10..24 -> "${gameState.currentCombo}x GREAT!"
            in 25..49 -> "${gameState.currentCombo}x AMAZING!"
            else -> "${gameState.currentCombo}x LEGENDARY!"
        }
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

    override fun onCleared() {
        super.onCleared()
        soundManager?.release()
        floatingTextManager.stop()
        gameLoopJob?.cancel()
        spawnJob?.cancel()
        scoreJob?.cancel()
    }
}