package com.crypticsamsara.spacedash.model

import android.R.attr.height
import androidx.compose.ui.geometry.Offset
import kotlin.random.Random

data class Star(
    var position: Offset,
    val radius: Float,
    val alpha: Float,
    val speed: Float
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
                alpha = Random.nextFloat() * 0.5f + 0.5f,
                speed = Random.nextFloat() * 0.5f + 0.2f
            )
        }
    }

    fun updateStars(stars: List<Star>, height: Float) {
        stars.forEach { star ->
            // Move star down
            star.position = Offset(
                star.position.x,
                star.position.y + star.speed
            )

            // Wrap around when off-screen
            if (star.position.y > height) {
                star.position = Offset(
                    star.position.x,
                    -10f
                )
            }
        }
    }

}
