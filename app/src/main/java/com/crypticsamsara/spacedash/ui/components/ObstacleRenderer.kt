package com.crypticsamsara.spacedash.ui.components

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.crypticsamsara.spacedash.model.Obstacle
import com.crypticsamsara.spacedash.model.ObstacleType
import com.crypticsamsara.spacedash.ui.theme.StarWhite

object ObstacleRenderer {

    fun DrawScope.drawObstacle(obstacle: Obstacle, screenWidth: Float) {
        val pixelX = obstacle.x * screenWidth
        val pixelY = obstacle.y

        when (obstacle.type) {
            ObstacleType.ASTEROID -> drawAsteroid(pixelX, pixelY, obstacle.size, obstacle.color)
            ObstacleType.ENEMY_SHIP -> drawEnemyShip(pixelX, pixelY, obstacle.size, obstacle.color)
            ObstacleType.SPACE_MINE -> drawSpaceMine(pixelX, pixelY, obstacle.size, obstacle.color)
        }
    }

    private fun DrawScope.drawAsteroid(x: Float, y: Float, size: Float, color: androidx.compose.ui.graphics.Color) {
        // Draw irregular asteroid shape
        val path = Path().apply {
            val radius = size / 2
            moveTo(x + radius, y)
            // Create jagged edges
            lineTo(x + radius * 1.2f, y + radius * 0.5f)
            lineTo(x + radius * 0.8f, y + radius)
            lineTo(x + radius * 0.3f, y + radius * 1.3f)
            lineTo(x - radius * 0.2f, y + radius * 0.9f)
            lineTo(x - radius * 0.8f, y + radius * 0.4f)
            lineTo(x - radius * 0.6f, y - radius * 0.3f)
            lineTo(x + radius * 0.2f, y - radius * 0.5f)
            close()
        }

        drawPath(
            path = path,
            color = color
        )

        // Add crater details
        drawCircle(
            color = color.copy(alpha = 0.5f),
            radius = size * 0.15f,
            center = Offset(x - size * 0.2f, y + size * 0.1f)
        )
    }

    private fun DrawScope.drawEnemyShip(x: Float, y: Float, size: Float, color: androidx.compose.ui.graphics.Color) {
        // Draw enemy ship (inverted triangle with wings)
        val halfSize = size / 2

        val bodyPath = Path().apply {
            moveTo(x, y + size) // Bottom point
            lineTo(x - halfSize, y) // Top left
            lineTo(x + halfSize, y) // Top right
            close()
        }

        drawPath(
            path = bodyPath,
            color = color
        )

        // Draw cockpit
        drawCircle(
            color = StarWhite.copy(alpha = 0.7f),
            radius = size * 0.2f,
            center = Offset(x, y + size * 0.6f)
        )

        // Draw wings
        drawLine(
            color = StarWhite,
            start = Offset(x - halfSize, y),
            end = Offset(x + halfSize, y),
            strokeWidth = 2f
        )
    }

    private fun DrawScope.drawSpaceMine(x: Float, y: Float, size: Float, color: androidx.compose.ui.graphics.Color) {
        // Draw space mine (circle with spikes)
        val radius = size / 2

        // Main body
        drawCircle(
            color = color,
            radius = radius,
            center = Offset(x, y)
        )

        // Draw spikes (8 directions)
        for (i in 0 until 8) {
            val angle = (i * 45f) * (Math.PI / 180f).toFloat()
            val startX = x + kotlin.math.cos(angle) * radius
            val startY = y + kotlin.math.sin(angle) * radius
            val endX = x + kotlin.math.cos(angle) * (radius + 10f)
            val endY = y + kotlin.math.sin(angle) * (radius + 10f)

            drawLine(
                color = color,
                start = Offset(startX, startY),
                end = Offset(endX, endY),
                strokeWidth = 3f
            )
        }

        // Inner circle detail
        drawCircle(
            color = StarWhite.copy(alpha = 0.5f),
            radius = radius * 0.4f,
            center = Offset(x, y)
        )
    }
}