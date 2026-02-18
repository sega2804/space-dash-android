package com.crypticsamsara.spacedash.ui.components

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.crypticsamsara.spacedash.model.Obstacle
import com.crypticsamsara.spacedash.model.ObstacleType
import com.crypticsamsara.spacedash.ui.theme.DangerRed
import com.crypticsamsara.spacedash.ui.theme.StarWhite
import kotlin.math.cos
import kotlin.math.sin

object ObstacleRenderer {

    fun DrawScope.drawObstacle(obstacle: Obstacle, screenWidth: Float) {
        val pixelX = obstacle.x * screenWidth
        val pixelY = obstacle.y
        val radius = obstacle.size / 2f

        when (obstacle.type) {
            ObstacleType.ASTEROID -> drawAsteroid(pixelX, pixelY, radius,  obstacle.color)
            ObstacleType.ENEMY_SHIP -> drawEnemyShip(pixelX, pixelY, radius, obstacle.color)
            ObstacleType.SPACE_MINE -> drawSpaceMine(pixelX, pixelY, radius,  obstacle.color)
            ObstacleType.COMET -> drawComet(pixelX, pixelY, radius, obstacle.color)
            ObstacleType.SPACE_ROCK  -> drawSpaceRock(pixelX, pixelY, radius, obstacle.color)
            ObstacleType.BLACK_HOLE  -> drawBlackHole(pixelX, pixelY, radius)
            ObstacleType.LASER_BEAM  -> drawLaserBeam(pixelX, pixelY, radius, obstacle.color)

        }

        // hp bar
        if (obstacle.maxHP > 1) {
            drawHpBar(pixelX, pixelY, obstacle)
        }
    }

