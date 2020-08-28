package com.rndash.mbheadunit

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.audiofx.Visualizer
import android.microntek.mtcser.BTServiceInf
import android.microntek.mtcser.BTServiceInfStub
import android.os.IBinder
import android.os.Parcel
import android.os.RemoteException
import android.util.Log
import com.rndash.mbheadunit.nativeCan.KombiDisplay
import java.lang.Exception
import kotlin.properties.Delegates

@ExperimentalUnsignedTypes
@ExperimentalStdlibApi
object BTMusic {
    private var aManager: AudioManager? = null
    var btService: BTServiceInf? = null
    val serviceConnection = object: ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            btService = BTServiceInfStub.asInterface(service)
            println("Attempt BT Create")
            try {
                btService!!.init()
            } catch (e: Exception) {
                println("Error binding!")
                e.printStackTrace()
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            println("BT Disconnecting")
            btService = null
        }
    }

    fun setAudioManager(am: AudioManager) {
        aManager = am
        aManager?.abandonAudioFocus(fManager())
    }

    private class fManager() : AudioManager.OnAudioFocusChangeListener {
        override fun onAudioFocusChange(focusChange: Int) {
            when(focusChange) {
                1 -> {
                    println("Focus BT Audio")
                    FullscreenActivity.carManager.setParameters("av_focus_gain=gsm_bt")
                }
                in -3..-1 -> {
                    println("Unfocus BT Audio")
                    FullscreenActivity.carManager.setParameters("av_focus_loss=gsm_bt")
                }
                else -> { println("Unknown audio focus $focusChange") }
            }
        }
    }

    private val audioFocusManager = fManager()

    fun focusBTMusic() {
        aManager?.let {
            it.requestAudioFocus(audioFocusManager, 3, 1)
        }
    }

    fun unFocusBTMusic() {
        aManager?.let {
            it.abandonAudioFocus(audioFocusManager)
        }
    }

    private var sampler: Visualizer? = null
    private var trackNameInternal: String by Delegates.observable("UNKNOWN") { _, o, n ->
        if (n != o) {
            KombiDisplay.setAudioBodyText(n, arrayOf(KombiDisplay.TEXT_FMT.CENTER_JUSTIFIED))
        }
    }
    private var trackArtist: String = "UNKNOWN"
    private var trackAlbum: String = "UNKNOWN"
    private var playing: Boolean by Delegates.observable(false) { _, o, n ->
        when(n) {
            false -> {
                KombiDisplay.setAudioHeaderText("Paused", arrayOf(KombiDisplay.TEXT_FMT.CENTER_JUSTIFIED, KombiDisplay.TEXT_FMT.FLASHING))
                KombiDisplay.setAudioBodyText("Not playing", arrayOf(KombiDisplay.TEXT_FMT.CENTER_JUSTIFIED))
            }
            true -> {
                KombiDisplay.setAudioBodyText(trackNameInternal, arrayOf(KombiDisplay.TEXT_FMT.CENTER_JUSTIFIED))
                KombiDisplay.setAudioHeaderText("Playing BT", arrayOf(KombiDisplay.TEXT_FMT.LEFT_JUSTIFIED))
            }
        }
    }

    fun isPlaying() : Boolean = playing
    fun getTrackName() : String = trackNameInternal
    fun getTrackArtist() : String = trackArtist
    fun getTrackAlbum() : String = trackAlbum

    fun setPlayState(p: Boolean) { playing = p }
    fun setArtist(a: String) { trackArtist = a }
    fun setName(t: String) { trackNameInternal = t }
    fun setAlbum(a: String) { trackAlbum = a }

    fun setupSampler() {
        println("Sampler begin")
        if (sampler != null) {
            println("Sampler already allocated!")
            return // Already allocated
        }
        sampler = Visualizer(0) // System sound channel
        sampler?.let {v ->
            println("Sampler setup")
            v.enabled = false
            v.captureSize = Visualizer.getCaptureSizeRange()[1]
            v.setDataCaptureListener(object: Visualizer.OnDataCaptureListener {
                override fun onFftDataCapture(visualizer: Visualizer?, fft: ByteArray?, samplingRate: Int) {
                    fft?.let { data ->
                        if (!isDataEmpty(data)) {
                            println("Has audio")
                        }
                    }
                }

                override fun onWaveFormDataCapture(visualizer: Visualizer?, waveform: ByteArray?, samplingRate: Int) {
                    println(waveform)
                }
            }, (Visualizer.getMaxCaptureRate() * 0.75).toInt(), false, true)
            v.enabled = true
        }
    }

    fun tearDownSampler() {
        println("Goodbye sampler")
        sampler?.let { v ->
            v.enabled = false
            v.release()
        }
        sampler = null
    }

    fun playNext() {
        println("BT Asking next track")
        btService!!.avPlayNext()
    }

    fun playPrev() {
        println("BT Asking prev track")
        btService!!.avPlayPrev()
    }

    private fun isDataEmpty(b: ByteArray): Boolean {
        b.forEach {
            if (it != 0x00.toByte()) {
                return false
            }
        }
        return true
    }
}