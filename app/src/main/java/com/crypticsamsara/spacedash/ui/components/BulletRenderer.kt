package com.crypticsamsara.spacedash.ui.components

import android.R.attr.y
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.crypticsamsara.spacedash.model.Bullet
import com.crypticsamsara.spacedash.model.BulletType


object BulletRenderer {

    fun DrawScope.drawBullet(bullet: Bullet) {
        when (bullet.bulletType) {
            BulletType.BASIC_LASER -> drawBasicLaser(bullet)
            BulletType.RAPID_FIRE -> drawRapidFire(bullet)
            BulletType.SPREAD_SHOT -> drawSpreadShot(bullet)
            BulletType.MISSILE -> drawMissile(bullet)
            BulletType.PLASMA_CANNON -> drawPlasmaCannon(bullet)
        }
    }

    private fun DrawScope.drawBasicLaser(bullet: Bullet) {

        // outer glow
        drawRoundRect(
            color = bullet.color.copy(alpha = 0.3f),
            topLeft = Offset(
                x = bullet.x - bullet.width / 2 - 2f,
                y = bullet.y - bullet.height / 2 - 2f
            ),
            size = Size(bullet.width + 4f, bullet.height + 4f),
            cornerRadius = CornerRadius(4f, 4f)
        )

        // main body
        drawRoundRect(
            color = bullet.color,
            topLeft = Offset(
                x = bullet.x - bullet.width / 2,
                y = bullet.y - bullet.height / 2
            ),
            size = Size(bullet.width, bullet.height),
            cornerRadius = CornerRadius(3f, 3f)
        )

        // inner highLight
        drawRoundRect(
            color = Color.White.copy(alpha = 0.6f),
            topLeft = Offset(
                x = bullet.x - bullet.width / 4,
                y = bullet.y - bullet.height / 4
            ),
            size = Size(bullet.width / 2, bullet.height / 3),
            cornerRadius = CornerRadius(2f, 2f)
        )
    }

    private fun DrawScope.drawRapidFire(bullet: Bullet) {

        // Glow trail
        drawRoundRect(
            color = bullet.color.copy (alpha = 0.2f),
            topLeft = Offset(
                x = bullet.x - bullet.width / 2 - 1f,
                y = bullet.y - bullet.height / 2
            ),
            size = Size(bullet.width + 2f, bullet.height + 10f),
            cornerRadius = CornerRadius(3f, 3f)
        )

        // Main body
        drawRoundRect(
            color = bullet.color,
            topLeft = Offset(
                x = bullet.x - bullet.width / 2,
                y = bullet.y - bullet.height / 2
            ),
            size = Size(bullet.width, bullet.height),
            cornerRadius = CornerRadius(3f, 3f)
        )
    }

    private fun DrawScope.drawSpreadShot(bullet: Bullet) {
        // outer glow
        drawCircle(
            color = bullet.color.copy(alpha = 0.3f),
            radius = bullet.width + 3f,
            center = Offset(bullet.x, bullet.y)
        )

        // Main body
        drawRoundRect(
            color = bullet.color,
            topLeft = Offset(
                x = bullet.x - bullet.width / 2,
                y = bullet.y - bullet.height / 2
            ),
            size = Size(bullet.width, bullet.height),
        )

        // Energy core
        drawCircle(
            color = Color.White.copy(alpha = 0.7f),
            radius = bullet.width / 3,
            center = Offset(bullet.x, bullet.y)
        )
    }

    private fun DrawScope.drawMissile(bullet: Bullet) {
        // Exhaust trail (fading circles behind)
        for (i in 1..3) {
            drawCircle(
                color = Color(0xFFFF6B35).copy(alpha = 0.3f / i),
                radius = bullet.width / 2 + i * 2f,
                center = Offset(bullet.x, bullet.y + (i * 8f))
            )
        }

        // Main body
        drawRoundRect(
            color = bullet.color,
            topLeft = Offset(
                x = bullet.x - bullet.width / 2,
                y = bullet.y - bullet.height / 2
            ),
            size = Size(bullet.width, bullet.height),
            cornerRadius = CornerRadius(4f, 4f)
        )

        // Warhead tip
        drawCircle(
            color = Color.White.copy(alpha = 0.8f),
            radius = bullet.width / 2,
            center = Offset(bullet.x, bullet.y - bullet.height / 2)
        )

        // Stripe details
        drawLine(
            color = Color.Black.copy(alpha = 0.3f),
            start = Offset(bullet.x - bullet.width / 2, bullet.y),
            end = Offset(bullet.x + bullet.width / 2, bullet.y),
            strokeWidth = 2f
        )
    }

    private fun DrawScope.drawPlasmaCannon(bullet: Bullet){

        // outer plasma field
        drawCircle(
            color = Color.Cyan.copy(alpha = 0.2f),
            radius = bullet.width * 2,
            center = Offset(bullet.x , bullet.y),
        )

        // Middle layer
        drawCircle(
            color = Color.Cyan.copy(alpha = 0.4f),
            radius = bullet.width * 1.5f,
            center = Offset(bullet.x , bullet.y),
        )

        // core energy
        drawCircle(
            color = Color.Cyan,
            radius = bullet.width,
            center = Offset(bullet.x, bullet.y)
        )

        // white hot center
        drawCircle(
            color = Color.White.copy(alpha = 0.9f),
            radius = bullet.width / 2,
            center = Offset(bullet.x, bullet.y)
        )
    }

    fun DrawScope.drawBullets(bullets: List<Bullet>) {
        bullets.forEach { bullet ->
            if (bullet.isActive) {
                drawBullet(bullet)
            }
        }
    }
}