package com.crypticsamsara.spacedash.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.crypticsamsara.spacedash.ui.theme.NeonCyan
import com.crypticsamsara.spacedash.ui.theme.DangerRed
import com.crypticsamsara.spacedash.ui.theme.NeonPurple
import kotlin.random.Random

data class Bullet(
    val id: Int,
    val x: Float,
    val y: Float,
    val velocity: Float,
    val damage: Int,
    val bulletType: BulletType,
    val color: Color,
    val width: Float = 10f,
    val height: Float = 30f,
    val isActive: Boolean = true,
    val angle: Float = 0f,
    val velocityX: Float = 0f,
    val velocityY: Float = 0f,
    val targetObstacleId: Int? = null
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

    fun createBasicLaser(
        playerX: Float,
        playerY: Float
    ): Bullet {
        val id = nextId++
        return Bullet(
            id = id,
            x = playerX,
            y = playerY - 40f,
            velocity = 15f,
            damage = 1,
            bulletType = BulletType.BASIC_LASER,
            color = NeonCyan,
            width = 8f,
            height = 25f
        )
    }

    fun createRapidFire(
        playerX: Float,
        playerY: Float
    ): Bullet {
        val id = nextId++
        return Bullet(
            id = id,
            x = playerX,
            y = playerY - 40f,
            velocity = 18f,
            damage = 1,
            bulletType = BulletType.RAPID_FIRE,
            color = Color.Yellow,
            width = 6f,
            height = 20f
        )
    }

    fun createSpreadShotBullet(
        playerX: Float,
        playerY: Float,
        angleOffset: Float = 0f,
        weapon: Weapon
    ): Bullet {
        val id = nextId++

        // Convert angle to radians for calculation
        val angleRad = Math.toRadians(angleOffset.toDouble())

        // Calculate velocity components based on angle
        val velocityX = (weapon.bulletSpeed * kotlin.math.sin(angleRad)).toFloat()
        val velocityY = (weapon.bulletSpeed * kotlin.math.cos(angleRad)).toFloat()

        return Bullet(
            id = id,
            x = playerX,
            y = playerY - 40f,
            velocity = weapon.bulletSpeed,
            damage = weapon.damage,
            bulletType = BulletType.SPREAD_SHOT,
            color = weapon.bulletColor,
            width = 7f,
            height = 22f,
            angle = angleOffset,
            velocityX = velocityX,
            velocityY = velocityY
        )
    }

    fun createMissile(
        playerX: Float,
        playerY: Float,
        weapon: Weapon
    ): Bullet {
        val id = nextId++
        return Bullet(
            id = id,
            x = playerX,
            y = playerY - 40f,
            velocity = weapon.bulletSpeed,
            damage = weapon.damage,
            bulletType = BulletType.MISSILE,
            color = weapon.bulletColor,
            width = 12f,
            height = 35f,
            velocityX = 0f,
            velocityY = weapon.bulletSpeed
        )
    }

    fun createPlasmaBullet(
        playerX: Float,
        playerY: Float,
        weapon: Weapon
    ): Bullet {
        val id = nextId++
        return Bullet(
            id = id,
            x = playerX,
            y = playerY - 40f,
            velocity = weapon.bulletSpeed,
            damage = weapon.damage,
            bulletType = BulletType.PLASMA_CANNON,
            color = weapon.bulletColor,
            width = 15f,   // Wider beam
            height = 40f   // Longer beam
        )
    }

    // Helper to create spread shot (3 bullets with proper angles)
    fun createSpreadShot(
        playerX: Float,
        playerY: Float,
        weapon: Weapon
    ): List<Bullet> {
        return listOf(
            createSpreadShotBullet(playerX, playerY, -25f, weapon), // Left angle
            createSpreadShotBullet(playerX, playerY, 0f, weapon),   // Center straight
            createSpreadShotBullet(playerX, playerY, 25f, weapon)   // Right angle
        )
    }

    // Helper to create bullets for a specific weapon
    fun createBulletsForWeapon(
        weapon: Weapon,
        playerX: Float,
        playerY: Float
    ): List<Bullet> {
        return when (weapon.type) {
            WeaponType.BASIC_LASER -> {
                listOf(createBasicLaser(playerX, playerY))
            }
            WeaponType.RAPID_FIRE -> {
                listOf(createRapidFire(playerX, playerY))
            }
            WeaponType.SPREAD_SHOT -> {
                createSpreadShot(playerX, playerY, weapon)
            }
            WeaponType.MISSILE -> {
                listOf(createMissile(playerX, playerY, weapon))
            }
            WeaponType.PLASMA_CANNON -> {
                listOf(createPlasmaBullet(playerX, playerY, weapon))
            }
        }
    }
}