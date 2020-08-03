package com.rndash.mbheadunit.warnings

import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.rndash.mbheadunit.R

open class MBUXDialog(
    private val img_res: Int,
    private val audio_res: Int,
    private val audio_loop: Boolean,
    private val severity: WarnLevel,
    private val msg_text: String,
    private val title_text: String,
    a: Activity
) : Dialog(a) {
    enum class WarnLevel {
        INFO,
        WARNING,
        CRITICAL
    }
    lateinit var mp: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        super.getWindow()?.addFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
        super.getWindow()?.setBackgroundDrawableResource(android.R.color.transparent)
        super.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val textColour = when(severity) {
            WarnLevel.CRITICAL -> Color.RED
            WarnLevel.WARNING -> Color.YELLOW
            WarnLevel.INFO -> Color.WHITE
        }
        setContentView(R.layout.alert)
        val title: TextView = findViewById(R.id.alert_title)
        title.text = title_text
        title.setTextColor(textColour)
        val text: TextView = findViewById(R.id.alert_msg)
        text.text = msg_text
        text.setTextColor(textColour)

        val icon: ImageView = findViewById(R.id.alert_img)
        icon.setImageResource(img_res)

        val dismiss : ImageView = findViewById(R.id.btn_dismiss)
        dismiss.setOnClickListener {
            this.cancel()
        }
        mp = MediaPlayer.create(context, audio_res)
        mp.isLooping = audio_loop
        mp.start()

        super.setOnCancelListener {
            println("Goodbye!")
            if (mp.isPlaying) {
                mp.stop()
            }
            mp.release()
        }
    }
}