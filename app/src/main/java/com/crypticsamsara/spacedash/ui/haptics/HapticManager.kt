package com.crypticsamsara.spacedash.ui.haptics

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

class HapticManager(context: Context) {

    private val vibrator: Vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        // Android 12+ (API 31+)
        val vibratorManager =
            context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibratorManager.defaultVibrator
    } else {
        // for older android versions
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    // settings
    var isHapticEnabled: Boolean = true

    // Light tap
    fun lightTap() {
        if (!isHapticEnabled) return
        vibrate(20, 50)
    }

    // Medium vibration for dodging objects
    fun mediumVibration() {
        if (!isHapticEnabled) return
        vibrate(50, 100)
    }

    // Strong vibration  for collisions
    fun strongVibration() {
        if (!isHapticEnabled) return
        vibrate(150, 250)
    }

    // success vibration for collecting power-ups
    fun successVibration() {
        if (!isHapticEnabled) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Pattern: vibrate 50ms, pause 50ms, vibrate 50ms
            val pattern = longArrayOf(0, 50, 50, 50)
            val amplitudes = intArrayOf(0, 150, 0, 150)

            val effect = VibrationEffect.createWaveform(pattern, amplitudes, -1)
            vibrator.vibrate(effect)
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(longArrayOf(0, 50, 50, 50), -1)
        }
    }

    // Generic vibration helper
    private fun vibrate(durationMs: Long, amplitude: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val effect = VibrationEffect.createOneShot(durationMs, amplitude)
            vibrator.vibrate(effect)
        } else {
            // Older versions (amplitude not supported)
            @Suppress("DEPRECATION")
            vibrator.vibrate(durationMs)
        }
    }


    // Cancel any ongoing vibration
    fun cancel() {
        vibrator.cancel()
    }








}