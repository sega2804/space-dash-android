package com.crypticsamsara.spacedash.ui.effects

import android.provider.SyncStateContract.Helpers.update
import androidx.compose.runtime.mutableStateListOf
import com.crypticsamsara.spacedash.model.FloatingText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.NonCancellable.isActive
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FloatingTextManager {

    val floatingTexts = mutableStateListOf<FloatingText>()

    private var isUpdating = false


     // Add a new floating text

    fun addFloatingText(text: FloatingText) {
        floatingTexts.add(text)
    }

     // Start updating floating texts

    fun startUpdating(scope: CoroutineScope) {
        if (isUpdating) return
        isUpdating = true

        scope.launch {
            while (isActive) {
                update()
                delay(16L) // ~60 FPS
            }
        }
    }

      // Update all floating texts

    private fun update() {
        floatingTexts.forEach { text ->
            // Move upward
            text.position = text.position.copy(
                x = text.position.x + text.velocity.x,
                y = text.position.y + text.velocity.y
            )

            // Fade out over time
            text.life -= 0.015f // Lasts about 1 second at 60 FPS
            text.alpha = text.life.coerceAtLeast(0f)
        }

        // Remove dead texts
        floatingTexts.removeAll { it.life <= 0f }
    }

      // nClear all floating texts

    fun clear() {
        floatingTexts.clear()
    }

     // Stop updating

    fun stop() {
        isUpdating = false
    }
}