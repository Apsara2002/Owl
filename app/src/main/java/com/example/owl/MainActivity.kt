package com.example.owl

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

     fun startGame(view: View){
         val intent = Intent(this, HighScore::class.java).apply {
             startActivity(this)
         }
         finish()
     }
}