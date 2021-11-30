package com.example.maracas

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.SystemClock.sleep
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import java.time.LocalDateTime
import java.util.*
import kotlin.math.sqrt
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    private var mSensorManager: SensorManager? = null
    private var mAccel = 0f
    private var mAccelCurrent = 0f
    private var mAccelLast = 0f
    private var mMediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.activity_main)
        mSensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        Objects.requireNonNull(mSensorManager)?.registerListener(
            mSensorListener, mSensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_NORMAL
        )
        mAccel = 10f
        mAccelCurrent = SensorManager.GRAVITY_EARTH
        mAccelLast = SensorManager.GRAVITY_EARTH

    }

    private val mSensorListener: SensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]
            mAccelLast = mAccelCurrent
            mAccelCurrent = sqrt((x * x + y * y + z * z).toDouble()).toFloat()
            val delta = mAccelCurrent - mAccelLast
            mAccel = mAccel * 0.9f + delta
            if (mAccel > 4) {
                playSound()
                sleep(100)

            }
        }
        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
    }

    override fun onResume() {
        mSensorManager!!.registerListener(
            mSensorListener, mSensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_NORMAL
        )
        super.onResume()
    }

    fun playSound() {
        mMediaPlayer = MediaPlayer.create(this, randomSoundSelector())
        mMediaPlayer!!.start()
        mMediaPlayer = null
    }


    override fun onStop() {
        super.onStop()
        mSensorManager!!.unregisterListener(mSensorListener)
        if (mMediaPlayer != null) {
            mMediaPlayer!!.release()
            mMediaPlayer = null
        }
    }

    private fun randomSoundSelector(): Int {

        var random: Int = LocalDateTime.now().toInt()
        random %= 2
        when(random){
            0 -> return R.raw.maraca_sound1
            1 -> return R.raw.maraca_sound2
        }
        return R.raw.maraca_sound1
    }

}
private fun LocalDateTime.toInt(): Int {
    val time = LocalDateTime.now()
    return time.minute+time.second+time.nano
}
