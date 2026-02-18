package com.crypticsamsara.spacedash.data.game

import androidx.compose.ui.graphics.Color
import kotlin.math.min

object DifficultyManager {

    // Base values
    private const val BASE_SPAWN_INTERVAL = 2000L // 2 secs
    private const val BASE_OBSTACLE_SPEED = 5f
    private const val BASE_OBSTACLE_SIZE = 55f

    // Maximum values (caps)
    private const val MIN_SPAWN_INTERVAL = 600L // 0.6 sec
    private const val MAX_OBSTACLE_SPEED = 12f
    private const val MAX_OBSTACLE_SIZE = 70f

    // Difficulty scaling constants
    private const val TIME_TO_MAX_DIFFICULTY = 180000L // 3 min to max difficulty

    // difficulty calculation based on survival time
    // 0.0 = start, 1.0 = maximum difficulty
    fun calculateDifficultyMultiplier(survivalTimeMs: Long): Float {
        val progress = survivalTimeMs.toFloat() / TIME_TO_MAX_DIFFICULTY.toFloat()
        return min(progress, 1.0f)// cap it at 10
    }

    // Current spawn interval (time between obstacle spawns)
    // Decreases over time
    fun getSpawnInterval(survivalTimeMs: Long): Long {
        val multiplier = calculateDifficultyMultiplier(survivalTimeMs)

        // from base to minimum
        val interval = BASE_SPAWN_INTERVAL - (multiplier * (BASE_SPAWN_INTERVAL - MIN_SPAWN_INTERVAL))
        return interval.toLong()
    }

    // Obstacle speed multiplier, increases over time
    // i.e. obstacles are supposed to move faster
    fun getObstacleSpeed(survivalTimeMs: Long): Float {
        val multiplier = calculateDifficultyMultiplier(survivalTimeMs)

        // base to maximum
        return BASE_OBSTACLE_SPEED + (multiplier * (MAX_OBSTACLE_SPEED - BASE_OBSTACLE_SPEED))
    }

    // obstacle size, increase slightly over time making it harder to dodge
    fun getObstacleSize(survivalTimeMs: Long): Float {
        val multiplier = calculateDifficultyMultiplier(survivalTimeMs)

        // base to maximum
        return BASE_OBSTACLE_SIZE + (multiplier * (MAX_OBSTACLE_SIZE - BASE_OBSTACLE_SIZE))
    }

    // Difficulty level as a 1-10 scale for display
    fun getDifficultyLevel(survivalTimeMs: Long): Int {
        val multiplier = calculateDifficultyMultiplier(survivalTimeMs)
        return (multiplier * 10).toInt() + 1
    }

    // Difficulty description
    fun getDifficultyDescription(survivalTimeMs: Long): String {
        val level = getDifficultyLevel(survivalTimeMs)
        return when (level) {
            1 -> "Easy"
            in 2..3 -> "Normal"
            in 4..5 -> "Getting Harder"
            in 6..7 -> "Challenging"
            in 8..9 -> "Hard"
            else -> "EXTREME"
        }
    }


    // Difficulty color
    fun getDifficultyColor(survivalTimeMs: Long): Color {
        val level = getDifficultyLevel(survivalTimeMs)
        return when (level) {
            1 -> Color(0xFF4CAF50)
            in 2..3 -> Color(0xFF8BC34A)
            in 4..5 -> Color(0xFFFFC107)
            in 6..7 -> Color(0xFFFF9800)
            in 8..9 -> Color(0xFFFF5722)
            else -> Color(0xFFF44336)
        }
    }
}








