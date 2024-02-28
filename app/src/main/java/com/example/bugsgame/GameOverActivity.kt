package com.example.bugsgame

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class GameOverActivity : AppCompatActivity() {

    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_over)

        val restartButton = findViewById<Button>(R.id.restart)
        val textView = findViewById<TextView>(R.id.textView)

        restartButton.setOnClickListener{
            setContentView(GameView(this))
            mediaPlayer?.stop()
        }

        val mes = intent.getStringExtra("message")

        textView.text = mes
        if(mes.equals("WASTED")) {
           mediaPlayer = MediaPlayer.create(this, R.raw.lose)
            mediaPlayer?.let {
                it.isLooping = false
                it.start()
            }
        } else {
            mediaPlayer = MediaPlayer.create(this, R.raw.win)
            mediaPlayer?.let {
                it.isLooping = true
                it.start()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}