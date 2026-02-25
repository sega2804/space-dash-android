package com.crypticsamsara.spacedash.data.game

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.crypticsamsara.spacedash.model.WeaponType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "game_prefs")

class PreferencesManager(private val context: Context) {

    companion object {
        val HIGH_SCORE_KEY = intPreferencesKey("high_score")
        val UNLOCKED_WEAPONS_KEY = stringSetPreferencesKey("unlocked_weapons")
    }



    val highScore: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[HIGH_SCORE_KEY] ?: 0
    }

    suspend fun saveHighScore(score: Int) {
        context.dataStore.edit { preferences ->
            val currentHighScore = preferences[HIGH_SCORE_KEY] ?: 0
            if (score > currentHighScore) {
                preferences[HIGH_SCORE_KEY] = score
            }
        }
    }

    suspend fun saveUnlockedWeapons(weapons: Set<WeaponType>) {
        context.dataStore.edit { preferences ->
            // Convert WeaponType enum to strings for storage
            val weaponStrings = weapons.map { it.name }.toSet()
            preferences[UNLOCKED_WEAPONS_KEY] = weaponStrings
        }
    }

    val unlockedWeaponsFlow: Flow<Set<WeaponType>> = context.dataStore.data.map { preferences ->
        val weaponStrings = preferences[UNLOCKED_WEAPONS_KEY] ?: setOf("BASIC_LASER")

        // Convert strings back to WeaponType enum
        weaponStrings.mapNotNull {
            try {
                WeaponType.valueOf(it)
            } catch (e: Exception) {
                null // Ignore invalid weapon types
            }
        }.toSet()
    }


    suspend fun getUnlockedWeapons(): Set<WeaponType> {
        var weapons = setOf<WeaponType>()
        context.dataStore.edit { preferences ->
            val weaponStrings = preferences[UNLOCKED_WEAPONS_KEY] ?: setOf("BASIC_LASER")
            weapons = weaponStrings.mapNotNull {
                try {
                    WeaponType.valueOf(it)
                } catch (e: Exception) {
                    null
                }
            }.toSet()
        }
        return weapons
    }

    suspend fun isWeaponUnlocked(weaponType: WeaponType): Boolean {
        val unlockedWeapons = getUnlockedWeapons()
        return unlockedWeapons.contains(weaponType)
    }

    suspend fun unlockWeapon(weaponType: WeaponType) {
        context.dataStore.edit { preferences ->
            val currentWeapons = preferences[UNLOCKED_WEAPONS_KEY] ?: setOf("BASIC_LASER")
            val updatedWeapons = currentWeapons + weaponType.name
            preferences[UNLOCKED_WEAPONS_KEY] = updatedWeapons
        }
    }

  // testing for reseting to only basic laser unlocked
    suspend fun resetUnlockedWeapons() {
        context.dataStore.edit { preferences ->
            preferences[UNLOCKED_WEAPONS_KEY] = setOf("BASIC_LASER")
        }
    }

    // testing
    suspend fun unlockAllWeapons() {
        val allWeapons = WeaponType.values().map { it.name }.toSet()
        context.dataStore.edit { preferences ->
            preferences[UNLOCKED_WEAPONS_KEY] = allWeapons
        }
    }
}