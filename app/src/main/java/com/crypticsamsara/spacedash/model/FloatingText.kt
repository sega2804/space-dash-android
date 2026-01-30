package com.crypticsamsara.spacedash.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

data class FloatingText(
    val id: Int,
    val text: String,
    var position: Offset,
    val color: Color,
    val fontSize: Float,
    var alpha: Float = 1f,
    var life: Float = 1f,  // 1.0 = just spawned, 0.0 = dead
    val velocity: Offset = Offset(0f, -2f)  // Floats upward
    )

object FloatingTextFactory {
    private var nextId = 0

    fun createScoreText(
        x: Float,
        y: Float,
        points: Int,
        combo: Int = 1
    ): FloatingText {
        val id = nextId++

        // color based on points and combo
        val color = when {
            combo >= 25 -> Color(0xFFFF1744) // Red - legendary
            combo >= 10 -> Color(0xFFFFD700) // Gold - great
            combo >= 5 -> Color(0xFF00F0FF) // Cyan - combo
            else -> Color(0xFFFFFFFF) // White - normal
        }

        // size based on points
        val fontSize = when {
            points >= 200 -> 32f
            points >= 100 -> 28f
            points >= 50 -> 24f
            else -> 20f
        }
        return FloatingText(
            id = id,
            text = "+$points",
            position = Offset(x, y),
            color = color,
            fontSize = fontSize
        )
    }

    fun createComboText(
        x: Float,
        y: Float,
        combo: Int
    ): FloatingText {
        val id = nextId++

        val color = when {
            combo >= 25 -> Color(0xFFFF1744)
            combo >= 10 -> Color(0xFFFFD700)
            combo >= 5 -> Color(0xFF00F0FF)
            else -> Color(0xFFBF40BF)
        }

        return FloatingText(
            id = id,
            text = "${combo}x COMBO!",
            position = Offset(x, y - 50f), // Spawn slightly higher
            color = color,
            fontSize = 28f,
            velocity = Offset(0f, -3f) // Floats faster
        )
    }

    fun createMilestoneText(
        x: Float,
        y: Float,
        milestone: Int
    ): FloatingText {
        val id = nextId++

        return FloatingText(
            id = id,
            text = "🔥 ${milestone}x! 🔥",
            position = Offset(x, y - 80f),
            color = Color(0xFFFF6B35),
            fontSize = 36f,
            velocity = Offset(0f, -4f) // Floats even faster
        )
    }
}
