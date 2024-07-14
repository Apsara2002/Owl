package com.example.owl

import android.app.Activity
import android.os.Bundle

class StartGame : Activity() {
    var gameView:GameView?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gameView=GameView(this)
        setContentView(gameView)
    }
}