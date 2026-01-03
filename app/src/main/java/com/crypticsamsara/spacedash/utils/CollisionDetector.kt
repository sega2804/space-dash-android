package com.crypticsamsara.spacedash.utils

import com.crypticsamsara.spacedash.model.Obstacle
import com.crypticsamsara.spacedash.ui.components.PlayerRenderer
import kotlin.math.sqrt

object CollisionDetector {

    // To check if player collides with an obstacle

    fun checkCollision(
        playerX: Float,
        playerY: Float,
        obstacle: Obstacle,
        screenWidth: Float
    ): Boolean {
        // Player hitbox
        val playerCenterX = playerX
        val playerCenterY = playerY + PlayerRenderer.PLAYER_HEIGHT / 2
        val playerRadius = PlayerRenderer.PLAYER_WIDTH / 3 // in_case the player is smaller

        // Obstacle hitbox
        val obstacleX = obstacle.x * screenWidth
        val obstacleY = obstacle.y
        val obstacleRadius = obstacle.size / 2

        //Calculate distance between centers
        val distance = calculateDistance(
            playerCenterX, playerCenterY,
            obstacleX, obstacleY
        )

        // Collision if distance is less than sum of radii
        return distance < (playerRadius + obstacleRadius)
    }

    // Checking for collision using rectangle
    fun checkRectCollision(
        playerX: Float,
        playerY: Float,
        obstacle: Obstacle,
        screenWidth: Float
    ): Boolean {
        val playerLeft = playerX - PlayerRenderer.PLAYER_WIDTH / 2
        val playerRight = playerX + PlayerRenderer.PLAYER_WIDTH / 2
        val playerTop = playerY
        val playerBottom = playerY + PlayerRenderer.PLAYER_HEIGHT

        val obstacleX = obstacle.x * screenWidth
        val obstacleLeft = obstacleX - obstacle.size / 2
        val obstacleRight = obstacleX + obstacle.size / 2
        val obstacleTop = obstacle.y - obstacle.size / 2
        val obstacleBottom = obstacle.y + obstacle.size / 2

        return playerLeft < obstacleRight &&
                playerRight > obstacleLeft &&
                playerTop < obstacleBottom &&
                playerBottom > obstacleTop
    }

    // Calculate Euclidean distance between two point
    private fun calculateDistance(x1: Float, y1: Float, x2: Float, y2: Float): Float{
        val dx = x2 -x1
        val dy = y2 - y1
        return sqrt(dx * dx + dy * dy)
    }
    // Check if player is close to dodging an obstacle
    fun checkNearMiss(
        playerX: Float,
        playerY: Float,
        obstacle: Obstacle,
        screenWidth: Float,
        threshold: Float = 100f
    ): Boolean {
        val playerCenterX = playerX
        val playerCenterY = playerY + PlayerRenderer.PLAYER_HEIGHT / 2

        val obstacleX = obstacle.x * screenWidth
        val obstacleY = obstacle.y

        val distance = calculateDistance(
            playerCenterX, playerCenterY,
            obstacleX, obstacleY
        )

        val collisionDistance = (PlayerRenderer.PLAYER_WIDTH / 3) + (obstacle.size / 2)

        // Near miss: close but not colliding
        return distance < (collisionDistance + threshold) && distance >= collisionDistance
    }
}