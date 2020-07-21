package com.rndash.mbheadunit

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
@ExperimentalStdlibApi
class FullscreenActivity : AppCompatActivity() {

    companion object {
        var comm: CarComm? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()
        setContentView(R.layout.activity_fullscreen)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
            actionBar?.hide()
        }
    }

    override fun onResume() {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        comm?.quit()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        val dm = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)
        val x2 = Math.pow(dm.widthPixels / dm.xdpi.toDouble(), 2.0)
        val y2 = Math.pow(dm.heightPixels / dm.ydpi.toDouble(), 2.0)
        var screenInches = Math.sqrt(x2 + y2)
        println("Screen size: $screenInches. Dimensions: ${dm.widthPixels} x ${dm.heightPixels}")
        var x : UsbManager = getSystemService(Context.USB_SERVICE) as UsbManager
        var dev : UsbDevice? = null
        x.deviceList.forEach { (_, u) ->
            println("Found device V: ${u.vendorId} D: ${u.deviceId}")
            if (u.vendorId == 0x1a86) { // Arduino Uno
                val pi = PendingIntent.getBroadcast(
                    this, 0,
                    Intent("com.rnd-ash.github.mbui"), 0
                )
                dev = u
                x.requestPermission(dev, pi);
                return@forEach
            }
        }
        if (dev == null) {
            Log.e("MAIN", "No Arduino found!")
            Toast.makeText(this, "Error. Arduino not found!", Toast.LENGTH_LONG).show()
        } else {
            Log.d("MAIN", "Arduino found!")
            comm = CarComm(dev!!, x)
            Toast.makeText(this, "Connected to car!", Toast.LENGTH_SHORT).show()
        }
    }
}
