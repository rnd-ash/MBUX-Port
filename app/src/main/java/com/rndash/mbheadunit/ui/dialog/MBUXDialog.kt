package com.rndash.mbheadunit.ui.dialog

import android.app.Activity
import android.app.Dialog
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import com.rndash.mbheadunit.R

open class MBUXDialog(a: Activity, private val title: String, private val body: String, private val timeout: Int, private val audio: Int = R.raw.attention) : Dialog(a) {

    lateinit var mp: MediaPlayer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        super.setCancelable(false)
        super.getWindow()?.addFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
        super.getWindow()?.setBackgroundDrawableResource(android.R.color.transparent)
        super.requestWindowFeature(Window.FEATURE_NO_TITLE)

        setContentView(R.layout.alert)
        val title = findViewById<TextView>(R.id.alert_title)
        val body = findViewById<TextView>(R.id.alert_text)
        val dismiss_btn = findViewById<ImageView>(R.id.alert_dismiss)
        dismiss_btn.setOnClickListener {
            this.cancel()
        }
        dismiss_btn.setOnClickListener { this.cancel() }
        title.text = this.title
        body.text = this.body

        mp = MediaPlayer.create(context, audio)
        mp.isLooping = true
        mp.start()

        super.setOnCancelListener {
            if (mp.isPlaying) {
                mp.stop()
            }
            mp.release()
        }

        if(timeout != 0) {
            Handler().postDelayed(Runnable { this.cancel() }, timeout.toLong())
        }
    }
}