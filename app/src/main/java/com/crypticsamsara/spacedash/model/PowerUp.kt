package com.crypticsamsara.spacedash.model

import androidx.compose.ui.graphics.Color
import kotlin.random.Random

data class PowerUp(
    val id: Int,
    var x: Float,
    var y: Float,
    val velocity: Float = 3f,
    val size: Float = 40f,
    val type: PowerUpType,
    val color: Color,
    val isCollected: Boolean = false
)

enum class PowerUpType {
    AMMO_REFILL,
    AMMO_PACK,
    SHIELD,
    DOUBLE_DAMAGE,
    RAPID_FIRE,
    SCORE_MULTIPLIER
}

object PowerUpFactory {
    private var nextId = 0

    fun createRandomPowerUp (
        screenWidth: Float,
    ): PowerUp? {
        if (Random.nextFloat() > 0.3f) { // 30% chance to spawn
            return null
    }

        val id = nextId++
        val x = Random.nextFloat()
        val y = -50f

        // weighted random type
        val type = when (Random.nextInt(100)) {
            in 0..69 -> PowerUpType.AMMO_REFILL
            in 70..89 -> PowerUpType.AMMO_PACK
            in 90..94 -> PowerUpType.DOUBLE_DAMAGE
            in 95..99 -> PowerUpType.RAPID_FIRE
            else -> PowerUpType.AMMO_REFILL
        }

        // Color based on type
        val color = when (type) {
            PowerUpType.AMMO_REFILL -> Color(0xFF00E676)
            PowerUpType.AMMO_PACK -> Color(0xFF00C853)
            PowerUpType.SHIELD -> Color(0xFF2196F3)
            PowerUpType.DOUBLE_DAMAGE -> Color(0xFFFF6B35)
            PowerUpType.RAPID_FIRE -> Color(0xFFFFD700)
            PowerUpType.SCORE_MULTIPLIER -> Color(0xFFBF40BF)
        }

        return PowerUp(
            id = id,
            x = x,
            y = y,
            velocity = 2.5f,
            size = 45f,
            type = type,
            color = color
        )
    }

    //  specific power-up types
    fun createAmmoRefill(x: Float, y: Float): PowerUp {
        return PowerUp(
            id = nextId++,
            x = x,
            y = y,
            velocity = 2.5f,
            size = 45f,
            type = PowerUpType.AMMO_REFILL,
            color = Color(0xFF00E676)
        )
    }

    fun createAmmoPack(x: Float, y: Float): PowerUp {
        return PowerUp(
            id = nextId++,
            x = x,
            y = y,
            velocity = 2.5f,
            size = 50f,
            type = PowerUpType.AMMO_PACK,
            color = Color(0xFF00C853)
        )
    }
}
