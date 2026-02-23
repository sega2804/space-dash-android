package com.crypticsamsara.spacedash.ui.components



import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import com.crypticsamsara.spacedash.model.Bullet
import com.crypticsamsara.spacedash.model.BulletType
import kotlin.math.cos
import kotlin.math.sin


object BulletRenderer {

    fun DrawScope.drawBullet(bullet: Bullet, animationTime: Float) {
        when (bullet.bulletType) {
            BulletType.BASIC_LASER -> drawBasicLaser(bullet)
            BulletType.RAPID_FIRE -> drawRapidFire(bullet)
            BulletType.SPREAD_SHOT -> drawSpreadShot(bullet)
            BulletType.MISSILE -> drawMissile(bullet, animationTime)
            BulletType.PLASMA_CANNON -> drawPlasmaCannon(bullet, animationTime)
        }
    }

    private fun DrawScope.drawBasicLaser(bullet: Bullet) {

        // outer glow
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    bullet.color.copy(alpha = 0.6f),
                    bullet.color.copy(alpha = 0.1f),
                    Color.Transparent
                )
            ),
            topLeft = Offset(bullet.x - bullet.width / 2 - 4, bullet.y),
            size = Size(bullet.width + 8, bullet.height)
        )

        // main body
        drawRect(
            color = bullet.color,
            topLeft = Offset(bullet.x - bullet.width / 2, bullet.y),
            size = Size(bullet.width, bullet.height)
        )

        // inner highLight
        drawLine(
            color = Color.White,
            start = Offset(bullet.x, bullet.y),
            end = Offset(bullet.x, bullet.y + bullet.height),
            strokeWidth = 2f
        )
    }

    private fun DrawScope.drawRapidFire(bullet: Bullet) {

        // Glow trail
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color.Transparent,
                    bullet.color.copy(alpha = 0.3f),
                    bullet.color.copy(alpha = 0.6f)
                )
            ),
            topLeft = Offset(bullet.x - bullet.width / 2, bullet.y - 15),
            size = Size(bullet.width, bullet.height + 15)
        )

        // Main body
        drawRect(
            color = bullet.color,
            topLeft = Offset(bullet.x - bullet.width / 2, bullet.y),
            size = Size(bullet.width, bullet.height)
        )

        // Bright core
        drawRect(
            color = Color.White.copy(alpha = 0.6f),
            topLeft = Offset(bullet.x - 2, bullet.y + 5),
            size = Size(4f, bullet.height - 10)
        )
    }

    private fun DrawScope.drawSpreadShot(bullet: Bullet) {
        // Particle trail effect
        for (i in 0..2) {
            val trailOffset = i * 10f
            drawCircle(
                color = bullet.color.copy(alpha = 0.3f - (i * 0.1f)),
                radius = bullet.width / 2 + (3 - i),
                center = Offset(
                    bullet.x - (bullet.velocityX * trailOffset * 0.5f),
                    bullet.y + (bullet.velocityY * trailOffset * 0.5f)
                )
            )
        }

        // Main bullet body - diamond shape
        val path = Path().apply {
            moveTo(bullet.x, bullet.y - bullet.height / 2) // Top
            lineTo(bullet.x + bullet.width / 2, bullet.y) // Right
            lineTo(bullet.x, bullet.y + bullet.height / 2) // Bottom
            lineTo(bullet.x - bullet.width / 2, bullet.y) // Left
            close()
        }

        drawPath(
            path = path,
            color = bullet.color
        )

        // Inner glow
        drawPath(
            path = path,
            color = Color.White.copy(alpha = 0.4f),
            style = Stroke(width = 1f)
        )
    }

    private fun DrawScope.drawMissile(bullet: Bullet, animationTime: Float) {
        // rotation angle based on velocity direction
        val angle = if (bullet.velocityX != 0f || bullet.velocityY != 0f) {
            Math.toDegrees(
                kotlin.math.atan2(bullet.velocityX.toDouble(), -bullet.velocityY.toDouble())
            ).toFloat()
        } else {
            0f
        }

        // Animated flame trail
        val flameLength = 20f + (sin(animationTime * 10) * 5)
        val flameWidth = 8f

        for (i in 0..3) {
            val trailAlpha = 0.6f - (i * 0.15f)
            val trailLength = flameLength - (i * 5)

            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFF6B35).copy(alpha = trailAlpha),
                        Color(0xFFFFAA00).copy(alpha = trailAlpha / 2),
                        Color.Transparent
                    )
                ),
                topLeft = Offset(
                    bullet.x - flameWidth / 2,
                    bullet.y + bullet.height + (i * 4)
                ),
                size = Size(flameWidth, trailLength)
            )
        }

        // Missile body (with rotation)
        rotate(angle, pivot = Offset(bullet.x, bullet.y + bullet.height / 2)) {
            // Main body
            drawRect(
                color = Color.DarkGray,
                topLeft = Offset(bullet.x - bullet.width / 2, bullet.y),
                size = Size(bullet.width, bullet.height * 0.7f)
            )

            // Nose cone
            val nosePath = Path().apply {
                moveTo(bullet.x, bullet.y - bullet.height * 0.3f)
                lineTo(bullet.x - bullet.width / 2, bullet.y)
                lineTo(bullet.x + bullet.width / 2, bullet.y)
                close()
            }
            drawPath(path = nosePath, color = bullet.color)

            // Fins
            drawRect(
                color = Color.Red,
                topLeft = Offset(bullet.x - bullet.width / 2 - 3, bullet.y + bullet.height * 0.5f),
                size = Size(3f, bullet.height * 0.3f)
            )
            drawRect(
                color = Color.Red,
                topLeft = Offset(bullet.x + bullet.width / 2, bullet.y + bullet.height * 0.5f),
                size = Size(3f, bullet.height * 0.3f)
            )
        }

        // Homing indicator
        if (bullet.targetObstacleId != null) {
            drawCircle(
                color = Color.Red.copy(alpha = 0.3f),
                radius = 20f,
                center = Offset(bullet.x, bullet.y + bullet.height / 2),
                style = Stroke(width = 2f)
            )
        }
    }

    private fun DrawScope.drawPlasmaCannon(bullet: Bullet, animationTime: Float){

        // outer plasma field
        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(
                    bullet.color.copy(alpha = 0.4f),
                    Color.Transparent
                ),
                center = Offset(bullet.x, bullet.y + bullet.height / 2),
                radius = bullet.width * 2
            ),
            topLeft = Offset(bullet.x - bullet.width, bullet.y - bullet.width),
            size = Size(bullet.width * 2, bullet.height + bullet.width * 2)
        )

        // Main plasma beam
        drawRect(
            brush = Brush.horizontalGradient(
                colors = listOf(
                    Color.Transparent,
                    bullet.color,
                    Color.White,
                    bullet.color,
                    Color.Transparent
                )
            ),
            topLeft = Offset(bullet.x - bullet.width / 2, bullet.y),
            size = Size(bullet.width, bullet.height)
        )

        // Electric arc effect
        val arcOffset = (sin(animationTime * 20) * 5)
        drawLine(
            color = Color.White.copy(alpha = 0.7f),
            start = Offset(bullet.x + arcOffset, bullet.y),
            end = Offset(bullet.x - arcOffset, bullet.y + bullet.height),
            strokeWidth = 1f
        )

        // Core beam
        drawRect(
            color = Color.White.copy(alpha = 0.8f),
            topLeft = Offset(bullet.x - 2, bullet.y),
            size = Size(4f, bullet.height)
        )

        // Energy particles
        for (i in 0..3) {
            val particleY = bullet.y + (i * bullet.height / 3) + (sin(animationTime * 15 + i) * 3)
            val particleX = bullet.x + (cos(animationTime * 15 + i) * 8)

            drawCircle(
                color = Color.White.copy(alpha = 0.6f),
                radius = 2f,
                center = Offset(particleX, particleY)
            )
        }
    }
}