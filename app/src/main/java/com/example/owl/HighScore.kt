package com.example.owl

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView

class HighScore : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_high_score)
        val highScoreTextView=findViewById<TextView>(R.id.highScoreTextView)
        val highScore=ScoreManager.getHighScore(this)
        highScoreTextView.text="High Score:$highScore"
    }
    fun playGame(view: View){
        val intent=Intent(this,StartGame::class.java)
        startActivity(intent)
        finish()
    }
}