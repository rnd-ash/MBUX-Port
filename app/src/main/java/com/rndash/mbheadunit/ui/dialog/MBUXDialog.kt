package com.rndash.mbheadunit.ui.dialog

import android.app.Activity
import android.app.Dialog
import android.media.MediaPlayer
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import com.rndash.mbheadunit.R

open class MBUXDialog(a: Activity) : Dialog(a) {

    lateinit var mp: MediaPlayer
    lateinit var mp2 : MediaPlayer // For click button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        super.setCancelable(false)
        super.getWindow()?.addFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
        super.getWindow()?.setBackgroundDrawableResource(android.R.color.transparent)
        super.requestWindowFeature(Window.FEATURE_NO_TITLE)

        setContentView(R.layout.alert)
        val title = findViewById<TextView>(R.id.alert_title)
        val body = findViewById<TextView>(R.id.alert_text)
        val image = findViewById<ImageView>(R.id.alert_img)
        val dismiss_btn = findViewById<ImageView>(R.id.alert_dismiss)
        mp2 = MediaPlayer.create(context, R.raw.attention)
        dismiss_btn.setOnClickListener {
            mp2.isLooping = false
            //mp2.start()
            //while(mp2.isPlaying) {
            //}
            //mp2.release()
            this.cancel()
        }
        dismiss_btn.setOnClickListener { this.cancel() }
        title.text = "Warning"
        body.text = "This is a test message. Please do nothing"

        mp = MediaPlayer.create(context, R.raw.attention)
        mp.isLooping = true
        mp.start()

        super.setOnCancelListener {
            if (mp.isPlaying) {
                mp.stop()
            }
            mp.release()
        }
    }
}