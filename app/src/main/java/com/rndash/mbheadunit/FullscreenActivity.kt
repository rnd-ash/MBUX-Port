package com.rndash.mbheadunit

import android.app.PendingIntent
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.media.AudioManager
import android.microntek.CarManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.rndash.mbheadunit.car.KeyManager
import com.rndash.mbheadunit.nativeCan.CanBusNative
import com.rndash.mbheadunit.nativeCan.KombiDisplay
import com.rndash.mbheadunit.nativeCan.canB.DBE_A1
import com.rndash.mbheadunit.nativeCan.canB.SAM_H_A2
import com.rndash.mbheadunit.ui.ACDisplay
import com.rndash.mbheadunit.ui.LightsDisplay
import com.rndash.mbheadunit.ui.MPGDisplay
import com.rndash.mbheadunit.ui.PTDisplay
import java.util.*
import java.util.jar.Manifest


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
@ExperimentalStdlibApi
@ExperimentalUnsignedTypes
class FullscreenActivity : FragmentActivity() {
    private lateinit var viewPager: ViewPager2
    companion object {
        val carManager = CarManager()
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
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()
        setContentView(R.layout.activity_fullscreen)
        //val bar = findViewById<Fragment>(R.id.status_bar_fragment)
        viewPager = findViewById(R.id.ui_fragment)
        val pagerAdapter = ScreenSlidePagerAdapter(this)
        viewPager.setPageTransformer(ZoomOutPageTransformer())
        viewPager.adapter = pagerAdapter

        askForPermission(android.Manifest.permission.RECORD_AUDIO, 137)
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
            Thread {
                Thread.sleep(1000)
                //CanBusNative.sendBytesToBuffer("C06086F43062DFA003600\r\n".toByteArray(Charsets.US_ASCII), 23)
                sendToBusTest(CanFrame(0x0090, 'B', byteArrayOf(0xB0.toByte(), 0x59, 0x57)))
                sendToBusTest(CanFrame(0x608, 'C', byteArrayOf(0x6E, 0x41, 0x06, 0x2D, 0xFA.toByte(), 0x02, 0x5C, 0x00)))

                Thread.sleep(10)
                println(SAM_H_A2)
            }.start()
        } else {
            Log.d("MAIN", "Arduino found!")
            comm = CarComm(dev!!, x)
        }

        //val mbux = MBUXDialog(this)
        //mbux.show()
        val bg = findViewById<ImageView>(R.id.ui_bg)

