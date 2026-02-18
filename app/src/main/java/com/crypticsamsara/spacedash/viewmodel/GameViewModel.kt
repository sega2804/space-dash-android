package com.crypticsamsara.spacedash.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crypticsamsara.spacedash.data.PreferencesManager
import com.crypticsamsara.spacedash.data.game.DifficultyManager
import com.crypticsamsara.spacedash.model.Bullet
import com.crypticsamsara.spacedash.model.BulletFactory
import com.crypticsamsara.spacedash.model.BulletType
import com.crypticsamsara.spacedash.model.FloatingTextFactory
import com.crypticsamsara.spacedash.model.Obstacle
import com.crypticsamsara.spacedash.model.ObstacleFactory
import com.crypticsamsara.spacedash.model.PowerUp
import com.crypticsamsara.spacedash.model.PowerUpFactory
import com.crypticsamsara.spacedash.model.PowerUpType
import com.crypticsamsara.spacedash.model.Star
import com.crypticsamsara.spacedash.model.StarFactory
import com.crypticsamsara.spacedash.model.Weapon
import com.crypticsamsara.spacedash.model.WeaponFactory
import com.crypticsamsara.spacedash.model.WeaponType
import com.crypticsamsara.spacedash.ui.audio.SoundManager
import com.crypticsamsara.spacedash.ui.components.PlayerRenderer
import com.crypticsamsara.spacedash.ui.effects.FloatingTextManager
import com.crypticsamsara.spacedash.ui.effects.ScreenShakeController
import com.crypticsamsara.spacedash.ui.effects.ShootingEffectsManager
import com.crypticsamsara.spacedash.ui.haptics.HapticManager
import com.crypticsamsara.spacedash.utils.CollisionDetector
import com.crypticsamsara.spacedash.utils.CollisionDetector.checkNearMiss
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch


