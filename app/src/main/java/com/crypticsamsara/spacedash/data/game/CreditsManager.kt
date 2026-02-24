package com.crypticsamsara.spacedash.data.game

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit

class CreditsManager(private val context: Context) {

    companion object {
        private val Context.creditsDataStore: DataStore<Preferences> by preferencesDataStore(
            name = "credits_preferences"
        )

        private val CREDITS_KEY = intPreferencesKey("total_credits")
        private val TOTAL_EARNED_KEY = intPreferencesKey("total_credits_earned")
        private val TOTAL_SPENT_KEY = intPreferencesKey("total_credits_spent")

        // Credit earning rates
        const val CREDITS_PER_OBSTACLE = 10
        const val SCORE_TO_CREDITS_RATIO = 100

        // Difficulty multipliers
        const val EASY_MULTIPLIER = 1.0f      // 0-30 seconds
        const val NORMAL_MULTIPLIER = 1.5f    // 30-60 seconds
        const val HARD_MULTIPLIER = 2.0f      // 60-120 seconds
        const val EXPERT_MULTIPLIER = 2.5f    // 120+ seconds
    }


     //  current credits as Flow

    val creditsFlow: Flow<Int> = context.creditsDataStore.data.map { preferences ->
        preferences[CREDITS_KEY] ?: 0
    }

     // total credits earned (all time) as Flow
    val totalEarnedFlow: Flow<Int> = context.creditsDataStore.data.map { preferences ->
        preferences[TOTAL_EARNED_KEY] ?: 0
    }

      // Get total credits spent (all time) as Flow
    val totalSpentFlow: Flow<Int> = context.creditsDataStore.data.map { preferences ->
        preferences[TOTAL_SPENT_KEY] ?: 0
    }

     // Add credits
    suspend fun addCredits(amount: Int) {
        context.creditsDataStore.edit { preferences ->
            val currentCredits = preferences[CREDITS_KEY] ?: 0
            val currentTotalEarned = preferences[TOTAL_EARNED_KEY] ?: 0

            preferences[CREDITS_KEY] = currentCredits + amount
            preferences[TOTAL_EARNED_KEY] = currentTotalEarned + amount
        }
    }

     // Spend credits (returns true if successful, false if insufficient credits)
    suspend fun spendCredits(amount: Int): Boolean {
        var success = false
        context.creditsDataStore.edit { preferences ->
            val currentCredits = preferences[CREDITS_KEY] ?: 0

            if (currentCredits >= amount) {
                val currentTotalSpent = preferences[TOTAL_SPENT_KEY] ?: 0
                preferences[CREDITS_KEY] = currentCredits - amount
                preferences[TOTAL_SPENT_KEY] = currentTotalSpent + amount
                success = true
            }
        }
        return success
    }


     // current credits (synchronous)
    suspend fun getCurrentCredits(): Int {
        var credits = 0
        context.creditsDataStore.edit { preferences ->
            credits = preferences[CREDITS_KEY] ?: 0
        }
        return credits
    }

     // Set credits to specific amount (for testing/debugging)
    suspend fun setCredits(amount: Int) {
        context.creditsDataStore.edit { preferences ->
            preferences[CREDITS_KEY] = amount
        }
    }

     // Reset all credits data
    suspend fun resetCredits() {
        context.creditsDataStore.edit { preferences ->
            preferences[CREDITS_KEY] = 0
            preferences[TOTAL_EARNED_KEY] = 0
            preferences[TOTAL_SPENT_KEY] = 0
        }
    }

     // credits earned from a game session
    fun calculateCreditsEarned(
        obstaclesDestroyed: Int,
        finalScore: Int,
        survivalTime: Long
    ): Int {
        // base credits
        val destroyBonus = obstaclesDestroyed * CREDITS_PER_OBSTACLE
        val scoreBonus = finalScore / SCORE_TO_CREDITS_RATIO

        // Get difficulty multiplier based on survival time
        val difficultyMultiplier = getDifficultyMultiplier(survivalTime)

        // Calculate total credits
        val totalCredits = ((destroyBonus + scoreBonus) * difficultyMultiplier).toInt()

        return totalCredits.coerceAtLeast(0) // Ensure non-negative
    }


     // Get difficulty multiplier based on survival time
    private fun getDifficultyMultiplier(survivalTimeMs: Long): Float {
        val survivalSeconds = survivalTimeMs / 1000

        return when {
            survivalSeconds < 30 -> EASY_MULTIPLIER       // 0-30s = 1.0x
            survivalSeconds < 60 -> NORMAL_MULTIPLIER     // 30-60s = 1.5x
            survivalSeconds < 120 -> HARD_MULTIPLIER      // 60-120s = 2.0x
            else -> EXPERT_MULTIPLIER                     // 120+s = 2.5x
        }
    }

     // Get difficulty multiplier description
    fun getDifficultyMultiplierDescription(survivalTimeMs: Long): String {
         return when (val multiplier = getDifficultyMultiplier(survivalTimeMs)) {
            EASY_MULTIPLIER -> "Easy (${multiplier}x)"
            NORMAL_MULTIPLIER -> "Normal (${multiplier}x)"
            HARD_MULTIPLIER -> "Hard (${multiplier}x)"
            EXPERT_MULTIPLIER -> "Expert (${multiplier}x)"
            else -> "${multiplier}x"
        }
    }

     // Calculate credits breakdown for display
    data class CreditsBreakdown(
        val destroyBonus: Int,
        val scoreBonus: Int,
        val baseTotal: Int,
        val difficultyMultiplier: Float,
        val finalTotal: Int
    )

     // Get detailed breakdown of credits earned
    fun getCreditsBreakdown(
        obstaclesDestroyed: Int,
        finalScore: Int,
        survivalTime: Long
    ): CreditsBreakdown {
        val destroyBonus = obstaclesDestroyed * CREDITS_PER_OBSTACLE
        val scoreBonus = finalScore / SCORE_TO_CREDITS_RATIO
        val baseTotal = destroyBonus + scoreBonus
        val difficultyMultiplier = getDifficultyMultiplier(survivalTime)
        val finalTotal = (baseTotal * difficultyMultiplier).toInt()

        return CreditsBreakdown(
            destroyBonus = destroyBonus,
            scoreBonus = scoreBonus,
            baseTotal = baseTotal,
            difficultyMultiplier = difficultyMultiplier,
            finalTotal = finalTotal
        )
    }
}

 // Credits Stats - For display in stats screen
data class CreditsStats(
    val currentCredits: Int,
    val totalEarned: Int,
    val totalSpent: Int,
    val netCredits: Int = totalEarned - totalSpent
)