package com.crypticsamsara.spacedash.ui.effects

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class ScreenShakeController {

    var shakeOffset by mutableStateOf(Offset.Zero)
        private set

    private var isShaking = false

    // Light shake - for near misses
    fun lightShake(scope: CoroutineScope) {
        if (isShaking) return
        shake(scope, duration = 200, intensity = 5f)
    }

    // medium shake - for combo milestone
    fun mediumShake(scope: CoroutineScope) {
        if (isShaking) return
        shake(scope, duration = 300, intensity = 10f)
    }

    // strong shake - for collisions
    fun strongShake(scope: CoroutineScope) {
        if (isShaking) return
        shake(scope, duration = 500, intensity = 20f)
    }

    // Custom shake
    private fun shake (
        scope: CoroutineScope,
        duration: Long,
        intensity: Float
    ) {
        isShaking = true

        scope.launch {
            val startTime = System.currentTimeMillis()
            val endTime = startTime + duration

            while (isActive && System.currentTimeMillis() < endTime) {
                // Calculate decay (shake gets weaker over time)
                val elapsed = System.currentTimeMillis() - startTime
                val progress = elapsed.toFloat() / duration.toFloat()
                val decay = 1f - progress

                // Random direction
                val angle = Random.nextFloat() * 2f * Math.PI.toFloat()
                val currentIntensity = intensity * decay

                // Calculate offset
                val x = cos(angle) * currentIntensity
                val y = sin(angle) * currentIntensity

                shakeOffset = Offset(x, y)

                delay(16L) // ~60 FPS
            }

            // Reset
            shakeOffset = Offset.Zero
            isShaking = false
        }
    }

    // stop ongoing shake
    fun stopShake() {
        shakeOffset = Offset.Zero
        isShaking = false
    }
}