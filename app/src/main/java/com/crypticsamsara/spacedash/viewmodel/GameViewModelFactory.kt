package com.crypticsamsara.spacedash.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.crypticsamsara.spacedash.ui.audio.SoundManager

class GameViewModelFactory(
    private val soundManager: SoundManager? = null
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
            return GameViewModel(soundManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}