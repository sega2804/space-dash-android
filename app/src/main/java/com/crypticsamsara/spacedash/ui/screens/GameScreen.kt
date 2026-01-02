package com.crypticsamsara.spacedash.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.crypticsamsara.spacedash.ui.theme.SpaceBlack
import com.crypticsamsara.spacedash.ui.theme.StarWhite
import com.crypticsamsara.spacedash.viewmodel.GameViewModel
import java.nio.file.Files.size
import kotlin.random.Random

@Composable
fun GameScreen (
    viewModel: GameViewModel = viewModel()
) {
    val gameState by viewModel.gameState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SpaceBlack)
    ) {

        // Canvas for game rendering
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            // Starfield background (simple stars)
            repeat(50) {
                val x = Random.nextFloat() * canvasWidth
                val y = Random.nextFloat() * canvasHeight
                val radius = Random.nextFloat() *2f + 1f

                drawCircle(
                    color = StarWhite.copy(alpha = Random.nextFloat() * 0.5f + 0.5f),
                    radius = radius,
                    center = Offset(x, y)
                )
            }

            // Center marker (temporary to verify canvas is working)
            drawCircle(
                color = Color.Green,
                radius = 20f,
                center = Offset(canvasWidth / 2, canvasHeight / 2)
            )
        }

    }
}