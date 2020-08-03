package com.rndash.mbheadunit.ui.elements

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.media.MediaPlayer
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.Toast
import androidx.appcompat.widget.AppCompatCheckBox
import com.rndash.mbheadunit.R
import java.io.IOException


class MbCheckbox(private val ctx: Context, attrs: AttributeSet) : AppCompatCheckBox(ctx, attrs) {
    init {
        buttonDrawable = null
        text = null
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        super.setSoundEffectsEnabled(false)
        event?.let {
            if (it.action == MotionEvent.ACTION_DOWN) {
            }
        }
        return super.onTouchEvent(event)
    }

    override fun setChecked(checked: Boolean) {
        super.setChecked(checked)
        // Set view image (Checked or not)
        if (checked) {
            this.setBackgroundResource(R.drawable.check_yes)
        } else {
            this.setBackgroundResource(R.drawable.check_no)
        }
    }
}