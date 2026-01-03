package com.crypticsamsara.spacedash.model

import androidx.compose.ui.geometry.Offset
import kotlin.random.Random

data class Star(
    val position: Offset,
    val radius: Float,
    val alpha: Float
)

object StarFactory {
    fun generateStarfield(width: Float, height: Float, count: Int = 100): List<Star> {
        return List(count) {
            Star(
                position = Offset(
                    x = Random.nextFloat() * width,
                    y = Random.nextFloat() * height
                ),
                radius = Random.nextFloat() * 2f + 1f,
                alpha = Random.nextFloat() * 0.5f + 0.5f
            )
        }
    }
}
