package com.crypticsamsara.spacedash.ui.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Build
import com.crypticsamsara.spacedash.R

class SoundManager(private val context: Context) {

    // Media player for background music
    private var musicPlayer: MediaPlayer? = null

    // SoundPool for sound effects (short audio)
    private var soundPool: SoundPool? = null

    // Sound effect IDs
    private var explosionSoundId: Int = 0
    private var dodgeSoundId: Int = 0
    private var clickSoundId: Int = 0
    private var powerUpSoundId: Int = 0
    private var shootSoundId: Int = 0


    // Volume settings (0.0 to 2.0)
    var musicVolume: Float = 0.5f
        set(value) {
            field = value.coerceIn(0f, 1f)
            musicPlayer?.setVolume(field, field)
        }

    var soundEffectsVolume: Float = 0.7f
        set(value) {
            field = value.coerceIn(0f,1f)
        }

    // Enable/disable flags
    var isMusicEnabled: Boolean = true
    var areSoundEffectsEnabled: Boolean = true


    init {
        setupSoundPool()
        setupMusic()
    }

    private fun setupSoundPool() {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(5) // Max 5 sounds playing simultaneously
            .setAudioAttributes(audioAttributes)
            .build()

        // Load sound effects
        try {
            explosionSoundId = soundPool?.load(context, R.raw.explosion, 1) ?: 0
            dodgeSoundId = soundPool?.load(context, R.raw.dodge, 1) ?: 0
            clickSoundId = soundPool?.load(context, R.raw.button_click, 1) ?: 0
            powerUpSoundId = soundPool?.load(context, R.raw.power_up, 1) ?: 0
            shootSoundId = soundPool?.load(context, R.raw.shoot, 1) ?: 0
        } catch (e: Exception) {
            // Sounds not found - that's okay, we'll add them later
            println("Sound files not found: ${e.message}")
        }
    }

    private fun setupMusic() {
        try {
            musicPlayer = MediaPlayer.create(context, R.raw.background_music)?.apply {
                isLooping = true
                setVolume(musicVolume, musicVolume)
            }
    } catch (e: Exception) {
        println("Background music not found: ${e.message}")
        }
    }

    // Play sound effects
    fun playExplosion() {
        if (areSoundEffectsEnabled && explosionSoundId != 0) {
            soundPool?.play(explosionSoundId, soundEffectsVolume, soundEffectsVolume, 1, 0, 1f)
        }
    }

    fun playShoot() {
        if (areSoundEffectsEnabled && shootSoundId != 0) {
            soundPool?.play(shootSoundId, soundEffectsVolume, soundEffectsVolume, 1, 0, 1f)
        }
    }
    fun playDodge() {
        if (areSoundEffectsEnabled && dodgeSoundId != 0) {
            soundPool?.play(dodgeSoundId, soundEffectsVolume, soundEffectsVolume, 1, 0, 1f)
        }
    }

    fun playClick() {
        if (areSoundEffectsEnabled && clickSoundId != 0) {
            soundPool?.play(clickSoundId, soundEffectsVolume, soundEffectsVolume, 1, 0, 1f)
        }
    }

    fun playPowerUp() {
        if (areSoundEffectsEnabled && powerUpSoundId != 0) {
            soundPool?.play(powerUpSoundId, soundEffectsVolume, soundEffectsVolume, 1, 0, 1f)
        }
    }

    // Music controls
    fun startMusic() {
        if (isMusicEnabled) {
            musicPlayer?.start()
        }
    }

    fun pauseMusic() {
        musicPlayer?.pause()
    }

    fun stopMusic() {
        musicPlayer?.stop()
        musicPlayer?.prepare()
    }

    fun resumeMusic() {
        if (isMusicEnabled && musicPlayer?.isPlaying == false) {
            musicPlayer?.start()
        }
    }

    // Lifecycle management
    fun pause() {
        musicPlayer?.pause()
    }

    fun resume() {
        if (isMusicEnabled) {
            musicPlayer?.start()
        }
    }

    fun release() {
        musicPlayer?.release()
        musicPlayer = null
        soundPool?.release()
        soundPool = null
    }
}
