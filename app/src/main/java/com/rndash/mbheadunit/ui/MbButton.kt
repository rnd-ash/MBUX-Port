package com.rndash.mbheadunit.ui

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.util.AttributeSet
import android.view.MotionEvent
import com.rndash.mbheadunit.R
import kotlin.random.Random
import androidx.appcompat.widget.AppCompatButton

const val res_path : String = "android.resource://com.rndash.mbheadunit/raw/"

class MbButton(private val ctx: Context, attrs: AttributeSet) : AppCompatButton(ctx, attrs) {
    val mp = MediaPlayer()
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        super.setSoundEffectsEnabled(false)
        // Randomise the noise of button press
        event?.let {
            if (it.action == MotionEvent.ACTION_DOWN) {
                val res = when (Random.nextInt(1, 8)) {
                    2 -> "click_02.wav"
                    3 -> "click_02.wav"
                    4 -> "click_02.wav"
                    5 -> "click_02.wav"
                    6 -> "click_02.wav"
                    7 -> "click_02.wav"
                    else -> "click_02.wav"
                }
                mp.stop()
                mp.setDataSource(Uri.parse(res_path + res).toString())
                mp.prepare()
                mp.start()
            }
        }
        return super.onTouchEvent(event)
    }


}