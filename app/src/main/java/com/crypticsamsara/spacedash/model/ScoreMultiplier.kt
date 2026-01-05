package com.crypticsamsara.spacedash.model

object ScoreMultiplier {

    fun calculateMultiplier(obstaclesDodged: Int): Float {
        return when {
            obstaclesDodged >= 50 -> 3.0f
            obstaclesDodged >= 30 -> 2.5f
            obstaclesDodged >= 20 -> 2.0f
            obstaclesDodged >= 10 -> 1.5f
            else -> 1.0f
        }
    }

    fun getMultiplierText(multiplier: Float): String {
        return when {
            multiplier >= 3.0f -> "🔥 LEGENDARY! ${multiplier}x"
            multiplier >= 2.5f -> "⚡ AMAZING! ${multiplier}x"
            multiplier >= 2.0f -> "✨ GREAT! ${multiplier}x"
            multiplier >= 1.5f -> "💫 GOOD! ${multiplier}x"
            else -> ""
        }
    }
}