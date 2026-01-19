package com.crypticsamsara.spacedash.ui.components

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.crypticsamsara.spacedash.ui.theme.DangerRed
import com.crypticsamsara.spacedash.ui.theme.NeonPurple
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

data class Particle(
    var x: Float,
    var y: Float,
    val velocityX: Float,
    val velocityY: Float,
    val color: androidx.compose.ui.graphics.Color,
    val size: Float,
    var alpha: Float = 1f,
    var life: Float = 1f
    )

class ParticleSystem {
    private val particles = mutableStateListOf<Particle>()

    fun createExplosion(
        x: Float,
        y: Float,
        particleCount: Int = 30
    ) {
        repeat(particleCount) {
            val angle = Random.nextFloat() * 2 * Math.PI.toFloat()
            val speed = Random.nextFloat() * 8f + 4f
            val size = Random.nextFloat() * 4f + 2f

            particles.add(
                Particle(
                    x = x,
                    y = y,
                    velocityX = cos(angle) * speed,
                    velocityY = sin(angle) * speed,
                    color = if (Random.nextBoolean()) DangerRed else NeonPurple,
                    size = size,
                    alpha = 1f,
                    life = 1f
                )
            )
        }
    }

    fun update() {
        particles.forEach { particle ->
            particle.x += particle.velocityX
            particle.y += particle.velocityY
            particle.life -= 0.02f
            particle.alpha = particle.life.coerceAtMost(0f)
        }

        particles.removeAll { it.life <= 0f }
    }

    fun DrawScope.drawParticles() {
        particles.forEach { particle ->
            drawCircle(
                color = particle.color.copy(alpha = particle.alpha),
                radius = particle.size,
                center = Offset(particle.x, particle.y)
            )
        }
    }

    fun clear() {
        particles.clear()
    }

}