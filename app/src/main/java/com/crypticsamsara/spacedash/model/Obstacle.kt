package com.crypticsamsara.spacedash.model

import androidx.compose.ui.graphics.Color
import com.crypticsamsara.spacedash.ui.theme.DangerRed
import com.crypticsamsara.spacedash.ui.theme.NeonPurple
import kotlin.random.Random

data class Obstacle(
    val id: Int,
    var x: Float,
    var y: Float,
    val speed: Float,
    val size: Float,
    val color: Color,
    val type: ObstacleType,
    val maxHP: Int = 1,
    var currentHP: Int = 1,
    val isDestructible: Boolean = true,
    val creditValue: Int = 10
)

enum class ObstacleType {
    // DESTRUCTIBLE OBSTACLES
    ASTEROID,
    COMET,
    SPACE_ROCK,
    ENEMY_SHIP,
    SPACE_MINE,
    // INDESTRUCTIBLE OBSTACLES
    BLACK_HOLE,
    LASER_BEAM
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
        val type = when (Random.nextInt(100)) {
            in 0..39 -> ObstacleType.ASTEROID
            in 40..64 -> ObstacleType.COMET
            in 65..79 -> ObstacleType.SPACE_ROCK
            in 80..89 -> ObstacleType.ENEMY_SHIP
            in 90..99 -> ObstacleType.SPACE_MINE
            else -> ObstacleType.ASTEROID
        }

        // Hp and properties based on obstacle type
        val (maxHP, finalSize, creditValue, isDestructible) = when (type) {
            ObstacleType.ASTEROID -> {
                Tuple4(1, size.coerceIn(40f, 60f), 10, true)
            }
            ObstacleType.COMET -> {
                Tuple4(2, size.coerceIn(55f, 75f), 20, true)
            }
            ObstacleType.SPACE_ROCK -> {
                Tuple4(3, size.coerceIn(70f, 90f), 40, true)
            }
            ObstacleType.ENEMY_SHIP -> {
                Tuple4(2, size.coerceIn(50f, 70f), 30, true)
            }
            ObstacleType.SPACE_MINE -> {
                Tuple4(1, size.coerceIn(35f, 50f), 15, true)
            }
            ObstacleType.BLACK_HOLE -> {
                Tuple4(999, size.coerceIn(80f, 100f), 0, false)
            }
            ObstacleType.LASER_BEAM -> {
                Tuple4(999, size.coerceIn(60f, 80f), 0, false)
            }
        }

        // Color based on type
        val color = when (type) {
            ObstacleType.ASTEROID -> Color(0xFFFF6B35)
            ObstacleType.COMET -> Color(0xFF00BCD4)
            ObstacleType.SPACE_ROCK -> Color(0xFF9E9E9E)
            ObstacleType.ENEMY_SHIP -> DangerRed
            ObstacleType.SPACE_MINE -> NeonPurple
            ObstacleType.BLACK_HOLE -> Color(0xFF000000)
            ObstacleType.LASER_BEAM -> Color(0xFFFF1744)
        }

        return Obstacle(
            id = id,
            x = x,
            y = y,
            speed = speed.coerceAtLeast(3f),
            size = finalSize,
            color = color,
            type = type,
            maxHP = maxHP,
            currentHP = maxHP,
            isDestructible = isDestructible,
            creditValue = creditValue
        )
    }

    // Helper function to create specific obstacle types
    fun createAsteroid(x: Float, y: Float, speed: Float): Obstacle {
        return Obstacle(
            id = nextId++,
            x = x,
            y = y,
            speed = speed,
            size = 50f,
            color = Color(0xFFFF6B35),
            type = ObstacleType.ASTEROID,
            maxHP = 1,
            currentHP = 1,
            isDestructible = true,
            creditValue = 10
        )
    }

    fun createComet(x: Float, y: Float, speed: Float): Obstacle {
        return Obstacle(
            id = nextId++,
            x = x,
            y = y,
            speed = speed,
            size = 65f,
            color = Color(0xFF00BCD4),
            type = ObstacleType.COMET,
            maxHP = 2,
            currentHP = 2,
            isDestructible = true,
            creditValue = 20
        )
    }

    fun createSpaceRock(x: Float, y: Float, speed: Float): Obstacle {
        return Obstacle(
            id = nextId++,
            x = x,
            y = y,
            speed = speed,
            size = 80f,
            color = Color(0xFF9E9E9E),
            type = ObstacleType.SPACE_ROCK,
            maxHP = 3,
            currentHP = 3,
            isDestructible = true,
            creditValue = 40
        )
    }
}

// Helper data class for multiple return values
private data class Tuple4<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)
