package com.rndash.mbheadunit

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaRecorder
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

    var sampler: MediaRecorder? = null
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
                KombiDisplay.setAudioHeaderText("BT Paused", arrayOf(KombiDisplay.TEXT_FMT.CENTER_JUSTIFIED, KombiDisplay.TEXT_FMT.FLASHING))
                KombiDisplay.setAudioSymbol(KombiDisplay.AUDIO_SYMBOL.PLAY, KombiDisplay.AUDIO_SYMBOL.NONE)
                KombiDisplay.setAudioBodyText("  ", arrayOf(KombiDisplay.TEXT_FMT.CENTER_JUSTIFIED))
            }
            true -> {
                KombiDisplay.setAudioBodyText(trackNameInternal, arrayOf(KombiDisplay.TEXT_FMT.CENTER_JUSTIFIED))
                KombiDisplay.setAudioSymbol(KombiDisplay.AUDIO_SYMBOL.UP_ARROW, KombiDisplay.AUDIO_SYMBOL.DOWN_ARROW)
                KombiDisplay.setAudioHeaderText("BT Playing", arrayOf(KombiDisplay.TEXT_FMT.LEFT_JUSTIFIED))
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
        sampler = MediaRecorder() // System sound channel
        sampler?.let { s ->
            s.setAudioSource(MediaRecorder.AudioSource.MIC)
            s.start()
            println("Sampler setup")
        }
    }

    fun tearDownSampler() {
        println("Goodbye sampler")
        sampler?.let { v ->
            v.stop()
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