package com.rndash.mbheadunit

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.media.AudioManager
import android.media.audiofx.Visualizer
import android.microntek.mtcser.BTServiceInf
import android.microntek.mtcser.BTServiceInfStub
import android.os.IBinder
import android.os.Parcel
import android.os.RemoteException
import android.util.Log
import java.lang.Exception

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

    private var sampler: Visualizer? = null
    private var trackName: String = "UNKNOWN"
    private var trackArtist: String = "UNKNOWN"
    private var trackAlbum: String = "UNKNOWN"
    private var isPlaying: Boolean = false

    fun isPlaying() : Boolean = isPlaying
    fun getTrackName() : String = trackName
    fun getTrackArtist() : String = trackArtist
    fun getTrackAlbum() : String = trackAlbum

    fun setPlayState(p: Boolean) { isPlaying = p }
    fun setArtist(a: String) { trackArtist = a }
    fun setName(t: String) { trackName = t }
    fun setAlbum(a: String) { trackAlbum = a }

    fun playNext() {
        println("BT Asking next track")
        btService!!.avPlayNext()
    }

    fun playPrev() {
        println("BT Asking prev track")
        btService!!.avPlayPrev()
    }
}