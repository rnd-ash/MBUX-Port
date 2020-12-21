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
import android.view.KeyEvent
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
import com.rndash.mbheadunit.nativeCan.canB.TPM_A1
import com.rndash.mbheadunit.ui.*
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
    private lateinit var pagerAdapter: ScreenSlidePagerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()
        setContentView(R.layout.activity_fullscreen)
        viewPager = findViewById(R.id.ui_fragment)
        pagerAdapter = ScreenSlidePagerAdapter(this)
        viewPager.setPageTransformer(ZoomOutPageTransformer())
        viewPager.adapter = pagerAdapter

        askForPermission(android.Manifest.permission.RECORD_AUDIO, 137)
        askForPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, 137)
        askForPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE, 137)
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
            CarComm.init_test()
            Thread {
                Thread.sleep(1000)
                sendToBusTest(
                        CanFrame(
                                0x01D0,
                                'B',
                                byteArrayOf(0x03,0x05,0x22,0xD7.toByte(),0x01,0x00,0xC2.toByte(),0x00)
                        ))
            }.start()
        } else {
            Log.d("MAIN", "Arduino found!")
            comm = CarComm(dev!!, x)
        }

        //val mbux = MBUXDialog(this)
        //mbux.show()
        val bg = findViewById<ImageView>(R.id.ui_bg)
        Timer().schedule(object: TimerTask() {
            override fun run() {
                runOnUiThread {
                    if (CarData.isSportFeel) {
                        bg.setImageResource(R.drawable.bg_sport)
                    } else {
                        bg.setImageResource(R.drawable.bg_normal)
                    }
                }
            }
        }, 0, 500)

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

        val i = Intent()
        i.component = ComponentName("android.microntek.mtcser", "android.microntek.mtcser.BlueToothService")
        bindService(i, BTMusic.serviceConnection, BIND_AUTO_CREATE)

        KombiDisplay.setAudioHeaderText("AUDIO", arrayOf(KombiDisplay.TEXT_FMT.CENTER_JUSTIFIED))
        KombiDisplay.setAudioSymbol(KombiDisplay.AUDIO_SYMBOL.NONE, KombiDisplay.AUDIO_SYMBOL.NONE)
        KombiDisplay.setAudioBodyText("NO SOURCE", arrayOf(KombiDisplay.TEXT_FMT.CENTER_JUSTIFIED))

        KombiDisplay.setTelHeaderText("TELEPHONE", arrayOf(KombiDisplay.TEXT_FMT.CENTER_JUSTIFIED))
        KombiDisplay.setTelBodyText("NOT", arrayOf(KombiDisplay.TEXT_FMT.CENTER_JUSTIFIED),
                "CONNECTED", arrayOf(KombiDisplay.TEXT_FMT.CENTER_JUSTIFIED),
                "", arrayOf(KombiDisplay.TEXT_FMT.CENTER_JUSTIFIED),
                "", arrayOf(KombiDisplay.TEXT_FMT.CENTER_JUSTIFIED)
        )
        BTMusic.focusBT()
        CarData.dataCollector.start()
    }

    private inner class ScreenSlidePagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = 4
        private lateinit var curr_fragment: UIFragment
        override fun createFragment(position: Int): Fragment {
            curr_fragment = when (position) {
                1 -> ACDisplay()
                2 -> MPGDisplay()
                3 -> LightsDisplay()
                else -> PTDisplay()
            }
            return curr_fragment
        }

        fun keyEvent(code: Int, event: KeyEvent): Boolean {
            return curr_fragment.onKeyDown(code, event)
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
                ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return pagerAdapter.keyEvent(keyCode, event!!)
    }
}
