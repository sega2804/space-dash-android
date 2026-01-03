package com.crypticsamsara.spacedash.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crypticsamsara.spacedash.model.Obstacle
import com.crypticsamsara.spacedash.model.ObstacleFactory
import com.crypticsamsara.spacedash.model.Star
import com.crypticsamsara.spacedash.model.StarFactory
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class GameState(
    val isPlaying: Boolean = false,
    val score: Int = 0,
    val playerX: Float = 0.5f, // Position
    val isGameOver: Boolean = false
)
class GameViewModel: ViewModel() {
    var gameState by mutableStateOf(GameState())
    private set

    // obstacles list
    val obstacles = mutableStateListOf<Obstacle>()

    // Stars List (static)
    var stars = mutableStateListOf<Star>()


    // Screen dimensions
    var screenWidth by mutableStateOf(0f)
        private set

    var screenHeight by mutableStateOf(0f)
        private set

    // Game loop job
    private var gameLoopJob: Job? = null
    private var spawnJob: Job? = null


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
            isGameOver = false
        )
        obstacles.clear()
        startGameLoop()
        startObstacleSpawner()

    }

    private fun startGameLoop() {
        gameLoopJob?.cancel()
        gameLoopJob = viewModelScope.launch {
            while (isActive && gameState.isPlaying) {
                updateObstacles()
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

    private fun updateObstacles() {
        // movement from down
        obstacles.forEach { obstacle ->
            obstacle.y += obstacle.speed
        }
        // Remove obstacles that are off screen
        obstacles.removeAll { it.y > screenHeight + 100f}
    }

    private fun spawnObstacles() {
        if (screenWidth > 0) {
            val newObstacle = ObstacleFactory.createRandomObstacle(screenWidth)
            obstacles.add(newObstacle)
        }
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

    fun stopGame() {
        gameState = gameState.copy(isPlaying = false, isGameOver = true)
        gameLoopJob?.cancel()
        spawnJob?.cancel()
    }

    // Get player position in pixels
    fun getPlayerPixelX(): Float {
        return gameState.playerX * screenWidth
    }

    override fun onCleared() {
        super.onCleared()
        gameLoopJob?.cancel()
        spawnJob?.cancel()
    }
}