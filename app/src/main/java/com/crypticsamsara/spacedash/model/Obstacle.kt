package com.crypticsamsara.spacedash.model

import androidx.compose.ui.graphics.Color
import com.crypticsamsara.spacedash.ui.theme.DangerRed
import com.crypticsamsara.spacedash.ui.theme.NeonPurple
import kotlin.random.Random

data class Obstacle(
    val id: Int,
    var x: Float,           // X position (percentage 0.0 - 1.0)
    var y: Float,           // Y position in pixels
    val speed: Float,       // Pixels per frame
    val size: Float,        // Size of obstacle
    val color: Color,       // Color for variety
    val type: ObstacleType  // Type of obstacle
)

enum class ObstacleType {
    ASTEROID,
    ENEMY_SHIP,
    SPACE_MINE
}

object ObstacleFactory {
    private var nextId = 0


    fun createRandomObstacle(
        screenWidth: Float,
        baseSpeed: Float,
        baseSize: Float,
        ): Obstacle {
        val id = nextId++
        val x = Random.nextFloat() // Random X position (0.0 - 1.0)
        val y = -100f // Start above screen

        val speedVariance = baseSpeed * 0.2f
        val speed = baseSpeed + Random.nextFloat() * speedVariance

        val sizeVariance = baseSize * 0.2f
        val size = baseSpeed + Random.nextFloat() * sizeVariance

        // Random obstacle type
        val type = ObstacleType.entries.toTypedArray().random()

        // Color based on type
        val color = when (type) {
            ObstacleType.ASTEROID -> Color(0xFFFF6B35)
            ObstacleType.ENEMY_SHIP -> DangerRed
            ObstacleType.SPACE_MINE -> NeonPurple
        }

        return Obstacle(
            id = id,
            x = x,
            y = y,
            speed = speed.coerceAtLeast(3f),
            size = size.coerceAtLeast(40f),
            color = color,
            type = type
        )
    }
}
