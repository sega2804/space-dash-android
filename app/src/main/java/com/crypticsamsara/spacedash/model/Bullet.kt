package com.crypticsamsara.spacedash.model

import androidx.compose.ui.graphics.Color
import com.crypticsamsara.spacedash.ui.theme.DangerRed
import com.crypticsamsara.spacedash.ui.theme.NeonCyan
import com.crypticsamsara.spacedash.ui.theme.NeonPurple
import kotlin.math.cos
import kotlin.math.sin

data class Bullet(
    val id: Int,
    var x: Float,
    var y: Float,
    var velocity: Float,
    val damage: Int,
    val bulletType: BulletType,
    val color: Color,
    val width: Float = 8f,
    val height: Float = 20f,
    var isActive: Boolean = true,

    val angle: Float = 0f,           // For spread shots (angle in degrees)
    val velocityX: Float = 0f,       // Horizontal velocity for angled shots
    val velocityY: Float = velocity,  // Vertical velocity
    var targetObstacleId: Int? = null // For homing missiles
)

enum class BulletType {
    BASIC_LASER,
    RAPID_FIRE,
    SPREAD_SHOT,
    MISSILE,
    PLASMA_CANNON
}

object BulletFactory {
    private var nextId = 0

    // bullets for different weapon type
    fun createBulletsForWeapon(
        weapon: Weapon,
        playerX: Float,
        playerY: Float
    ): List<Bullet> {
        return when (weapon.type) {
            WeaponType.BASIC_LASER -> listOf(createBasicLaser(playerX, playerY, weapon))
            WeaponType.RAPID_FIRE -> listOf(createRapidFire(playerX, playerY, weapon))
            WeaponType.SPREAD_SHOT -> createSpreadShot(playerX, playerY, weapon)
            WeaponType.MISSILE -> listOf(createMissile(playerX, playerY, weapon))
            WeaponType.PLASMA_CANNON -> listOf(createPlasmaCannon(playerX, playerY, weapon))
        }
    }

    fun createBasicLaser(x: Float, y: Float, weapon: Weapon): Bullet {
        return Bullet(
            id = nextId++,
            x = x,
            y = y,
            velocity = weapon.bulletSpeed,
            damage = weapon.damage,
            bulletType = BulletType.BASIC_LASER,
            color = weapon.bulletColor,
            width = 8f,
            height = 20f
        )
    }

    fun createRapidFire(x: Float, y: Float, weapon: Weapon): Bullet {
        return Bullet(
            id = nextId++,
            x = x,
            y = y,
            velocity = weapon.bulletSpeed,
            damage = weapon.damage,
            bulletType = BulletType.RAPID_FIRE,
            color = weapon.bulletColor,
            width = 5f,
            height = 15f
        )
    }


    fun createSpreadShot(x: Float, y: Float, weapon: Weapon): List<Bullet> {
        val bullets = mutableListOf<Bullet>()

        // Angles: -20°, 0°, +20° (in radians for calculation)
        val angles = listOf(-20f, 0f, 20f)

        angles.forEach { angleDegrees ->
            val angleRadians = Math.toRadians(angleDegrees.toDouble()).toFloat()

            // Calculate velocity components
            val velocityX = sin(angleRadians) * weapon.bulletSpeed
            val velocityY = -cos(angleRadians) * weapon.bulletSpeed // Negative for upward

            bullets.add(
                Bullet(
                    id = nextId++,
                    x = x,
                    y = y,
                    velocity = weapon.bulletSpeed,
                    damage = weapon.damage,
                    bulletType = BulletType.SPREAD_SHOT,
                    color = weapon.bulletColor,
                    width = 10f,
                    height = 15f,
                    angle = angleDegrees,
                    velocityX = velocityX,
                    velocityY = velocityY
                )
            )
        }

        return bullets
    }


    fun createSpreadShotBullet(
        x: Float,
        y: Float,
        angle: Float,
        weapon: Weapon
    ): Bullet {
        val angleRadians = Math.toRadians(angle.toDouble()).toFloat()

        val velocityX = sin(angleRadians) * weapon.bulletSpeed
        val velocityY = -cos(angleRadians) * weapon.bulletSpeed

        return Bullet(
            id = nextId++,
            x = x,
            y = y,
            velocity = weapon.bulletSpeed,
            damage = weapon.damage,
            bulletType = BulletType.SPREAD_SHOT,
            color = weapon.bulletColor,
            width = 10f,
            height = 15f,
            angle = angle,
            velocityX = velocityX,
            velocityY = velocityY
        )
    }


    fun createMissile(x: Float, y: Float, weapon: Weapon): Bullet {
        return Bullet(
            id = nextId++,
            x = x,
            y = y,
            velocity = weapon.bulletSpeed,
            damage = weapon.damage,
            bulletType = BulletType.MISSILE,
            color = weapon.bulletColor,
            width = 12f,
            height = 25f
        )
    }


    fun createPlasmaCannon(x: Float, y: Float, weapon: Weapon): Bullet {
        return Bullet(
            id = nextId++,
            x = x,
            y = y,
            velocity = weapon.bulletSpeed,
            damage = weapon.damage,
            bulletType = BulletType.PLASMA_CANNON,
            color = weapon.bulletColor,
            width = 15f,
            height = 30f
        )
    }
}
