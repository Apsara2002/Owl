package com.example.owl

import android.content.Context

object ScoreManager {
    private const val PREF_NAME="HighScorePref"
    private const val KEY_HIGH_SCORE = "highScore"
    fun getHighScore(context: Context):Int{
        val sharedPreferences=context.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE)
        return sharedPreferences.getInt(KEY_HIGH_SCORE,0)//Default is 0 if high score not set
    }

    fun setHighScore(context: Context,score:Int){
        val sharedPreferences=context.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE)
        val editor=sharedPreferences.edit()
        editor.putInt(KEY_HIGH_SCORE,score)
        editor.apply()
    }
}