data class GameState(
    val isPlaying: Boolean = false,
    val score: Int = 0,
    val playerX: Float = 0.5f,
    val isGameOver: Boolean = false,
    val survivalTime: Long = 0L,
    val obstaclesDodged: Int = 0,
    val highScore: Int = 0,
    val isPaused: Boolean = false,
    val currentCombo: Int = 0,
    val maxComboReached: Int = 0,
    val currentAmmo: Int = 50,
    val maxAmmo: Int = 50,
    val lastShotTime: Long = 0L,
    val obstaclesDestroyed: Int = 0,
    val currentWeapon: Weapon = WeaponFactory.getBasicLaser(),
    val unlockedWeapons: Set<WeaponType> = setOf(WeaponType.BASIC_LASER),
    val credits: Int = 0
)
class GameViewModel(
     val soundManager: SoundManager? = null,
     val hapticManager: HapticManager? = null
): ViewModel() {

    var gameState by mutableStateOf(GameState())
    private set

    // obstacles list
    val obstacles = mutableStateListOf<Obstacle>()

    // screen shake controller
    val screenShakeController = ScreenShakeController()

    // floating text manager
    val floatingTextManager = FloatingTextManager()

    // Bullet list
    val bullets = mutableStateListOf<Bullet>()

    // Power_Up list
    val powerUps = mutableStateListOf<PowerUp>()

    // shooting effects manager
    val shootingEffectsManager = ShootingEffectsManager()

    // tracking dodged obstacles
    private val dodgedObstacleIds = mutableSetOf<Int>()

    // track which obstacles we have checked for near miss
    private val nearMissedObstacleIds = mutableSetOf<Int>()

    // Stars List (static)
    var stars = mutableStateListOf<Star>()

    // Screen dimensions
    var screenWidth by mutableFloatStateOf(0f)
        private set

    var screenHeight by mutableFloatStateOf(0f)
        private set

    // difficulty level up
    var onDifficultyLevelUp: ((Int) -> Unit)? = null

    // Game loop job
    private var gameLoopJob: Job? = null
    private var spawnJob: Job? = null
    private var scoreJob: Job? = null

    // High Score
    private var sessionHighScore = 0

    // difficulty level up
    private var lastDifficultyLevel = 1

    var onExplosion: ((Float, Float) -> Unit)? = null

    var onComboMilestone: ((Int) -> Unit)? = null

    var isScreenShakeEnabled: Boolean = true

    // Scoring constraints
    private companion object {
        const val POINTS_PER_SECOND = 10
        const val POINTS_PER_DODGE = 50
        const val NEAR_MISS_BONUS = 25
        const val COMBO_MULTIPLIER = 10
        const val POINTS_PER_DESTROY = 15
    }

    // power up spawn timer
    private var lastPowerUpSpawnTime = 0L
    private val powerUpSpawnInterval = 15000L

    // active weapon's fire-rate cooldown in milliseconds
    private fun currentFireRateCooldown(): Long = gameState.currentWeapon.fireRate

    // returns how much ammo the active weapon consumes per shot
    private fun currentAmmoConsumption(): Int = gameState.currentWeapon.ammoConsumption


    fun setScreenSize (width: Float, height: Float) {
        screenWidth = width
        screenHeight = height

        // Generation of stars once when screen size is known
        if (stars.isEmpty() && width > 0 && height > 0) {
            stars.addAll(StarFactory.generateStarfield(width, height, 100))
        }
    }

    fun startGame() {
        gameState = GameState(
            isPlaying = true,
            score = 0,
            playerX = 0.5f,
            isGameOver = false,
            survivalTime = 0L,
            obstaclesDodged = 0,
            highScore = sessionHighScore,
            isPaused = false,
            currentCombo = 0,
            maxComboReached = 0,
            currentAmmo = 50,
            maxAmmo = 50,
            currentWeapon = WeaponFactory.getBasicLaser(),
            unlockedWeapons = gameState.unlockedWeapons,
            credits = gameState.credits,
            lastShotTime = 0L,
            obstaclesDestroyed = 0
        )

        obstacles.clear()
        bullets.clear()
        dodgedObstacleIds.clear()
        nearMissedObstacleIds.clear()
        floatingTextManager.clear()

        soundManager?.startMusic()

        // floating text updates
        floatingTextManager.startUpdating(viewModelScope)

        startGameLoop()
        startObstacleSpawner()
        startScoreTimer()
    }

    fun pauseGame() {
        if (!gameState.isPlaying || gameState.isGameOver || gameState.isPaused) return

        gameState = gameState.copy(isPaused = true)

        // To pause music
        soundManager?.pauseMusic()
    }

    fun resumeGame() {
        if (!gameState.isPlaying || gameState.isGameOver || !gameState.isPaused) return

        gameState = gameState.copy(isPaused = false)

        // Resume music
        soundManager?.resumeMusic()
    }

    fun restartFromPause() {
        // unpause first
        gameState = gameState.copy(isPaused = false)
        // then restart
        restartGame()
    }

    fun toggleScreenShake(enabled: Boolean) {
        isScreenShakeEnabled = enabled
        if (!enabled) {
            screenShakeController.stopShake()
        }
    }

    fun shootBullet() {
        if (!gameState.isPlaying || gameState.isGameOver || gameState.isPaused) return

        val currentTime = System.currentTimeMillis()

        // has enough time passed since last shot?
        if (currentTime - gameState.lastShotTime < currentFireRateCooldown()) {
            return
        }

        // do we have ammo?
        if (gameState.currentAmmo < currentAmmoConsumption()) {
            // for later: add out of ammo sound later
            return
        }

        // Get player position
        val playerX = getPlayerPixelX()
        val playerY = screenHeight - PlayerRenderer.PLAYER_HEIGHT - 100f

        val newBullets = BulletFactory.createBulletsForWeapon(
            gameState.currentWeapon,
            playerX,
            playerY
        )
        bullets.addAll(newBullets)


        gameState = gameState.copy(
            currentAmmo = gameState.currentAmmo - currentAmmoConsumption(),
            lastShotTime = currentTime
        )

        // spot for gun sound
        soundManager?.playShoot()
        // spot for haptic feedback
        hapticManager?.lightTap()
    }

    private fun startGameLoop() {
        gameLoopJob?.cancel()
        gameLoopJob = viewModelScope.launch {
            while (isActive && gameState.isPlaying) {
                if (!gameState.isPaused) {
                    updateGame()
                }
                delay(16L) // 60 FPs
            }
        }
    }

    private fun startObstacleSpawner() {
        spawnJob?.cancel()
        spawnJob = viewModelScope.launch {
            while (isActive && gameState.isPlaying) {
                // spawn if not paused
                if (!gameState.isPaused) {
                    spawnObstacles()

                    // dynamic spawn interval based on difficulty
                    val spawnInterval = DifficultyManager.getSpawnInterval(gameState.survivalTime)
                    delay((1000L..2500L).random()) // spawn every 1-2.5 seconds
                } else {
                    delay(100L) // pause for 0.1 second

                }
            }
        }
    }

    private fun startScoreTimer() {
        scoreJob?.cancel()
        val startTime = System.currentTimeMillis()
        var pauseStateTime: Long = 0
        var totalPausedTime: Long = 0


        scoreJob = viewModelScope.launch {
            while (isActive && gameState.isPlaying) {
                // handle pause state
                if (gameState.isPaused) {
                    if (pauseStateTime == 0L) {
                    pauseStateTime = System.currentTimeMillis()
                }
                delay(100L)
                continue
            } else {
                if (pauseStateTime != 0L) {
                    totalPausedTime += System.currentTimeMillis() - pauseStateTime
                    pauseStateTime = 0L
            }
        }

                val currentTime = System.currentTimeMillis()
                val survivalTime = currentTime - startTime - totalPausedTime

                // survival time
                gameState = gameState.copy(survivalTime = survivalTime)

                // time-based score (10 points per second)
                val timeScore = (survivalTime / 100) // 1 point per 100ms = 10
                val totalScore = timeScore.toInt() + (gameState.obstaclesDodged * POINTS_PER_DODGE)

                gameState = gameState.copy(score = totalScore)

                delay(100L)
            }
        }
    }

    private fun updateGame() {
        // movement from down
        obstacles.forEach { obstacle ->
            obstacle.y += obstacle.speed
        }

        updateBullets()

        // difficulty level change
        val currentDifficultyLevel = getCurrentDifficultyLevel()
        if (currentDifficultyLevel > lastDifficultyLevel) {
            lastDifficultyLevel = currentDifficultyLevel
            onDifficultyLevelUp?.invoke(currentDifficultyLevel)

            hapticManager?.lightTap()
        }

        // Check for dodged obstacles
        checkDodgedObstacles()

        // Check collisions
        checkCollisions()

        // check for bullet collisions
        checkBulletCollisions()

        // check for near miss
        checkNearMisses()


        // Remove obstacles that are off-screen
        obstacles.removeAll { obstacle ->
            val isOffScreen = obstacle.y > screenHeight + 100f
            if (isOffScreen) {
                dodgedObstacleIds.remove(obstacle.id)
                nearMissedObstacleIds.remove(obstacle.id)
            }
            isOffScreen
        }
    }

    private fun checkDodgedObstacles() {
        val playerY = screenHeight - PlayerRenderer.PLAYER_HEIGHT - 100f

        obstacles.forEach { obstacle ->
            // check if obstacle has passed the player and hasn't been counted yet
            if (obstacle.y > playerY + PlayerRenderer.PLAYER_HEIGHT &&
                !dodgedObstacleIds.contains(obstacle.id)) {

                dodgedObstacleIds.add(obstacle.id)

                // combo system
                val newCombo = gameState.currentCombo + 1
                val newMaxCombo = maxOf(newCombo, gameState.maxComboReached)

                // POINTS FOR THIS DODGE
                // base points calculation
                val basePoints = POINTS_PER_DODGE
                // combo bonus calculation
                val comboBonus = newCombo * COMBO_MULTIPLIER
                // dodge points calculation
                val dodgePoints = basePoints + comboBonus

                // OBSTACLE POSITION FOR FLOATING TEXT
                val obstaclePixelX = obstacle.x * screenWidth
                val obstaclePixelY = obstacle.y

                // FLOATING TEXT FOR POINTS
                val scoreText = FloatingTextFactory.createScoreText(
                    x = obstaclePixelX,
                    y = obstaclePixelY,
                    points = dodgePoints,
                    combo = newCombo
                )
                floatingTextManager.addFloatingText(scoreText)

                // Check for milestones
                if (newCombo in listOf(5, 10, 25, 50, 100)) {
                    onComboMilestone?.invoke(newCombo)
                    // haptic addition for milestone
                    hapticManager?.successVibration()
                    // only shake if enabled
                    if (isScreenShakeEnabled) {
                        // screen shake for milestone
                        screenShakeController.mediumShake(viewModelScope)
                    }

                    // MILESTONE FLOATING TEXT
                    val milestoneText = FloatingTextFactory.createMilestoneText(
                        x = screenWidth / 2,
                        y = screenHeight / 2,
                        milestone = newCombo
                    )
                    floatingTextManager.addFloatingText(milestoneText)
                } else {
                    hapticManager?.mediumVibration()


                //  COMBO FLOATING TEXT
                if (newCombo > 3 && newCombo % 3 == 0) {
                    val comboText = FloatingTextFactory.createComboText(
                        x = screenWidth / 2,
                        y = screenHeight / 3,
                        combo = newCombo
                    )
                    floatingTextManager.addFloatingText(comboText)
                }
            }

                // Dodge sound
                soundManager?.playDodge()

                // Increment dodged count
                val newDodgeCount = gameState.obstaclesDodged + 1
                gameState = gameState.copy(
                    obstaclesDodged = newDodgeCount,
                    currentCombo = newCombo,
                    maxComboReached = newMaxCombo
                )

                // Add dodge bonus to score
                val dodgeBonus = POINTS_PER_DODGE * newDodgeCount
                val timeScore = (gameState.survivalTime / 100).toInt()
                val totalComboBonus = (1..newCombo).sum() * COMBO_MULTIPLIER

                gameState = gameState.copy(
                    score = timeScore + dodgeBonus + totalComboBonus)
            }
        }
    }


    private fun checkNearMisses() {
        if (!gameState.isPlaying || gameState.isGameOver || !isScreenShakeEnabled) return

        val playerX = getPlayerPixelX()
        val playerY = screenHeight - PlayerRenderer.PLAYER_HEIGHT - 100f

        obstacles.forEach { obstacle ->
            if (!nearMissedObstacleIds.contains(obstacle.id)) {
                if (CollisionDetector.checkNearMiss(playerX, playerY, obstacle, screenWidth)) {
                    nearMissedObstacleIds.add(obstacle.id)
                    screenShakeController.lightShake(viewModelScope)
                }
            }
        }
    }

    private fun spawnObstacles() {
        if (screenWidth > 0) {
            // difficulty based parameters
            val baseSpeed = DifficultyManager.getObstacleSpeed(gameState.survivalTime)
            val baseSize = DifficultyManager.getObstacleSize(gameState.survivalTime)

            val obstacle = ObstacleFactory.createRandomObstacle(
                screenWidth,
                baseSpeed = baseSpeed,
                baseSize = baseSize
            )
            obstacles.add(obstacle)
        }
    }

    private fun spawnPowerUps() {
        val currentTime = System.currentTimeMillis()

        // Only spawn power-ups every 15 seconds
        if (currentTime - lastPowerUpSpawnTime < powerUpSpawnInterval)
            return


        lastPowerUpSpawnTime = currentTime

        // Try to spawn a power-up (30% chance)
        val powerUp = PowerUpFactory.createRandomPowerUp(screenWidth)
        if (powerUp != null) {
            powerUps.add(powerUp)
        }
    }

    private fun updatePowerUps() {
        // Move power-ups down
        powerUps.forEach { powerUp ->
            val index = powerUps.indexOf(powerUp)
            if (index != -1) {
                powerUps[index] = powerUp.copy(
                    y = powerUp.y + powerUp.velocity
                )
            }
        }

        powerUps.removeAll { it.y > screenHeight + 100f }
    }

    private fun checkPowerUpCollisions() {
        if (!gameState.isPlaying || gameState.isGameOver) return

        val playerX = getPlayerPixelX()
        val playerY = screenHeight - PlayerRenderer.PLAYER_HEIGHT - 100f

        val powerUpsToRemove = mutableSetOf<Int>()

        powerUps.forEach { powerUp ->
            if (powerUp.isCollected) return@forEach

            val powerUpPixelX = powerUp.x * screenWidth
            val powerUpPixelY = powerUp.y

            // Check collision with player
            val distance = kotlin.math.sqrt(
                (playerX - powerUpPixelX) * (playerX - powerUpPixelX) +
                        (playerY - powerUpPixelY) * (playerY - powerUpPixelY)
            )

            if (distance < (PlayerRenderer.PLAYER_WIDTH / 2 + powerUp.size / 2)) {
                // POWER-UP COLLECTED!
                powerUpsToRemove.add(powerUp.id)

                // Apply power-up effect
                when (powerUp.type) {
                    PowerUpType.AMMO_REFILL -> {
                        refillAmmo(25)
                        soundManager?.playPowerUp()
                        hapticManager?.successVibration()
                    }
                    PowerUpType.AMMO_PACK -> {
                        refillAmmo(50)
                        soundManager?.playPowerUp()
                        hapticManager?.successVibration()
                    }
                    PowerUpType.DOUBLE_DAMAGE -> {
                        // TODO: Implement in future
                    }
                    PowerUpType.RAPID_FIRE -> {
                        // TODO: Implement in future
                    }
                    else -> {}
                }

                // Floating text for collection
                val text = when (powerUp.type) {
                    PowerUpType.AMMO_REFILL -> "+25 AMMO"
                    PowerUpType.AMMO_PACK -> "+50 AMMO!"
                    PowerUpType.DOUBLE_DAMAGE -> "2X DAMAGE!"
                    PowerUpType.RAPID_FIRE -> "RAPID FIRE!"
                    else -> "POWER-UP!"
                }

                val collectionText = FloatingTextFactory.createComboText(
                    x = powerUpPixelX,
                    y = powerUpPixelY,
                    combo = 0
                )
                floatingTextManager.addFloatingText(collectionText)
            }
        }

        // Remove collected power-ups
        powerUps.removeAll { powerUpsToRemove.contains(it.id) }
    }

    private fun checkCollisions() {
        if (!gameState.isPlaying || gameState.isGameOver) return

        val playerX = getPlayerPixelX()
        val playerY = screenHeight - PlayerRenderer.PLAYER_HEIGHT - 100f

        // Check each obstacle for collision
        obstacles.forEach { obstacle ->
            if (CollisionDetector.checkCollision(playerX, playerY, obstacle, screenWidth)) {

                // Reset combo on collision
                gameState = gameState.copy(currentCombo = 0)

                // sound management
                // Explosion sound
                soundManager?.playExplosion()
                onExplosion?.invoke(playerX, playerY)
                // Vibration - strong
                hapticManager?.strongVibration()
                // only shake if enabled
                if (isScreenShakeEnabled) {
                    // screen shake - strong
                    screenShakeController.strongShake(viewModelScope)
                }

                val obstaclePixelX  = obstacle.x * screenWidth
                screenShakeController.directionalShake(
                    viewModelScope,
                    impactX = obstaclePixelX,
                    playerX = playerX
                )

                triggerGameOver()
                return
            }
        }
    }


    private fun triggerGameOver() {
        // Update high score if current is higher
        if (gameState.score > sessionHighScore) {
            sessionHighScore = gameState.score
        }

       gameState = gameState.copy(
            isPlaying = false,
            isGameOver = true,
            highScore = sessionHighScore
        )

        // Stop music
        soundManager?.pauseMusic()
        floatingTextManager.stop()

        gameState = gameState.copy(
            isPlaying = false,
            isGameOver = true,
            highScore = sessionHighScore
            )
        gameLoopJob?.cancel()
        spawnJob?.cancel()
        scoreJob?.cancel()
    }

    private fun updateBullets() {
        // upward movement for bullets
        bullets.forEach {bullet ->
            val index = bullets.indexOf(bullet)
            if (index != -1) {
                bullets[index] = bullet.copy(
                    y = bullet.y - bullet.velocity
                )
            }
        }

        bullets.removeAll { it.y < -50f }
    }

    private fun checkBulletCollisions() {
        if (!gameState.isPlaying || gameState.isGameOver) return

        // To track which bullets and obstacles to remove
        val bulletsToRemove = mutableSetOf<Int>()
        val obstaclesToRemove = mutableSetOf<Int>()
        val obstaclesToUpdate = mutableListOf<Pair<Int, Int>>()


        bullets.forEach { bullet ->
            obstacles.forEach{ obstacle ->

                if (bulletsToRemove.contains(bullet.id) ||
                    obstaclesToRemove.contains(obstacle.id)) {
                    return@forEach

            }

                // if obstacle is not destructible
                if (!obstacle.isDestructible) {
                    return@forEach
                }

            // Check collision between bullet and obstacle
            val obstaclePixelX = obstacle.x * screenWidth
            val obstaclePixelY = obstacle.y
            val obstacleRadius = obstacle.size / 2

            val bulletX = bullet.x
            val bulletY = bullet.y

            // Simple circle-circle collision
            val distance = kotlin.math.sqrt(
                (bulletX - obstaclePixelX) * (bulletX - obstaclePixelX) +
                        (bulletY - obstaclePixelY) * (bulletY - obstaclePixelY)
            )

            if (distance < (obstacleRadius + bullet.width)) {
                // COLLISION DETECTED
                bulletsToRemove.add(bullet.id)
                obstaclesToRemove.add(obstacle.id)

                // reduce obstacle HP
                val newHP = obstacle.currentHP - bullet.damage

                // hit effects
                shootingEffectsManager.createHitSparks(obstaclePixelX, obstaclePixelY, 12)
                shootingEffectsManager.createDamageNumber(obstaclePixelX, obstaclePixelY, bullet.damage)

                // Light vibration for destruction
                hapticManager?.lightTap()

                if (newHP <= 0) {
                    // OBSTACLE DESTROYED!
                    obstaclesToRemove.add(obstacle.id)

                    // Play explosion sound
                    soundManager?.playExplosion()

                    // Create explosion
                    onExplosion?.invoke(obstaclePixelX, obstaclePixelY)

                    // Award points and credits
                    val destroyPoints = obstacle.creditValue
                    gameState = gameState.copy(
                        score = gameState.score + destroyPoints,
                        obstaclesDestroyed = gameState.obstaclesDestroyed + 1
                    )

                    // FLOATING TEXT for destruction
                    val destructionText = FloatingTextFactory.createScoreText(
                        x = obstaclePixelX,
                        y = obstaclePixelY,
                        points = destroyPoints,
                        combo = 0
                    )
                    floatingTextManager.addFloatingText(destructionText)
                } else {
                    // Obstacle damaged but not destroyed
                    obstaclesToUpdate.add(Pair(obstacle.id, newHP))
                }
            }
            }
        }

        // Remove collided bullets and obstacles
    bullets.removeAll { bulletsToRemove.contains(it.id) }
        // Update obstacle HP
        obstaclesToUpdate.forEach { (obstacleId, newHP) ->
            val index = obstacles.indexOfFirst { it.id == obstacleId }
            if (index != -1) {
                obstacles[index] = obstacles[index].copy(currentHP = newHP)
            }
        }

        // Remove destroyed obstacles
        obstacles.removeAll { obstacle ->
            val shouldRemove = obstaclesToRemove.contains(obstacle.id)
            if (shouldRemove) {
                dodgedObstacleIds.remove(obstacle.id)
                nearMissedObstacleIds.remove(obstacle.id)
            }
            shouldRemove
        }
    }
    fun movePlayerLeft() {
        if (!gameState.isPlaying || gameState.isGameOver) return

        // Move left, but don't go below 0
        val newX = (gameState.playerX - 0.05f).coerceAtLeast(0f)
        gameState = gameState.copy(playerX = newX)
    }

    fun movePlayerRight() {
        if (!gameState.isPlaying || gameState.isGameOver) return

        // Move right, but don't go above 1
        val newX = (gameState.playerX + 0.05f).coerceAtMost(1f)
        gameState = gameState.copy(playerX = newX)
    }

    fun restartGame() {
        startGame()
    }

    // weapon firing
    fun fireBullets() {
        if (!gameState.isPlaying || gameState.isGameOver || gameState.isPaused) return

        val weapon = gameState.currentWeapon
        val playerX = getPlayerPixelX()
        val playerY = screenHeight - PlayerRenderer.PLAYER_HEIGHT - 100f

        val newBullets = BulletFactory.createBulletsForWeapon(weapon, playerX, playerY)
        bullets.addAll(newBullets)
    }

    // weapon switch
    fun switchWeapon(weapon: Weapon) {
        gameState = gameState.copy(currentWeapon = weapon)
    }

    // helper function for weapon types
    fun changeWeapon(weaponType: WeaponType) {
        if (!gameState.unlockedWeapons.contains(weaponType)) return

        val newWeapon = when (weaponType) {
            WeaponType.BASIC_LASER -> WeaponFactory.getBasicLaser()
            WeaponType.RAPID_FIRE -> WeaponFactory.getRapidFire()
            WeaponType.SPREAD_SHOT -> WeaponFactory.getSpreadShot()
            WeaponType.MISSILE -> WeaponFactory.getMissile()
            WeaponType.PLASMA_CANNON -> WeaponFactory.getPlasmaCannon()
        }
    }

    // helper function for combo
    fun getComboMultiplier(): Float {
        return when (gameState.currentCombo) {
            in 0..4 -> 1f
            in 5..9 -> 1.5f
            in 10..24 -> 2f
            in 25..49 -> 2.5f
            else -> 3f
        }
    }

    fun getComboMessage(): String {
        return when (gameState.currentCombo) {
            0 -> ""
            in 1..4 -> "${gameState.currentCombo}x"
            in 5..9 -> "${gameState.currentCombo}x COMBO!"
            in 10..24 -> "${gameState.currentCombo}x GREAT!"
            in 25..49 -> "${gameState.currentCombo}x AMAZING!"
            else -> "${gameState.currentCombo}x LEGENDARY!"
        }
    }

    fun stopGame() {
        if (gameState.score > sessionHighScore) {
            sessionHighScore = gameState.score
        }
        gameState = gameState.copy(
            isPlaying = false,
            isGameOver = true,
            highScore = sessionHighScore)
        gameLoopJob?.cancel()
        spawnJob?.cancel()
        scoreJob?.cancel()
    }

    // Get player position in pixels
    fun getPlayerPixelX(): Float {
        return gameState.playerX * screenWidth
    }

    // Helper to format survival time
    fun getFormattedSurvivalTime(): String {
        val seconds = gameState.survivalTime / 1000
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return String.format("%02d:%02d", minutes, remainingSeconds)
    }

    fun onButtonClick() {
        soundManager?.playClick()
        hapticManager?.lightTap()
    }

    fun getCurrentDifficultyLevel(): Int {
        return DifficultyManager.getDifficultyLevel(gameState.survivalTime)
    }

    fun getCurrentDifficultyDescription(): String {
        return DifficultyManager.getDifficultyDescription(gameState.survivalTime)
    }

    fun getCurrentDifficultyColor(): Color {
        return DifficultyManager.getDifficultyColor(gameState.survivalTime)
    }

    fun refillAmmo(amount: Int) {
        val newAmmo = (gameState.currentAmmo + amount).coerceAtMost(gameState.maxAmmo)
        gameState = gameState.copy(currentAmmo = newAmmo)

        soundManager?.playPowerUp()
    }

    override fun onCleared() {
        super.onCleared()
        soundManager?.release()
        floatingTextManager.stop()
        gameLoopJob?.cancel()
        spawnJob?.cancel()
        scoreJob?.cancel()
    }
}