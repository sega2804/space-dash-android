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

    // Obstacle dimensions
    const val MIN_SIZE = 40f
    const val MAX_SIZE = 70f
    const val MIN_SPEED = 3f
    const val MAX_SPEED = 8f

    fun createRandomObstacle(screenWidth: Float): Obstacle {
        val id = nextId++
        val x = Random.nextFloat() // Random X position (0.0 - 1.0)
        val y = -100f // Start above screen
        val speed = Random.nextFloat() * (MAX_SPEED - MIN_SPEED) + MIN_SPEED
        val size = Random.nextFloat() * (MAX_SIZE - MIN_SIZE) + MIN_SIZE

        // Random obstacle type
        val type = ObstacleType.values().random()

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
            speed = speed,
            size = size,
            color = color,
            type = type
        )
    }

}
