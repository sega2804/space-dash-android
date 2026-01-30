package com.crypticsamsara.spacedash.ui.components

import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import com.crypticsamsara.spacedash.model.FloatingText

object FloatingTextRenderer  {
    fun DrawScope.drawFloatingTexts(floatingTexts: List<FloatingText>) {
        floatingTexts.forEach { text ->
            drawFloatingText(text)
        }
    }

    private fun DrawScope.drawFloatingText(text: FloatingText) {
        drawIntoCanvas { canvas ->
            val paint = android.graphics.Paint().apply {
                color = text.color.copy(alpha = text.alpha).toArgb()
                textSize = text.fontSize * density
                textAlign = android.graphics.Paint.Align.CENTER
                isAntiAlias = true

                // Add shadow for better visibility
                setShadowLayer(
                    4f * density,
                    0f,
                    2f * density,
                    android.graphics.Color.BLACK
                )

                // Make text bold
                typeface = android.graphics.Typeface.DEFAULT_BOLD
            }

            canvas.nativeCanvas.drawText(
                text.text,
                text.position.x,
                text.position.y,
                paint
            )
        }
    }
}