    private fun DrawScope.drawAsteroid(x: Float, y: Float, size: Float, color: Color) {
        //  irregular asteroid shape
        val path = Path().apply {
            val radius = size / 2
            moveTo(x + radius, y)
            //  jagged edges
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

        //  crater details
        drawCircle(
            color = color.copy(alpha = 0.5f),
            radius = size * 0.15f,
            center = Offset(x - size * 0.2f, y + size * 0.1f)
        )
    }

    private fun DrawScope.drawEnemyShip(x: Float, y: Float, size: Float, color: Color) {
        //  enemy ship
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

        //  cockpit
        drawCircle(
            color = StarWhite.copy(alpha = 0.7f),
            radius = size * 0.2f,
            center = Offset(x, y + size * 0.6f)
        )

        //  wings
        drawLine(
            color = StarWhite,
            start = Offset(x - halfSize, y),
            end = Offset(x + halfSize, y),
            strokeWidth = 2f
        )
    }

    private fun DrawScope.drawSpaceMine(x: Float, y: Float, size: Float, color: Color) {
        //  space mine
        val radius = size / 2

        // Main body
        drawCircle(
            color = color,
            radius = radius,
            center = Offset(x, y)
        )

        //  spikes (8 directions)
        for (i in 0 until 8) {
            val angle = (i * 45f) * (Math.PI / 180f).toFloat()
            val startX = x + cos(angle) * radius
            val startY = y + sin(angle) * radius
            val endX = x + cos(angle) * (radius + 10f)
            val endY = y + sin(angle) * (radius + 10f)

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

    private fun DrawScope.drawComet(x: Float, y: Float, radius: Float, color: Color) {
        // Tail (3 trailing circles getting smaller)
        drawCircle(
            color = color.copy(alpha = 0.1f),
            radius = radius * 0.9f,
            center = Offset(x, y + radius * 1.2f)
        )
        drawCircle(
            color = color.copy(alpha = 0.2f),
            radius = radius * 0.6f,
            center = Offset(x, y + radius * 0.7f)
        )
        // Outer glow
        drawCircle(
            color = color.copy(alpha = 0.3f),
            radius = radius + 6f,
            center = Offset(x, y)
        )
        // Main body
        drawCircle(
            color = color,
            radius = radius,
            center = Offset(x, y)
        )
        // Ice-glint highlight
        drawCircle(
            color = Color.White.copy(alpha = 0.6f),
            radius = radius * 0.35f,
            center = Offset(x - radius * 0.25f, y - radius * 0.25f)
        )
    }

    private fun DrawScope.drawSpaceRock(x: Float, y: Float, radius: Float, color: Color) {
        // Outer glow (dim — this is a dark rock)
        drawCircle(
            color = color.copy(alpha = 0.15f),
            radius = radius + 6f,
            center = Offset(x, y)
        )

        // Irregular polygon body (8-point)
        val path = Path()
        val points = 8
        for (i in 0 until points) {
            val angle = (i * (360f / points)) * (Math.PI / 180f).toFloat()
            val jitter = if (i % 2 == 0) 0.85f else 1.0f
            val px = x + radius * jitter * cos(angle)
            val py = y + radius * jitter * sin(angle)
            if (i == 0) path.moveTo(px, py) else path.lineTo(px, py)
        }
        path.close()
        drawPath(path, color = color)

        // Crack lines
        drawLine(
            color = Color.Black.copy(alpha = 0.4f),
            start = Offset(x - radius * 0.5f, y - radius * 0.3f),
            end = Offset(x + radius * 0.2f, y + radius * 0.5f),
            strokeWidth = 2f
        )
        drawLine(
            color = Color.Black.copy(alpha = 0.3f),
            start = Offset(x + radius * 0.1f, y - radius * 0.5f),
            end = Offset(x - radius * 0.2f, y + radius * 0.2f),
            strokeWidth = 1.5f
        )
        // Dim highlight
        drawCircle(
            color = Color.White.copy(alpha = 0.1f),
            radius = radius * 0.4f,
            center = Offset(x - radius * 0.2f, y - radius * 0.2f)
        )
    }

    private fun DrawScope.drawBlackHole(x: Float, y: Float, radius: Float) {
        // Accretion disc rings
        drawCircle(
            color = Color(0xFF7B2FBE).copy(alpha = 0.15f),
            radius = radius + 20f,
            center = Offset(x, y)
        )
        drawCircle(
            color = Color(0xFF7B2FBE).copy(alpha = 0.3f),
            radius = radius + 10f,
            center = Offset(x, y)
        )
        drawCircle(
            color = Color(0xFFBB86FC).copy(alpha = 0.5f),
            radius = radius + 4f,
            center = Offset(x, y)
        )
        // Event horizon
        drawCircle(
            color = Color.Black,
            radius = radius,
            center = Offset(x, y)
        )
        // Inner purple glint
        drawCircle(
            color = Color(0xFF6200EE).copy(alpha = 0.6f),
            radius = radius * 0.3f,
            center = Offset(x, y)
        )
    }

    private fun DrawScope.drawLaserBeam(x: Float, y: Float, radius: Float, color: Color) {
        // Wide outer glow
        drawRect(
            color = color.copy(alpha = 0.1f),
            topLeft = Offset(x - radius, y - radius * 2f),
            size = Size(radius * 2f, radius * 4f)
        )
        // Mid glow
        drawRect(
            color = color.copy(alpha = 0.3f),
            topLeft = Offset(x - radius * 0.6f, y - radius * 2f),
            size = Size(radius * 1.2f, radius * 4f)
        )
        // Core beam
        drawRect(
            color = color,
            topLeft = Offset(x - radius * 0.25f, y - radius * 2f),
            size = Size(radius * 0.5f, radius * 4f)
        )
        // Bright center line
        drawRect(
            color = Color.White.copy(alpha = 0.7f),
            topLeft = Offset(x - radius * 0.08f, y - radius * 2f),
            size = Size(radius * 0.16f, radius * 4f)
        )
    }

    // HP BAR

    private fun DrawScope.drawHpBar(x: Float, y: Float, obstacle: Obstacle) {
        val barWidth  = obstacle.size
        val barHeight = 5f
        val barTop    = y - obstacle.size / 2f - 12f
        val barLeft   = x - barWidth / 2f

        val hpPercent = obstacle.currentHP.toFloat() / obstacle.maxHP.toFloat()

        val hpColor = when {
            hpPercent > 0.66f -> Color(0xFF00E676)
            hpPercent > 0.33f -> Color(0xFFFFD600)
            else -> DangerRed
        }

        // Background track
        drawRect(
            color = Color.Black.copy(alpha = 0.6f),
            topLeft = Offset(barLeft, barTop),
            size = Size(barWidth, barHeight)
        )

        // Filled portion
        drawRect(
            color = hpColor,
            topLeft = Offset(barLeft, barTop),
            size = Size(barWidth * hpPercent, barHeight)
        )

        // Thin outline
        drawRect(
            color = Color.White.copy(alpha = 0.3f),
            topLeft = Offset(barLeft, barTop),
            size = Size(barWidth, barHeight),
            style = Stroke(width = 1f)
        )
    }
}