        Thread {
            while(true) {
                if (DBE_A1.get_daemmer()) { // Light sensor request dim please
                    println("Sensor activated - Dimming!")
                    window.attributes.screenBrightness = 0.5f
                } else {
                    window.attributes.screenBrightness = 1.0f
                }
                when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
                    in 5..7 -> runOnUiThread { bg.setImageResource(R.drawable.bg_dusk) }
                    in 7..9 -> runOnUiThread { bg.setImageResource(R.drawable.bg_mid) }
                    in 9..11 -> runOnUiThread { bg.setImageResource(R.drawable.bg_rise) }
                    in 11..15 -> runOnUiThread { bg.setImageResource(R.drawable.bg_day) }
                    in 15..18 -> runOnUiThread { bg.setImageResource(R.drawable.bg_mid) }
                    in 18..21 -> runOnUiThread { bg.setImageResource(R.drawable.bg_dusk) }
                    else -> runOnUiThread { bg.setImageResource(R.drawable.bg_night) }
                }
                Thread.sleep(2000)
            }
        }.start()

        // Register for all Microntek intents
        val intentFilter = IntentFilter()
        intentFilter.addAction("com.microntek.bootcheck")
        intentFilter.addAction("com.microntek.removetask")
        intentFilter.addAction("hct.btmusic.play")
        intentFilter.addAction("hct.btmusic.pause")
        intentFilter.addAction("hct.btmusic.prev")
        intentFilter.addAction("hct.btmusic.next")
        intentFilter.addAction("hct.btmusic.info")
        intentFilter.addAction("hct.btmusic.playpause")
        intentFilter.addAction("com.microntek.bt.report")
        intentFilter.addAction("com.microntek.btbarstatechange")
        intentFilter.addAction("com.btmusic.finish")
        intentFilter.addAction("a2dp_play_type")

        registerReceiver(IntentManager(), intentFilter)
        KeyManager.watcher.start()

        KeyManager.registerPageUpListener(KeyManager.KEY.VOLUME_UP, object : KeyManager.KeyListener {
            override fun onLongPress(pg: KeyManager.PAGE) {
                // Ignore long presses for volume
            }

            override fun onShortPress(pg: KeyManager.PAGE) {
                // send intent to MTC
                modifyVolume(true)
            }
        })

        KeyManager.registerPageUpListener(KeyManager.KEY.VOLUME_DOWN, object : KeyManager.KeyListener {
            override fun onLongPress(pg: KeyManager.PAGE) {
                // Ignore long presses for volume
            }

            override fun onShortPress(pg: KeyManager.PAGE) {
                // Send intent to MTC
                modifyVolume(false)
            }
        })

        KeyManager.registerPageUpListener(KeyManager.KEY.PAGE_UP, object : KeyManager.KeyListener {
            override fun onLongPress(pg: KeyManager.PAGE) {
                println("Page up long press. Page: $pg")
            }

            override fun onShortPress(pg: KeyManager.PAGE) {
                if (pg == KeyManager.PAGE.AUDIO) {
                    BTMusic.playNext()
                }
            }
        })

        KeyManager.registerPageUpListener(KeyManager.KEY.PAGE_DOWN, object : KeyManager.KeyListener {
            override fun onLongPress(pg: KeyManager.PAGE) {
                println("Page up long press. Page: $pg")
            }

            override fun onShortPress(pg: KeyManager.PAGE) {
                if (pg == KeyManager.PAGE.AUDIO) {
                    BTMusic.playPrev()
                }
            }
        })

        BTMusic.setAudioManager(getSystemService(Service.AUDIO_SERVICE) as AudioManager)
        BTMusic.focusBTMusic()
        val i = Intent()
        i.component = ComponentName("android.microntek.mtcser", "android.microntek.mtcser.BlueToothService")
        bindService(i, BTMusic.serviceConnection, BIND_AUTO_CREATE)
        KombiDisplay.setAudioBodyText("NO MUSIC", arrayOf(KombiDisplay.TEXT_FMT.CENTER_JUSTIFIED, KombiDisplay.TEXT_FMT.FLASHING))
        KombiDisplay.setAudioHeaderText("No audio src", arrayOf(KombiDisplay.TEXT_FMT.LEFT_JUSTIFIED))
    }

    private inner class ScreenSlidePagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = 4

        override fun createFragment(position: Int): Fragment = when(position) {
            1 -> ACDisplay()
            2 -> MPGDisplay()
            3 -> LightsDisplay()
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

    private fun sendToBusTest(f: CanFrame) {
        var tmp = String.format("%c%04X", f.bus, f.canID)
        (0 until f.dlc).forEach {
            tmp += String.format("%02X", f.data[it])
        }
        tmp += "\r\n"
        val bs = tmp.toByteArray(Charsets.US_ASCII)
        CanBusNative.sendBytesToBuffer(bs, bs.size)
        println(bs.size)
    }

    // Sends an intent asking the headunit to set volume
    private fun modifyVolume(increase: Boolean) {
        val intent = Intent().apply {
            this.action = "com.microntek.VOLUME_SET"
            this.putExtra("type", if (increase) "add" else "sub")
        }
        applicationContext.sendBroadcast(intent)
    }

    private fun askForPermission(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(applicationContext, permission) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                Toast.makeText(applicationContext, "Please grant the requested permission to get your task done!", Toast.LENGTH_LONG).show()
                ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
            }
        }
    }

}
