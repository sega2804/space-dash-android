package com.crypticsamsara.spacedash.ui.effects

import androidx.compose.ui.graphics.nativeCanvas

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.crypticsamsara.spacedash.ui.theme.NeonCyan
import com.crypticsamsara.spacedash.ui.theme.DangerRed
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

// Muzzle Flash Effect
data class MuzzleFlash(
    val x: Float,
    val y: Float,
    var life: Float = 1f,  // 1.0 = just spawned, 0.0 = expired
    val maxLife: Float = 0.15f  // Very short duration (150ms)
)

// Hit Spark Particle
data class HitSpark(
    var x: Float,
    var y: Float,
    val velocityX: Float,
    val velocityY: Float,
    val color: Color,
    var life: Float = 1f,
    val size: Float = 3f
)

// Damage Number
data class DamageNumber(
    val damage: Int,
    var x: Float,
    var y: Float,
    val color: Color,
    var life: Float = 1f,
    var alpha: Float = 1f
)

class ShootingEffectsManager {

    // Muzzle flashes
    private val muzzleFlashes = mutableStateListOf<MuzzleFlash>()

    // Hit sparks
    private val hitSparks = mutableStateListOf<HitSpark>()

    // Damage numbers
    private val damageNumbers = mutableStateListOf<DamageNumber>()

    //  muzzle flash when shooting
    fun createMuzzleFlash(x: Float, y: Float) {
        muzzleFlashes.add(
            MuzzleFlash(x = x, y = y)
        )
    }

    //  hit sparks when bullet hits obstacle
    fun createHitSparks(x: Float, y: Float, count: Int = 12) {
        repeat(count) {
            val angle = Random.nextFloat() * 2f * Math.PI.toFloat()
            val speed = Random.nextFloat() * 6f + 3f

            hitSparks.add(
                HitSpark(
                    x = x,
                    y = y,
                    velocityX = cos(angle) * speed,
                    velocityY = sin(angle) * speed,
                    color = if (Random.nextBoolean()) Color.Yellow else Color(0xFFFF6B35),
                    size = Random.nextFloat() * 2f + 2f
                )
            )
        }
    }

    //  damage number when hitting obstacle
    fun createDamageNumber(x: Float, y: Float, damage: Int, isCritical: Boolean = false) {
        damageNumbers.add(
            DamageNumber(
                damage = damage,
                x = x + Random.nextFloat() * 20f - 10f, // Slight random offset
                y = y,
                color = if (isCritical) DangerRed else Color.White
            )
        )
    }

    // Update all effects
    fun update() {
        // Update muzzle flashes
        muzzleFlashes.forEach { flash ->
            flash.life -= 0.067f // Decay quickly (60 FPS = ~150ms total)
        }
        muzzleFlashes.removeAll { it.life <= 0f }

        // Update hit sparks
        hitSparks.forEach { spark ->
            spark.x += spark.velocityX
            spark.y += spark.velocityY
            spark.life -= 0.02f // ~50 frames = ~800ms
        }
        hitSparks.removeAll { it.life <= 0f }

        // Update damage numbers
        damageNumbers.forEach { number ->
            number.y -= 1.5f // Float upward
            number.life -= 0.015f // ~66 frames = ~1100ms
            number.alpha = number.life.coerceAtLeast(0f)
        }
        damageNumbers.removeAll { it.life <= 0f }
    }

    //  muzzle flashes
    fun DrawScope.drawMuzzleFlashes() {
        muzzleFlashes.forEach { flash ->
            val alpha = flash.life / flash.maxLife
            val size = 20f * (1f - flash.life) // Grows as it fades

            // Outer glow
            drawCircle(
                color = NeonCyan.copy(alpha = alpha * 0.3f),
                radius = size + 10f,
                center = Offset(flash.x, flash.y)
            )

            // Middle
            drawCircle(
                color = NeonCyan.copy(alpha = alpha * 0.6f),
                radius = size + 5f,
                center = Offset(flash.x, flash.y)
            )

            // Core
            drawCircle(
                color = Color.White.copy(alpha = alpha),
                radius = size,
                center = Offset(flash.x, flash.y)
            )
        }
    }

    //  hit sparks
    fun DrawScope.drawHitSparks() {
        hitSparks.forEach { spark ->
            val alpha = spark.life.coerceAtLeast(0f)

            // small circles with trails
            drawCircle(
                color = spark.color.copy(alpha = alpha),
                radius = spark.size,
                center = Offset(spark.x, spark.y)
            )

            // Trail effect
            drawCircle(
                color = spark.color.copy(alpha = alpha * 0.3f),
                radius = spark.size * 1.5f,
                center = Offset(spark.x - spark.velocityX * 0.5f, spark.y - spark.velocityY * 0.5f)
            )
        }
    }

    //  damage numbers
    fun DrawScope.drawDamageNumbers() {
        damageNumbers.forEach { number ->
            drawContext.canvas.nativeCanvas.apply {
                val paint = android.graphics.Paint().apply {
                    color = number.color.copy(alpha = number.alpha).value.toLong().toInt()
                    textSize = 24f * density
                    textAlign = android.graphics.Paint.Align.CENTER
                    isAntiAlias = true
                    typeface = android.graphics.Typeface.DEFAULT_BOLD

                    // Shadow for visibility
                    setShadowLayer(
                        4f * density,
                        0f,
                        2f * density,
                        android.graphics.Color.BLACK
                    )
                }

                drawText(
                    "-${number.damage}",
                    number.x,
                    number.y,
                    paint
                )
            }
        }
    }

    // all effects
    fun DrawScope.drawAllEffects() {
        drawHitSparks()
        drawMuzzleFlashes()
        drawDamageNumbers()
    }

    // Clear all effects
    fun clear() {
        muzzleFlashes.clear()
        hitSparks.clear()
        damageNumbers.clear()
    }
}