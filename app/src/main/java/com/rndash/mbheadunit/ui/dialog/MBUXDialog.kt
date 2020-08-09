package com.rndash.mbheadunit.ui.dialog

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import com.rndash.mbheadunit.R

open class MBUXDialog(a: Activity) : Dialog(a) {
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
        dismiss_btn.setOnClickListener { this.cancel() }
        title.text = "Warning"
        body.text = "This is a test message. Please do nothing"
        super.setOnCancelListener {
            // TODO custom close code
        }
    }
}