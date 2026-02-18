package com.crypticsamsara.spacedash.ui.components

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import com.crypticsamsara.spacedash.model.PowerUp
import com.crypticsamsara.spacedash.model.PowerUpType
import kotlin.math.cos
import kotlin.math.sin

object PowerUpRenderer {

    fun DrawScope.drawPowerUp(powerUp: PowerUp, screenWidth: Float) {
        if (powerUp.isCollected) return

        val pixelX = powerUp.x * screenWidth
        val pixelY = powerUp.y

        when (powerUp.type) {
            PowerUpType.AMMO_REFILL -> drawAmmoRefill(pixelX, pixelY, powerUp.size, powerUp.color)
            PowerUpType.AMMO_PACK -> drawAmmoPack(pixelX, pixelY, powerUp.size, powerUp.color)
            PowerUpType.SHIELD -> drawShield(pixelX, pixelY, powerUp.size, powerUp.color)
            PowerUpType.DOUBLE_DAMAGE -> drawDoubleDamage(pixelX, pixelY, powerUp.size, powerUp.color)
            PowerUpType.RAPID_FIRE -> drawRapidFire(pixelX, pixelY, powerUp.size, powerUp.color)
            PowerUpType.SCORE_MULTIPLIER -> drawScoreMultiplier(pixelX, pixelY, powerUp.size, powerUp.color)
        }
    }

    private fun DrawScope.drawAmmoRefill(x: Float, y: Float, size: Float, color: Color) {
        val radius = size / 2

        // Outer glow ring
        drawCircle(
            color = color.copy(alpha = 0.2f),
            radius = radius + 8f,
            center = Offset(x, y)
        )

        // Middle ring
        drawCircle(
            color = color.copy(alpha = 0.4f),
            radius = radius + 4f,
            center = Offset(x, y)
        )

        // Main circle
        drawCircle(
            color = color,
            radius = radius,
            center = Offset(x, y)
        )

        // bullet icon
        val bulletWidth = radius * 0.3f
        val bulletHeight = radius * 0.6f

        drawRect(
            color = Color.White,
            topLeft = Offset(x - bulletWidth / 2, y - bulletHeight / 2),
            size = Size(bulletWidth, bulletHeight)
        )

        // "+" symbol
        val plusSize = radius * 0.8f
        drawLine(
            color = Color.White.copy(alpha = 0.9f),
            start = Offset(x - plusSize / 2, y),
            end = Offset(x + plusSize / 2, y),
            strokeWidth = 3f
        )
        drawLine(
            color = Color.White.copy(alpha = 0.9f),
            start = Offset(x, y - plusSize / 2),
            end = Offset(x, y + plusSize / 2),
            strokeWidth = 3f
        )

        // Pulsing effect
        drawCircle(
            color = Color.White.copy(alpha = 0.2f),
            radius = radius * 0.7f,
            center = Offset(x, y)
        )
    }

    private fun DrawScope.drawAmmoPack(x: Float, y: Float, size: Float, color: Color) {
        val radius = size / 2

        // Multiple glowing rings
        drawCircle(
            color = color.copy(alpha = 0.15f),
            radius = radius + 12f,
            center = Offset(x, y)
        )
        drawCircle(
            color = color.copy(alpha = 0.3f),
            radius = radius + 6f,
            center = Offset(x, y)
        )

        // Main hexagon shape
        val hexPath = Path().apply {
            for (i in 0..5) {
                val angle = (i * 60f - 30f) * (Math.PI / 180f).toFloat()
                val pointX = x + radius * cos(angle)
                val pointY = y + radius * sin(angle)
                if (i == 0) {
                    moveTo(pointX, pointY)
                } else {
                    lineTo(pointX, pointY)
                }
            }
            close()
        }

        drawPath(
            path = hexPath,
            color = color
        )

        // Inner glow
        drawCircle(
            color = Color.White.copy(alpha = 0.5f),
            radius = radius * 0.6f,
            center = Offset(x, y)
        )

        // Multiple bullets icon
        for (i in -1..1) {
            val bulletWidth = radius * 0.2f
            val bulletHeight = radius * 0.5f
            val offsetX = i * radius * 0.3f

            drawRect(
                color = Color.White,
                topLeft = Offset(x + offsetX - bulletWidth / 2, y - bulletHeight / 2),
                size = Size(bulletWidth, bulletHeight)
            )
        }
    }

    private fun DrawScope.drawShield(x: Float, y: Float, size: Float, color: Color) {
        val radius = size / 2

        // Pulsing outer glow
        drawCircle(
            color = color.copy(alpha = 0.2f),
            radius = radius + 6f,
            center = Offset(x, y)
        )

        // Main circle
        drawCircle(
            color = color,
            radius = radius,
            center = Offset(x, y)
        )

        // Shield shape
        val shieldPath = Path().apply {
            moveTo(x, y - radius * 0.6f)
            lineTo(x - radius * 0.5f, y)
            lineTo(x, y + radius * 0.6f)
            lineTo(x + radius * 0.5f, y)
            close()
        }

        drawPath(
            path = shieldPath,
            color = Color.White,
            style = Stroke(width = 3f)
        )
    }

