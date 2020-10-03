package com.rndash.mbheadunit

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.microntek.CarManager
import android.os.Bundle
import android.os.Environment
import android.view.KeyEvent
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.rndash.mbheadunit.car.PartyMode
import com.rndash.mbheadunit.car.PartyMode.isEngineOn
import com.rndash.mbheadunit.doom.DoomSurfaceView
import com.rndash.mbheadunit.doom.wad.WadFile
import java.io.File
import java.util.stream.Collectors


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
@ExperimentalStdlibApi
@ExperimentalUnsignedTypes
class DoomActivity : FragmentActivity() {

    lateinit var cview: DoomSurfaceView
    companion object {
        val carManager = CarManager()
        init {
            System.loadLibrary("canbus-lib")
        }
    }
    private lateinit var w: WadFile
    override fun onCreate(savedInstanceState: Bundle?) {
        val f = File(Environment.getExternalStorageDirectory(), "doom.wad")
        if (!f.exists()) {
            val res = applicationContext.resources.openRawResource(R.raw.doom)
            f.createNewFile()
            f.writeBytes(res.readBytes())
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()
        if (isEngineOn()) {
            Toast.makeText(this, "DOOM cannot run with engine on!", Toast.LENGTH_SHORT).show()
            finish()
        }
        val t = Toast.makeText(this, "DOOM LOADING!", Toast.LENGTH_LONG)
        t.show()
        try {
            w = WadFile(f)
            w.loadLevels()
            // Show the select menu
            showLevelMenu(w.getLevelNames().toTypedArray())
        } catch (e: Exception) {
            t.cancel()
            Toast.makeText(this, "Doom failed to load: ${e.message}", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
            finish()
        }

        try {
            cview.inputThread.start()
        } catch (e: Exception) {
            Toast.makeText(this, "Input manager could not be started", Toast.LENGTH_SHORT).show()
        }
        try {
            PartyMode.startThread()
        } catch (e: Exception) {
            Toast.makeText(this, "Light thread could not be started", Toast.LENGTH_SHORT).show()
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

    private var level = ""

    private fun showLevelMenu(levels: Array<String>) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Chose a level")
        builder.setItems(levels) { dialog, which ->
            level = levels[which]
            cview = DoomSurfaceView(this, false, w, level)
            setContentView(cview)
            dialog.cancel()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    override fun onDestroy() {
        try {
            PartyMode.stopThread()
        } catch (e: Exception){}
        try {
            cview.inputThread.interrupt()
        } catch (e: Exception){}


        super.onDestroy()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return cview.processKeyDown(keyCode, event)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        return cview.processKeyUp(keyCode, event)
    }
}
