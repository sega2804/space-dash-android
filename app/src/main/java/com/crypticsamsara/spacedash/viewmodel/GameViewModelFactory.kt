package com.crypticsamsara.spacedash.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.crypticsamsara.spacedash.data.game.CreditsManager
import com.crypticsamsara.spacedash.data.game.PreferencesManager
import com.crypticsamsara.spacedash.ui.audio.SoundManager
import com.crypticsamsara.spacedash.ui.haptics.HapticManager

class GameViewModelFactory(
    private val soundManager: SoundManager,
    private val hapticManager: HapticManager,
    private val creditsManager: CreditsManager,
    private val preferencesManager: PreferencesManager
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
            return GameViewModel(
                soundManager,
                hapticManager,
                creditsManager,
                preferencesManager
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}