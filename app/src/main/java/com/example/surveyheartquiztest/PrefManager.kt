package com.example.surveyheartquiztest

import android.content.Context
import android.content.SharedPreferences

class PreferenceManager(context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("quiz_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val HIGH_SCORE_KEY = "high_score"
//        private const val SOUND_ENABLED_KEY = "sound_enabled"
        private const val USERNAME_KEY = "username"
    }

    fun saveHighScore(score: Int) {
        val currentHighScore = sharedPreferences.getInt(HIGH_SCORE_KEY, 0)
        if (score > currentHighScore) {
            with(sharedPreferences.edit()) {
                putInt(HIGH_SCORE_KEY, score)
                apply()
            }
        }
    }

    fun saveHighScore2(score: Int) {
        val editor = sharedPreferences.edit()
        editor.putInt("highScore", score)
        editor.apply()
    }
    fun getHighScore(): Int {
        return sharedPreferences.getInt("highScore", 0)
    }

    fun loadHighScore(): Int {
        return sharedPreferences.getInt(HIGH_SCORE_KEY, 0)
    }

//    fun saveUserPreferences(soundEnabled: Boolean, username: String) {
//        with(sharedPreferences.edit()) {
//            putBoolean(SOUND_ENABLED_KEY, soundEnabled)
//            putString(USERNAME_KEY, username)
//            apply()
//        }
//    }
//
//    fun loadUserPreferences(): Pair<Boolean, String> {
//        val soundEnabled = sharedPreferences.getBoolean(SOUND_ENABLED_KEY, true)
//        val username = sharedPreferences.getString(USERNAME_KEY, "Guest") ?: "Guest"
//        return Pair(soundEnabled, username)
//    }
}