    private fun DrawScope.drawDoubleDamage(x: Float, y: Float, size: Float, color: Color) {
        val radius = size / 2

        // Outer glow
        drawCircle(
            color = color.copy(alpha = 0.2f),
            radius = radius + 8f,
            center = Offset(x, y)
        )

        // Main circle
        drawCircle(
            color = color,
            radius = radius,
            center = Offset(x, y)
        )

        // "2X" text representation with lines
        val lineSize = radius * 0.4f
        drawLine(
            color = Color.White,
            start = Offset(x - radius * 0.4f, y - lineSize / 2),
            end = Offset(x - radius * 0.1f, y - lineSize / 2),
            strokeWidth = 3f
        )
        drawLine(
            color = Color.White,
            start = Offset(x - radius * 0.1f, y - lineSize / 2),
            end = Offset(x - radius * 0.1f, y),
            strokeWidth = 3f
        )
        drawLine(
            color = Color.White,
            start = Offset(x - radius * 0.4f, y),
            end = Offset(x - radius * 0.1f, y),
            strokeWidth = 3f
        )
        drawLine(
            color = Color.White,
            start = Offset(x - radius * 0.4f, y),
            end = Offset(x - radius * 0.4f, y + lineSize / 2),
            strokeWidth = 3f
        )
        drawLine(
            color = Color.White,
            start = Offset(x - radius * 0.4f, y + lineSize / 2),
            end = Offset(x - radius * 0.1f, y + lineSize / 2),
            strokeWidth = 3f
        )

        // Draw "X"
        drawLine(
            color = Color.White,
            start = Offset(x + radius * 0.1f, y - lineSize / 2),
            end = Offset(x + radius * 0.4f, y + lineSize / 2),
            strokeWidth = 3f
        )
        drawLine(
            color = Color.White,
            start = Offset(x + radius * 0.4f, y - lineSize / 2),
            end = Offset(x + radius * 0.1f, y + lineSize / 2),
            strokeWidth = 3f
        )
    }

    private fun DrawScope.drawRapidFire(x: Float, y: Float, size: Float, color: Color) {
        val radius = size / 2

        // Outer glow
        drawCircle(
            color = color.copy(alpha = 0.2f),
            radius = radius + 8f,
            center = Offset(x, y)
        )

        // Main circle
        drawCircle(
            color = color,
            radius = radius,
            center = Offset(x, y)
        )

        // three small bullets to represent rapid fire
        val bulletSpacing = radius * 0.5f
        for (i in -1..1) {
            val bulletY = y + i * bulletSpacing
            val bulletWidth = radius * 0.25f
            val bulletHeight = radius * 0.4f

            drawRect(
                color = Color.White,
                topLeft = Offset(x - bulletWidth / 2, bulletY - bulletHeight / 2),
                size = Size(bulletWidth, bulletHeight)
            )

            // Speed lines
            drawLine(
                color = Color.White.copy(alpha = 0.6f),
                start = Offset(x - radius * 0.6f, bulletY),
                end = Offset(x - radius * 0.3f, bulletY),
                strokeWidth = 2f
            )
        }
    }

    private fun DrawScope.drawScoreMultiplier(x: Float, y: Float, size: Float, color: Color) {
        val radius = size / 2

        // Outer glow with rotation effect
        drawCircle(
            color = color.copy(alpha = 0.2f),
            radius = radius + 8f,
            center = Offset(x, y)
        )

        // Main circle
        drawCircle(
            color = color,
            radius = radius,
            center = Offset(x, y)
        )

        // star shape for points/score
        val starPath = Path().apply {
            val points = 5
            val outerRadius = radius * 0.6f
            val innerRadius = radius * 0.3f

            for (i in 0 until points * 2) {
                val angle = (i * 36f - 90f) * (Math.PI / 180f).toFloat()
                val r = if (i % 2 == 0) outerRadius else innerRadius
                val pointX = x + r * cos(angle)
                val pointY = y + r * sin(angle)

                if (i == 0) {
                    moveTo(pointX, pointY)
                } else {
                    lineTo(pointX, pointY)
                }
            }
            close()
        }

        drawPath(
            path = starPath,
            color = Color.White
        )
    }

    // Helper for all power-ups
    fun DrawScope.drawPowerUps(powerUps: List<PowerUp>, screenWidth: Float) {
        powerUps.forEach { powerUp ->
            if (!powerUp.isCollected) {
                drawPowerUp(powerUp, screenWidth)
            }
        }
    }
}






