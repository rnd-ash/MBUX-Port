package com.rndash.mbheadunit.warnings

import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.rndash.mbheadunit.R

open class MBUXDialog(
    private val img_res: Int,
    private val audio_res: Int,
    private val audio_loop: Boolean,
    private val severity: WarnLevel,
    private val msg: String,
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

        setContentView(R.layout.alert)
        val title: TextView = findViewById(R.id.alert_title)
        val bg : ConstraintLayout = findViewById(R.id.alert_bg)
        when(severity) {
            WarnLevel.INFO -> {
                title.text = "INFO"
                bg.setBackgroundResource(R.drawable.alert_box_info)
            }
            WarnLevel.WARNING -> {
                title.text = "WARNING"
                bg.setBackgroundResource(R.drawable.alert_box_warn)
            }
            WarnLevel.CRITICAL -> {
                title.text = "CRITICAL"
                bg.setBackgroundResource(R.drawable.alert_box_critical)
            }
        }
        val text: TextView = findViewById(R.id.alert_text)
        text.text = msg

        val icon: ImageView = findViewById(R.id.alert_image)
        icon.setImageResource(img_res)

        val dismiss : Button = findViewById(R.id.alert_dismiss)
        dismiss.setOnClickListener {
            this.cancel()
        }
        mp = MediaPlayer.create(context, audio_res)
        mp.isLooping = audio_loop
        mp.start()

        super.setOnCancelListener {
            println("Goodbyte!")
            if (mp.isPlaying) {
                mp.stop()
            }
            mp.release()
        }
    }
}