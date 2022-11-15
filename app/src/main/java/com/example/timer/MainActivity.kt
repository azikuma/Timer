package com.example.timer

import android.annotation.SuppressLint
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.TextView
import com.example.timer.databinding.ActivityMainBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var soundPool: SoundPool
    private var soundResId = 0
    private var streamId = 0

    inner class MyCountDownTimer(millisInFuture: Long, countDownInterval: Long) : CountDownTimer(millisInFuture, countDownInterval) {

        var isRunning = false

        override fun onTick(millisUntilFinished: Long) {
            val minute = millisUntilFinished / 1000L / 60L
            val second = millisUntilFinished / 1000L % 60L
            binding.timerText.text = "%1d:%2$02d".format(minute, second)
        }

        override fun onFinish() {
            binding.timerText.text = "0:00"
            //停止するために変数に格納
            streamId = soundPool.play(soundResId, 1.0f, 100f, 0, 0, 1.0f)
        }

    }

    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val timerText = binding.timerText
        val playStop = binding.playStop
        val editNum = binding.editTextNumber

//        timerText.text = "3:00"
        timerText.text = "0:10"
        val timer = MyCountDownTimer(10 * 1000 , 100)
        playStop.setOnClickListener {
            timer.isRunning = when (timer.isRunning) {
                true -> {
                    timer.cancel()
                    //音楽停止するには、streamIdを渡す
                    soundPool.stop(streamId)
                    playStop.setImageResource(
                        R.drawable.ic_baseline_play_arrow_24
                    )
                    false
                }
                false -> {
                    timer.start()
                    playStop.setImageResource(
                        R.drawable.ic_baseline_stop_24
                    )
                    true
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
//        soundPool = SoundPool(2, AudioManager.STREAM_ALARM, 0)
        //Lollipop未満
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
            soundPool = SoundPool(2, AudioManager.STREAM_ALARM, 0)
        }else{
            //AudioAttributesでstreamTypeを指定
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM).build()
            soundPool = SoundPool.Builder()
                .setMaxStreams(2).setAudioAttributes(audioAttributes).build()
        }
        soundResId = soundPool.load(this, R.raw.bellsound, 1)
    }

    override fun onPause() {
        super.onPause()
        soundPool.release()
    }

}