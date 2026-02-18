package com.crypticsamsara.spacedash.model

import androidx.compose.ui.graphics.Color

data class Weapon(
    val type: WeaponType,
    val name: String,
    val description: String,
    val damage: Int,
    val fireRate: Long,
    val ammoConsumption: Int,
    val bulletSpeed:Float,
    val isUnlocked: Boolean = false,
    val cost: Int = 0,
    val bulletColor: Color,
    val icon: String
)

enum class WeaponType {
    BASIC_LASER,
    RAPID_FIRE,
    SPREAD_SHOT,
    MISSILE,
    PLASMA_CANNON,

}

object WeaponFactory {

    fun getBasicLaser(): Weapon {
        return Weapon(
        type = WeaponType.BASIC_LASER,
        name = "Basic Laser",
        description = "A basic laser weapon that deals moderate damage.",
        damage = 1,
        fireRate = 300L,
        ammoConsumption = 1,
            bulletSpeed = 15f,
            isUnlocked = true,
        cost = 0,
        bulletColor = Color(0xFF00F0FF),
        icon = "🔫"
        )
    }

    fun getRapidFire(): Weapon {
        return Weapon(
            type = WeaponType.RAPID_FIRE,
            name = "Rapid Fire Laser",
            description = "Fast firing, low damage. Spray and pray!",
            damage = 1,
            fireRate = 150L,
            ammoConsumption = 1,
            bulletSpeed = 19f,
            isUnlocked = false,
            cost = 500,
            bulletColor = Color(0xFFFFD700),
            icon = "⚡"
        )
    }

    fun getSpreadShot(): Weapon {
        return Weapon(
            type = WeaponType.SPREAD_SHOT,
            name = "Spread Shot",
            description = "Fires 3 bullets in a spread pattern.",
            damage = 1,
            fireRate = 400L,
            ammoConsumption = 3,
            bulletSpeed = 14f,
            isUnlocked = false,
            cost = 1000,
            bulletColor = Color(0xFFBF40BF),
            icon = "🔱"
        )
    }

    fun getMissile(): Weapon {
        return Weapon(
            type = WeaponType.MISSILE,
            name = "Missile Launcher",
            description = "Slow but powerful. Missiles home in on targets.",
            damage = 3,
            fireRate = 800L,
            ammoConsumption = 2,
            bulletSpeed = 10f,
            isUnlocked = false,
            cost = 1500,
            bulletColor = Color(0xFFFF6B35),
            icon = "\uD83D\uDE80"
        )
    }

    fun getPlasmaCannon(): Weapon {
        return Weapon(
            type = WeaponType.PLASMA_CANNON,
            name = "Plasma Cannon",
            description = "Continuous beam of death. High ammo drain.",
            damage = 2,
            fireRate = 100L,
            ammoConsumption = 2,
            bulletSpeed = 20f,
            isUnlocked = false,
            cost = 2500,
            bulletColor = Color(0xFF00FF00),
            icon = "⚛\uFE0F"
        )
    }

    // to get all weapons
    fun getAllWeapons(): List<Weapon> {
        return listOf(
            getBasicLaser(),
            getRapidFire(),
            getSpreadShot(),
            getMissile(),
            getPlasmaCannon()
        )
    }

    // get weapon by type
    fun getWeaponByType(type: WeaponType): Weapon? {
        return when (type) {
            WeaponType.BASIC_LASER -> getBasicLaser()
            WeaponType.RAPID_FIRE -> getRapidFire()
            WeaponType.SPREAD_SHOT -> getSpreadShot()
            WeaponType.MISSILE -> getMissile()
            WeaponType.PLASMA_CANNON -> getPlasmaCannon()
        }
    }

    // Unlock a weapon
    fun unlockWeapon(weapon: Weapon): Weapon {
        return weapon.copy(isUnlocked = true)
    }
}

// Weapon upgrade system
data class WeaponUpgrade(
    val weaponType: WeaponType,
    val upgradeType: UpgradeType,
    val level: Int,
    val maxLevel: Int = 5,
    val cost: Int,
    val benefit: String
)

enum class UpgradeType {
    DAMAGE,
    FIRE_RATE,
    AMMO_EFFICIENCY
}

object WeaponUpgradeFactory {

    fun getDamageUpgrade(weaponType: WeaponType, currentLevel: Int): WeaponUpgrade {
        val cost = when (currentLevel) {
            0 -> 300
            1 -> 500
            2 -> 800
            3 -> 1200
            4 -> 2000
            else -> 0
    }

    return WeaponUpgrade(
        weaponType = weaponType,
        upgradeType = UpgradeType.DAMAGE,
        level = currentLevel,
        cost = cost,
        benefit = "+${(currentLevel + 1) * 10}% Damage"
        )
    }

    fun getFireRateUpgrade(weaponType: WeaponType, currentLevel: Int): WeaponUpgrade {
        val cost = when (currentLevel) {
            0 -> 400
            1 -> 600
            2 -> 900
            3 -> 1400
            4 -> 2200
            else -> 0
        }

        return WeaponUpgrade(
            weaponType = weaponType,
            upgradeType = UpgradeType.FIRE_RATE,
            level = currentLevel,
            cost = cost,
            benefit = "-${(currentLevel + 1) * 10}% Cooldown"
        )
    }

    fun getAmmoEfficiencyUpgrade(weaponType: WeaponType, currentLevel: Int): WeaponUpgrade {
        val cost = when (currentLevel) {
            0 -> 350
            1 -> 550
            2 -> 850
            3 -> 1300
            4 -> 2100
            else -> 0
        }

        return WeaponUpgrade(
            weaponType = weaponType,
            upgradeType = UpgradeType.AMMO_EFFICIENCY,
            level = currentLevel,
            cost = cost,
            benefit = "-${(currentLevel + 1) * 5}% Ammo Use"
        )
    }
}