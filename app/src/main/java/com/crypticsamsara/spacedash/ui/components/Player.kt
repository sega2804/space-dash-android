package com.crypticsamsara.spacedash.ui.components

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import com.crypticsamsara.spacedash.ui.theme.NeonCyan
import com.crypticsamsara.spacedash.ui.theme.NeonPurple
import com.crypticsamsara.spacedash.ui.theme.StarWhite
import kotlin.io.path.Path

object PlayerRenderer {
    const val PLAYER_WIDTH = 60f
    const val PLAYER_HEIGHT =80f

    fun DrawScope.drawPlayer(x: Float, y: Float) {
        val centerX = x
        val topY = y
        val bottomY = y + PLAYER_HEIGHT
        val halfWidth = PLAYER_WIDTH / 2

        // Main body
        val bodyPath = Path().apply {
            moveTo(centerX, topY)// top point
            lineTo(centerX - halfWidth, bottomY) // bottom left point
            lineTo(centerX + halfWidth, bottomY) // bottom right point
            close()
        }

        // Draw main body with gradient effect (using multiple colors)
        drawPath(
            path = bodyPath,
            color = NeonCyan,
            style = Fill
        )

        // Cockpit (small circle at the top)
        drawCircle(
            color = StarWhite,
            radius = 12f,
            center = Offset(centerX, topY + 20f)
        )

        // Engine glow (left)
        drawCircle(
            color = NeonPurple.copy(alpha = 0.8f),
            radius = 8f,
            center = Offset(centerX - halfWidth + 10f, bottomY - 10f)
        )

        // Engine glow (right)
        drawCircle(
            color = NeonPurple.copy(alpha = 0.8f),
            radius = 8f,
            center = Offset(centerX - halfWidth + 10f, bottomY - 10f)
        )

        // Wings outline
        drawLine(
            color = StarWhite,
            start = Offset(centerX + halfWidth, bottomY),
            end = Offset(centerX, topY),
            strokeWidth = 2f
        )
    }
}