
package com.crypticsamsara.spacedash.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.crypticsamsara.spacedash.viewmodel.GameViewModel

enum class Screen {
    HOME,
    GAME
}

@Composable
fun GameNavigation() {
    var currentScreen by remember { mutableStateOf(Screen.HOME) }
    val viewModel: GameViewModel = viewModel()

    when (currentScreen) {
        Screen.HOME -> {
            HomeScreen(
                highScore = viewModel.gameState.highScore,
                onStartGame = {
                    currentScreen = Screen.GAME
                    viewModel.startGame()
                }
            )
        }
        Screen.GAME -> {
            GameScreen(viewModel = viewModel)
        }
    }
}