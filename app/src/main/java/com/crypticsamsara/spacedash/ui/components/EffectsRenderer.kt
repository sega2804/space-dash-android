package com.crypticsamsara.spacedash.ui.components

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.crypticsamsara.spacedash.ui.theme.DangerRed
import com.crypticsamsara.spacedash.ui.theme.NeonPurple

object EffectsRenderer {
    fun DrawScope.drawExplosion(x: Float, y: Float, frame: Int, maxFrames: Int = 20) {
        val progress = frame.toFloat() / maxFrames
        val radius = progress * 100f
        val alpha = 1f - progress

        // Multiple expanding circles
        repeat(5) { i ->
            val offset = i * 10f
            drawCircle(
                color = DangerRed.copy(alpha = alpha * 0.7f),
                radius = radius + offset,
                center = Offset(x, y)
            )
        }

        // Particles
        repeat(20) { i ->
            val angle = (i * 18f) * (Math.PI / 180f).toFloat()
            val distance = progress * 80f
            val particleX = x + kotlin.math.cos(angle) * distance
            val particleY = y + kotlin.math.sin(angle) * distance

            drawCircle(
                color = NeonPurple.copy(alpha = alpha),
                radius = 3f,
                center = Offset(particleX, particleY)
            )
        }
    }
}