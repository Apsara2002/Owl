package com.example.owl

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.graphics.Rect
import android.os.Handler
import android.view.Display
import android.view.MotionEvent
import android.view.View
import java.util.Random

class GameView(context:Context?):View(context) {
    //This is our custom View class
    private val handler:Handler //Handler is required to schedule a runnable after some delay
    var runnable:Runnable
    var UPDATE_MILLS=30
    var background:Bitmap
    var toptube:Bitmap
    var bottomtube:Bitmap
    private val display:Display
    var point:Point
    var dWidth:Int
    var dHeight:Int//Device width and height respectively
    var rect:Rect
    var birds:Array<Bitmap?>//Lets create a Bitmap array for the bird

    //We need an integer variable to keep track of bird image/frame
    var birdFrame=0
    var velocity=0
    var gravity=3//Lets play around with these values

    //We need to keep track of bird [position
    var birdX:Int
    var birdY:Int
    var gameState=false
    var gap=600//Gap between top tube and bottom tube
    var minTubeOffset:Int
    var maxTubeOffset:Int
    var numberOfTubes=4
    var distanceBetweenTubes:Int
    var tubeX=IntArray(numberOfTubes)
    var topTubeY=IntArray(numberOfTubes)
    var random:Random
    var tubeVelocity=8
    var score=0





    init {
        handler=Handler()
        runnable= Runnable {
            invalidate()//This will call onDraw()
        }
        background=BitmapFactory.decodeResource(resources,R.drawable.night)
        toptube=BitmapFactory.decodeResource(resources,R.drawable.bambooo)
        bottomtube = BitmapFactory.decodeResource(resources, R.drawable.bottom)
        display=(getContext() as Activity).windowManager.defaultDisplay
        point=Point()
        display.getSize(point)
        dWidth=point.x
        dHeight=point.y
        rect=Rect(0,0,dWidth,dHeight)
        birds= arrayOfNulls(2)
        birds[0]=BitmapFactory.decodeResource(resources,R.drawable.bird1)
        birds[1] = BitmapFactory.decodeResource(resources, R.drawable.bird2)
        birdX=dWidth/2 - (birds[0]?.width?:0)/2 //Initially bird will be centered horizontally
        birdY=dHeight/2 - (birds[0]?.height ?: 0)/2 // Initially bird will be centered vertically
        distanceBetweenTubes = dWidth * 3 / 4 //Our assumption
        minTubeOffset = gap / 2
        maxTubeOffset=dHeight-minTubeOffset-gap
        random= Random()
        for (i in 0 until numberOfTubes) {
            tubeX[i] = dWidth + i * distanceBetweenTubes
            topTubeY[i] =
                minTubeOffset + random.nextInt(maxTubeOffset - minTubeOffset + 1) //TopTubeY will vary between minTubeOffset and maxTubeOffset
        }

    }

    override fun onDraw(canvas: Canvas){
        super.onDraw(canvas)

        //Draw the background
        canvas.drawBitmap(background,null,rect,null)


        //Update bird frame
        birdFrame= if (birdFrame==0){
            1
        }else{
            0
        }
        if (gameState){
            //Update bird position based on gravity and velocity
            if (birdY < dHeight - birds[0]!!.height || velocity < 0) {
                velocity += gravity
                birdY += velocity
            }

            // Update tube positions and check for collision
            for (i in 0 until numberOfTubes){
                tubeX[i]-=tubeVelocity
                if (tubeX[i]<-toptube.width){
                    tubeX[i] += numberOfTubes * distanceBetweenTubes
                    topTubeY[i] = minTubeOffset + random.nextInt(maxTubeOffset - minTubeOffset + 1)
                }
                canvas.drawBitmap(
                    toptube,
                    tubeX[i].toFloat(),
                    (topTubeY[i]-toptube.height).toFloat(),
                    null
                )
                canvas.drawBitmap(
                    bottomtube,
                    tubeX[i].toFloat(),
                    (topTubeY[i] + gap).toFloat(),
                    null
                )

                //Check for collision with tubes
                if (birdX + birds[0]!!.width > tubeX[i] && birdX < tubeX[i] + toptube.width &&
                    (birdY < topTubeY[i] || birdY + birds[0]!!.height > topTubeY[i] + gap)
                ) {
                    // Collision detected, game over
                    gameState = false
                    showGameOverDialog(score)
                    return
                }
                if (tubeX[i] + toptube.width < birdX && tubeX[i] + toptube.width + tubeVelocity > birdX && birdY > topTubeY[i] && birdY < topTubeY[i] + gap) {
                    // Bird passed the tube without collision, increase score
                    score++
                }
            }
        }

        // Draw the bird
        canvas.drawBitmap(birds[birdFrame]!!, birdX.toFloat(), birdY.toFloat(), null)

        // Draw the score on the canvas
        val paint = Paint()
        paint.textSize = 70f
        paint.color = Color.WHITE
        canvas.drawText("Score: $score", 50f, 100f, paint)
        handler.postDelayed(runnable, UPDATE_MILLS.toLong())



    }




    private fun showGameOverDialog(finalScore: Int) {
        var highScore = ScoreManager.getHighScore(context)
        if (finalScore > highScore) {
            ScoreManager.setHighScore(context, finalScore)
            highScore = finalScore // Update displayed high score
        }
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Game Over")
        builder.setMessage("You collided with a tube. Your score: $finalScore")
        builder.setPositiveButton("Restart") { dialog: DialogInterface?, which: Int -> restart() } // Restart game on button click
        builder.setNegativeButton("Exit") { dialog: DialogInterface?, which: Int -> (context as Activity).finish() }
        builder.setCancelable(false) // Prevent dialog from being dismissed by tapping outside
        val dialog = builder.create()
        dialog.show()
    }



    private fun restart() {
        // Reset game variables
        birdY = dHeight / 2 - birds[0]!!.height / 2
        velocity = 0
        score = 0
        gameState = true

        // Reset tube positions
        for (i in 0 until numberOfTubes) {
            tubeX[i] = dWidth + i * distanceBetweenTubes
            topTubeY[i] = minTubeOffset + random.nextInt(maxTubeOffset - minTubeOffset + 1)
        }

        // Restart the game loop by posting a delayed runnable
        handler.postDelayed(runnable, UPDATE_MILLS.toLong())
    }




    // touch event
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val action = event.action

        if (action == MotionEvent.ACTION_DOWN) { //That is tap is detected on screen
            //Here we want to move the bird upwards by some unit
            velocity = -30 //Lets say , 30 units on upward direction
            gameState = true


        }
        return true//By returing true indicate that we've done with touch event and no further action is required by Android
    }




}