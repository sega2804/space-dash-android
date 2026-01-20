package com.crypticsamsara.spacedash

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.crypticsamsara.spacedash.ui.audio.SoundManager
import com.crypticsamsara.spacedash.ui.screens.GameNavigation
import com.crypticsamsara.spacedash.ui.screens.GameScreen
import com.crypticsamsara.spacedash.ui.theme.SpaceDashTheme
import com.crypticsamsara.spacedash.viewmodel.GameViewModel
import com.crypticsamsara.spacedash.viewmodel.GameViewModelFactory

class MainActivity : ComponentActivity() {

    private lateinit var soundManager: SoundManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Start soundManager
        soundManager = SoundManager(this)

        setContent {
            SpaceDashTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // create viewmodel with soundManager
                    val viewModel: GameViewModel = viewModel(
                        factory = GameViewModelFactory(soundManager)
                    )
                    GameScreen(viewModel = viewModel)
                }
            }
        }
    }
    override fun onPause() {
        super.onPause()
        soundManager.pause()
    }

    override fun onResume() {
        super.onResume()
        soundManager.resume()
    }

    override fun onDestroy() {
        super.onDestroy()
        soundManager.release()
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SpaceDashTheme {
        GameNavigation()
    }
}