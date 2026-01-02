package com.crypticsamsara.spacedash.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class GameState(
    val isPlaying: Boolean = false  ,
    val score: Int = 0
)

class GameViewModel : ViewModel() {
    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    fun startGame() {
        _gameState.value = GameState(isPlaying = true, score = 0)
    }

    fun stopGame() {
        _gameState.value = _gameState.value.copy(isPlaying = false)
    }
}