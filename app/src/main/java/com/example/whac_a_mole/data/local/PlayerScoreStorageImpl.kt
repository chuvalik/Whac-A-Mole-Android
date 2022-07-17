package com.example.whac_a_mole.data.local

import android.content.Context

class PlayerScoreStorageImpl(
    context: Context
) : PlayerScoreStorage {

    private val sharedPreferences =
        context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)

    override fun saveUserScore(score: Int) {
        sharedPreferences.edit().putInt(KEY_SCORE, score).apply()
    }

    override fun getUserScore(): Int {
        return sharedPreferences.getInt(KEY_SCORE, DEFAULT_VALUE)
    }

    private companion object {
        const val SHARED_PREFS_NAME = "user_score"
        const val KEY_SCORE = "score"
        const val DEFAULT_VALUE = 0
    }
}