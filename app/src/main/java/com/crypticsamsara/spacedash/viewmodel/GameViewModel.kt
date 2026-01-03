package com.crypticsamsara.spacedash.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

data class GameState(
    val isPlaying: Boolean = false,
    val score: Int = 0,
    val playerX: Float = 0.5f, // Position
    val isGameOver: Boolean = false
)
class GameViewModel: ViewModel() {
    var gameState by mutableStateOf(GameState())
    private set

    // Screen dimensions
    var screenWidth by mutableStateOf(0f)
        private set

    var screenHeight by mutableStateOf(0f)
        private set

    fun setScreenSize (width: Float, height: Float) {
        screenWidth = width
        screenHeight = height
    }

    fun startGame() {
        gameState = GameState(
            isPlaying = true,
            score = 0,
            playerX = 0.5f,
            isGameOver = false
        )
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
    }

    // Get player position in pixels
    fun getPlayerPixelX(): Float {
        return gameState.playerX * screenWidth
    }
}