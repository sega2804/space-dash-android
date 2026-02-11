package com.crypticsamsara.spacedash.model

import androidx.compose.ui.graphics.Color
import com.crypticsamsara.spacedash.ui.theme.DangerRed
import com.crypticsamsara.spacedash.ui.theme.NeonCyan
import com.crypticsamsara.spacedash.ui.theme.NeonPurple

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
    val isActive: Boolean = true
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
        playerY: Float,
    ): Bullet {
        val id = nextId++
        return Bullet(
            id = id,
            x = playerX,
            y = playerY - 40,
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
            y = playerY - 40,
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
        angleOffset: Float = 0f
    ): Bullet {
        val id = nextId++
        return Bullet(
            id = id,
            x = playerX + (angleOffset * 2),
            y = playerY - 40,
            velocity = 13f,
            damage = 1,
            bulletType = BulletType.SPREAD_SHOT,
            color = NeonPurple,
            width = 7f,
            height = 22f
        )
    }

    fun createMissile(
        playerX: Float,
        playerY: Float
    ): Bullet {
        val id = nextId++
        return Bullet(
            id = id,
            x = playerX,
            y = playerY - 40f,
            velocity = 10f,
            damage = 3,
            bulletType = BulletType.MISSILE,
            color = DangerRed,
            width = 12f,
            height = 33f
        )
    }

    fun createSpreadShot(
        playerX: Float,
        playerY: Float
    ): List<Bullet> {
        return listOf(
            createSpreadShotBullet(playerX, playerY, -15f),
            createSpreadShotBullet(playerX, playerY, 0f),
            createSpreadShotBullet(playerX, playerY, 15f)
        )
    }



}
