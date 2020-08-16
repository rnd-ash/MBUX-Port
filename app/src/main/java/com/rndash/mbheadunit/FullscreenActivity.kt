package com.rndash.mbheadunit

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.media.AudioManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.rndash.mbheadunit.nativeCan.canB.CanBAddrs
import com.rndash.mbheadunit.nativeCan.canC.CanCAddrs
import com.rndash.mbheadunit.nativeCan.CanBusNative
import com.rndash.mbheadunit.nativeCan.canB.KLA_A1
import com.rndash.mbheadunit.ui.ACDisplay
import com.rndash.mbheadunit.ui.MPGDisplay
import com.rndash.mbheadunit.ui.PTDisplay
import com.rndash.mbheadunit.ui.dialog.MBUXDialog
import java.lang.RuntimeException


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
@ExperimentalStdlibApi
@ExperimentalUnsignedTypes
class FullscreenActivity : FragmentActivity() {
    private lateinit var viewPager: ViewPager2
    companion object {
        private lateinit var audiomanager: AudioManager
        var comm: CarComm? = null
        lateinit var volContext: Context
        var volume = 10
        fun modifyVolume(increase: Boolean) {
            val intent = Intent().apply {
                this.setAction("com.microntek.VOLUME_SET")
                when(increase) {
                    true -> this.putExtra("type", "add")
                    false -> this.putExtra("type", "sub")
                }
            }
            volContext.sendBroadcast(intent)
        }
        init {
            System.loadLibrary("canbus-lib")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        volContext = this
        audiomanager = applicationContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()
        setContentView(R.layout.activity_fullscreen)
        viewPager = findViewById(R.id.ui_fragment)
        val pagerAdapter = ScreenSlidePagerAdapter(this)
        viewPager.setPageTransformer(ZoomOutPageTransformer())
        viewPager.adapter = pagerAdapter
        val am = this.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        try {
            am.setStreamVolume(AudioManager.STREAM_NOTIFICATION, 10, 0)
        } catch (e: RuntimeException) {
            // Ignore - this is due to DND
        }
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
                x.requestPermission(dev, pi)
                return@forEach
            }
        }
        if (dev == null) {
            Log.e("MAIN", "No Arduino found!")
            Toast.makeText(this, "Error. Arduino not found!", Toast.LENGTH_LONG).show()
            CanBusNative.init()
        } else {
            Log.d("MAIN", "Arduino found!")
            comm = CarComm(dev!!, x)
            Toast.makeText(this, "Connected to car!", Toast.LENGTH_SHORT).show()
        }

        val mbux = MBUXDialog(this)
        mbux.show()
        Thread {
            Thread.sleep(1000)
            CanBusNative.sendBytesToBuffer("B00302000031A00260150\r\n".toByteArray(Charsets.US_ASCII), 23)
            Thread.sleep(10)
            println(KLA_A1)
            println(CanBusNative.getBFrame(CanBAddrs.KLA_A1))
        }.start()
    }

    private inner class ScreenSlidePagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = 3

        override fun createFragment(position: Int): Fragment = when(position) {
            1 -> ACDisplay()
            2 -> MPGDisplay()
            else -> PTDisplay()
        }
    }

    @RequiresApi(21)
    class ZoomOutPageTransformer : ViewPager2.PageTransformer {
        override fun transformPage(view: View, position: Float) {
            view.apply {
                when {
                    position < -1 -> {
                        alpha = 0f
                    }
                    position <= 1 -> {
                        translationX = 0.0f
                    }
                    else -> { alpha = 0f }
                }
            }
        }
    }
}
