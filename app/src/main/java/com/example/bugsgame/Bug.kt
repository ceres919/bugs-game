package com.example.bugsgame

import android.graphics.Bitmap
import kotlin.random.Random


class Bug() : Thread() {
    var x: Float = 0f
    var y: Float = 0f
    private var speedX: Float = 0f
    private var speedY: Float = 0f
    var frameIndex: Int = 0
    lateinit var bitmap: Array<Bitmap?>
    private var isAlive: Boolean = true
    private var displayWidth: Int = 0
    private var displayHeight: Int = 0
    var deathBitmap: Bitmap? = null

    constructor(_bitmap: Array<Bitmap?>, _deathBitmap: Bitmap?, _displayWidth: Int, _displayHeight: Int) : this() {
        bitmap = _bitmap
        deathBitmap = _deathBitmap
        displayWidth = _displayWidth
        displayHeight = _displayHeight
        x = ((0 + getBugWidth()..displayWidth - getBugWidth()).random().toFloat())
        y = ((0 + getBugHeight()..displayHeight - getBugHeight()).random().toFloat())
        speedX = Random.nextDouble(-15.0, 15.0).toFloat()
        speedY = Random.nextDouble(-15.0, 15.0).toFloat()
    }

    fun getBugWidth(): Int {
        return bitmap[0]!!.width
    }

    fun getBugHeight(): Int {
        return bitmap[0]!!.height
    }

    fun getBugBitmap(frameIndex: Int) : Bitmap? {
        return bitmap[frameIndex]
    }

    private fun move() {
        if (x <= 0 || x >= displayWidth - getBugWidth()) {
                speedX = -speedX
        }
        if (y <= 0 || y >= displayHeight - getBugHeight()) {
            speedY = -speedY
        }
        x += speedX
        y += speedY
    }

    fun kill() {
        isAlive = false
        for(i in 0..<bitmap.size) {
            bitmap[i] = deathBitmap
        }
    }

    override fun run() {
        while (isAlive) {
            move()
            try {
                Thread.sleep(15)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }
}