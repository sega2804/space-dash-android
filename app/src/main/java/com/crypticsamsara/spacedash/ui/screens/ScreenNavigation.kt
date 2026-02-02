
package com.crypticsamsara.spacedash.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.crypticsamsara.spacedash.ui.audio.SoundManager
import com.crypticsamsara.spacedash.ui.haptics.HapticManager
import com.crypticsamsara.spacedash.viewmodel.GameViewModel
import com.crypticsamsara.spacedash.viewmodel.GameViewModelFactory

enum class Screen {
    HOME,
    GAME
}

@Composable
fun GameNavigation(
    soundManager: SoundManager,
    hapticManager: HapticManager
) {
    var currentScreen by remember { mutableStateOf(Screen.HOME) }
    val viewModel: GameViewModel = viewModel(
        factory = GameViewModelFactory(soundManager, hapticManager)
    )

    when (currentScreen) {
        Screen.HOME -> {
            HomeScreen(
                highScore = viewModel.gameState.highScore,
                onStartGame = {
                    // Play click sound + haptic
                    viewModel.onButtonClick()
                    // Navigate to game
                    currentScreen = Screen.GAME
                    // Start the game
                    viewModel.startGame()
                }
            )
        }
        Screen.GAME -> {
            GameScreen(viewModel = viewModel,
                onBackToHome = {
                    // Stop game if playing
                    if (viewModel.gameState.isPlaying) {
                        viewModel.stopGame()
                    }
                    // Navigate back to home
                    currentScreen = Screen.HOME
                }
            )
        }
    }
}