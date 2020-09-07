package com.rndash.mbheadunit

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.media.*
import android.media.audiofx.Visualizer
import android.microntek.mtcser.BTServiceInf
import android.microntek.mtcser.BTServiceInfStub
import android.os.IBinder
import android.os.Parcel
import android.os.RemoteException
import android.util.Log
import com.rndash.mbheadunit.nativeCan.KombiDisplay
import java.lang.Exception
import kotlin.experimental.or
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

    var sampler: AudioRecord? = null
    val min_sample = AudioRecord.getMinBufferSize(48000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT)
    private var trackNameInternal: String by Delegates.observable("UNKNOWN") { _, o, n ->
        if (n != o) {
            KombiDisplay.setAudioBodyText(n, arrayOf(KombiDisplay.TEXT_FMT.CENTER_JUSTIFIED))
            KombiDisplay.setAudioSymbol(KombiDisplay.AUDIO_SYMBOL.NEXT_TRACK, KombiDisplay.AUDIO_SYMBOL.PREV_TRACK)
            KombiDisplay.setAudioHeaderText("BT Music", arrayOf(KombiDisplay.TEXT_FMT.LEFT_JUSTIFIED))
        }
    }
    private var trackArtist: String = "UNKNOWN"
    private var trackAlbum: String = "UNKNOWN"

    fun getTrackName() : String = trackNameInternal
    fun getTrackArtist() : String = trackArtist
    fun getTrackAlbum() : String = trackAlbum

    fun setArtist(a: String) { trackArtist = a }
    fun setName(t: String) { trackNameInternal = t }
    fun setAlbum(a: String) { trackAlbum = a }

    fun setupSampler() {
        println("Sampler begin")
        if (sampler != null) {
            println("Sampler already allocated!")
            return // Already allocated
        }
        sampler = AudioRecord(MediaRecorder.AudioSource.MIC, 48000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, min_sample) // System sound channel
        sampler?.let { s ->
            s.startRecording()
            println("Sampler setup")
        }
    }

    fun tearDownSampler() {
        println("Goodbye sampler")
        sampler?.let { s ->
            s.stop()
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

    fun getAmplitude(): Double {
        sampler?.let { s ->
            var total = 0.0
            val buf = ShortArray(min_sample)
            s.read(buf, 0, min_sample)
            buf.forEach { total += it.toDouble() }
            return total / min_sample
        }
        return 0.0
    }
}