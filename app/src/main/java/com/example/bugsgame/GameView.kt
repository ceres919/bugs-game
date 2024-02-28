package com.example.bugsgame

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.media.AudioManager
import android.media.SoundPool
import android.view.MotionEvent
import android.view.View
import java.util.Timer
import java.util.Vector
import kotlinx.coroutines.*
import kotlin.concurrent.timerTask

class GameView(context: Context) : View(context) {

    private val bugsBitmaps: MutableMap<String, Array<Bitmap?>> = mutableMapOf()
    private val bugs: Vector<Bug> = Vector()
    private val bugFramesCount: Int = 2
    private var timer: Timer? = null
    private var score: Int = 0
    private var attempts: Int = 10
    private val sounds = SoundPool(10, AudioManager.STREAM_MUSIC, 0);
    private val hit = sounds.load(context, R.raw.hit_sound, 1);
    private val miss = sounds.load(context, R.raw.miss_sound, 1);
    private var displayWidth: Int = 0
    private var displayHeight: Int = 0

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        postInvalidate()
        val paint = Paint()
        paint.textSize = 50f
        paint.color = Color.BLACK
        canvas.drawColor(Color.WHITE)
        canvas.drawText("Score: $score", 50f, 110f, paint)
        canvas.drawText("Attempts left: $attempts", 50f, 50f, paint)

        synchronized(bugs){
            for (bug in bugs) {
                canvas.drawBitmap(bug.getBugBitmap(bug.frameIndex)!!, bug.x, bug.y, null)


                ++bug.frameIndex

                if(bug.frameIndex >= bugFramesCount) {
                    bug.frameIndex = 0
                }

            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            synchronized(bugs) {
                for (bug in bugs) {
                    if (event.x >= bug.x && event.x < bug.x + bug.getBugWidth()
                        && event.y >= bug.y && event.y < bug.y + bug.getBugHeight() && bug.isAlive
                    ) {
                        bug.kill()
                        sounds.play(hit, 1.0f, 1.0f, 0, 0, 1.5f)
                        score++
                        if (score == 30 || attempts <= 0) gameOver()

                        handler.postDelayed({
                            synchronized(bugs) {
                                bugs.remove(bug)
                            }
                        }, 5000)

                        return true
                    }
                }
            }
            sounds.play(miss, 1.0f, 1.0f, 0, 0, 1.5f)
            attempts--
            if (attempts <= 0) gameOver()
        }
        return super.onTouchEvent(event)
    }

    @SuppressLint("NewApi")
    private fun spawnRandBug() {
        val bugBitmap: Array<Bitmap?> = arrayOfNulls<Bitmap>(3)
        val crashedBitmap: Bitmap?
        when ((0..2).random()) {
            0 -> {
                bugBitmap[0] = bugsBitmaps["ant"]!![0]?.let { Bitmap.createScaledBitmap(it, 200, 300, true) }
                bugBitmap[1] = bugsBitmaps["ant"]!![1]?.let { Bitmap.createScaledBitmap(it, 200, 300, true) }
                crashedBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(resources, R.drawable.ant_dead), 200, 300, true)
            }
            1 -> {
                bugBitmap[0] = bugsBitmaps["cockroach"]!![0]?.let { Bitmap.createScaledBitmap(it, 200, 300, true) }
                bugBitmap[1] = bugsBitmaps["cockroach"]!![1]?.let { Bitmap.createScaledBitmap(it, 200, 300, true) }
                crashedBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(resources, R.drawable.cockroach_dead), 200, 300, true)            }
            else -> {
                bugBitmap[0] = bugsBitmaps["fly"]!![0]?.let { Bitmap.createScaledBitmap(it, 200, 300, true) }
                bugBitmap[1] = bugsBitmaps["fly"]!![1]?.let { Bitmap.createScaledBitmap(it, 200, 300, true) }
                crashedBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(resources, R.drawable.fly_dead), 200, 300, true)            }
        }
        val bug = Bug(bugBitmap, crashedBitmap, displayWidth, displayHeight)
        this.bugs.add(bug)
        bug.start()
    }

    init {
        val displayMetrics = resources.displayMetrics
        displayWidth = displayMetrics.widthPixels
        displayHeight = displayMetrics.heightPixels
        val bug1Bitmap: Array<Bitmap?> = arrayOfNulls(2)

        bug1Bitmap[0] = BitmapFactory.decodeResource(resources, R.drawable.ant1)
        bug1Bitmap[1] = BitmapFactory.decodeResource(resources, R.drawable.ant2)
        bugsBitmaps["ant"] = bug1Bitmap

        val bug2Bitmap: Array<Bitmap?> = arrayOfNulls(2)
        bug2Bitmap[0] = BitmapFactory.decodeResource(resources,R.drawable.cockroach1)
        bug2Bitmap[1] = BitmapFactory.decodeResource(resources, R.drawable.cockroach2)
        bugsBitmaps["cockroach"] = bug2Bitmap

        val bug3Bitmap: Array<Bitmap?> = arrayOfNulls(2)
        bug3Bitmap[0] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(resources,R.drawable.fly1), 200, 300, true)
        bug3Bitmap[1] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(resources, R.drawable.fly2), 200, 300, true)
        bugsBitmaps["fly"] = bug3Bitmap

        synchronized(bugs){
            for (i in 0..5) {
                spawnRandBug()
            }
        }

        this.timer = Timer()
        synchronized(bugs){
            timer?.scheduleAtFixedRate(timerTask {

                if (bugs.size < 13) spawnRandBug()
            }, 0, 500)
        }
    }

    private fun gameOver() {
        for (bug in bugs) {
            bug.kill()
        }
        bugs.clear()
        val intent = Intent(context, GameOverActivity::class.java)
        val message = if (attempts <= 0) "WASTED" else "MISSION PASSED!\nRESPECT +"
        intent.putExtra("message", message)
        context.startActivity(intent)
        (context as Activity).finish()
    }
}