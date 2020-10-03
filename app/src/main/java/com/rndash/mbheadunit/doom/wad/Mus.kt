package com.rndash.mbheadunit.doom.wad

import android.os.Environment
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.experimental.and

/**
 * Based on
 * https://github.com/Xeomuz/Doom-Port-Source-Code/blob/master/Gzdoom/src/mus2midi.cpp
 */
class Mus(d: ByteArray) {

    private val data: ByteBuffer
    init {
        if (d.take(4) != MUSMagic) {
            throw Exception("Not a MUS File!")
        }
        data = ByteBuffer.wrap(d).order(ByteOrder.LITTLE_ENDIAN)
    }

    // https://github.com/Xeomuz/Doom-Port-Source-Code/blob/master/Gzdoom/src/mus2midi.h
    companion object {
        val midiHead = byteArrayOf(
                'M'.toByte(),
                'T'.toByte(),
                'h'.toByte(),
                'd'.toByte(),
                0, 0, 0, 6,
                0, 0,
                0, 1,
                0, 70,
                'M'.toByte(),
                'T'.toByte(),
                'r'.toByte(),
                'k'.toByte(),
                0, 0, 0, 0,
                0, 255.toByte(), 81, 3, 0x07, 0xa1.toByte(), 0x20
        )

        val MIDI_SYSEX		= 0xF0		 // SysEx begin
        val MIDI_SYSEXEND	= 0xF7		 // SysEx end
        val MIDI_META		= 0xFF		 // Meta event begin
        val MIDI_META_TEMPO = 0x51
        val MIDI_META_EOT	= 0x2F		 // End-of-track
        val MIDI_META_SSPEC	= 0x7F		 // System-specific event

        val MIDI_NOTEOFF	= 0x80		 // + note + velocity
        val MIDI_NOTEON 	= 0x90		 // + note + velocity
        val MIDI_POLYPRESS	= 0xA0		 // + pressure (2 bytes)
        val MIDI_CTRLCHANGE = 0xB0		 // + ctrlr + value
        val MIDI_PRGMCHANGE = 0xC0		 // + new patch
        val MIDI_CHANPRESS	= 0xD0		 // + pressure (1 byte)
        val MIDI_PITCHBEND	= 0xE0		 // + pitch bend (2 bytes)



        val MUS_NOTEOFF: Byte = 0x00
        val MUS_NOTEON: Byte = 0x10
        val MUS_PITCHBEND: Byte = 0x20
        val MUS_SYSEVENT: Byte = 0x30
        val MUS_CTRLCHANGE: Byte = 0x40
        val MUS_SCOREEND: Byte = 0x60


        val MUSMagic = listOf(
                'M'.toByte(),
                'U'.toByte(),
                'S'.toByte(),
                0x1a
        )

        val CtrlTranslate = byteArrayOf(0, 0, 1, 7, 10, 11, 91, 93, 64, 67, 120, 123, 126, 127, 121)


    }

    data class MusHeader(
            val magic: Int,
            val songLen: Short,
            val songStart: Short,
            val numChans: Short,
            val numSecondaryChannels: Short,
            val numInstruments: Short,
            val pad: Short
    )

    fun readVarLen(startOffset: Int) : Pair<Int, Int> {
        var time = 0
        var offset = 0
        var t: Byte
        do {
            t = data[startOffset + offset++]
            time = (time shl 7) or (t and 127).toInt()
        } while ((t and 128.toByte()).toInt() != 0)
        return Pair(offset, time)
    }

    fun writeVarLen(t: Int): ByteArray {
        var time = t
        var buffer: Long = (t and 0x7F).toLong()

        while((time shl 7) > 0) {
            time = time shl 7
            buffer = (buffer shl 8) or 0x80 or (time and 0x7F).toLong()
        }
        val ba = ArrayList<Byte>()
        while(true) {
            ba.add((buffer and 0xFF).toByte())
            if ((buffer and 0x80) != 0L) {
                buffer = buffer shr 8
            } else {
                break
            }
        }
        return ba.toByteArray()
    }

    fun toMidi() : Boolean {
        val buffer = ArrayList<Byte>()
        data.position(0)
        val header = MusHeader(
                data.int,
                data.short,
                data.short,
                data.short,
                data.short,
                data.short,
                data.short
        )
        buffer.addAll(midiHead.toList())
        var chanUsed = ByteArray(16) { 100 }
        var lastVel = ByteArray(16) { 0x00 }

        var maxmus_p = 16 + header.songLen
        println("$header")
        var mus_p = 16 + header.songStart
        var event: Byte = 0
        var deltaTime = 0
        var status = 0
        var midStatus: Int = 0
        var midArgs: Int = 0
        var noOp = false
        var mid1: Byte = 0
        var mid2: Byte = 0

        while (mus_p < maxmus_p && (event and 0x70) != MUS_SCOREEND) {
            var channel: Int
            var t: Byte = 0
            event = data.get(mus_p++)

            if ((event and 0x70) != MUS_SCOREEND) {
                t = data.get(mus_p++)
            }
            channel = (event and 15).toInt()
            when {
                channel == 15 -> channel = 9
                channel >= 9 -> channel++
            }

            if (chanUsed[channel] == 0x00.toByte()) {
                chanUsed[channel] = 1
                buffer.add(0)
                buffer.add((0xB0 or channel).toByte())
                buffer.add(7)
                buffer.add(127)
            }
            midStatus = channel
            midArgs = 0
            noOp = false

            when(event and 0x70) {
                MUS_NOTEOFF -> {
                    midStatus = midStatus or MIDI_NOTEOFF
                    mid1 = t and 127
                    mid2 = 64
                }
                MUS_NOTEON -> {
                    midStatus = midStatus or MIDI_NOTEON
                    mid1 = t and 127
                    if (t.toInt() and 128 != 0) {
                        lastVel[channel] = data.get(mus_p++) and 127
                    }
                    mid2 = lastVel[channel]
                }
                MUS_PITCHBEND -> {
                    midStatus = midStatus or MIDI_PITCHBEND
                    mid1 = ((t and 1).toInt() shl 6).toByte()
                    mid2 = (t.toInt() shr 1).toByte() and 127
                }
                MUS_SYSEVENT -> {
                    if (t < 10 || t > 14) {
                        noOp = true
                    } else {
                        midStatus = midStatus or MIDI_CTRLCHANGE
                        mid1 = CtrlTranslate[t.toInt()]
                        mid2 = if (t.toInt() == 12) header.numChans.toByte() else 0
                    }
                }
                MUS_CTRLCHANGE -> {
                    when {
                        t.toInt() == 0 -> {
                            midArgs = 1
                            midStatus = midStatus or MIDI_PRGMCHANGE
                            mid1 = data.get(mus_p++) and 127
                            mid2 = 0
                        }
                        t in 1..9 -> {
                            midStatus = midStatus or MIDI_CTRLCHANGE
                            mid1 = CtrlTranslate[t.toInt()]
                            mid2 = data.get(mus_p++)
                        }
                        else -> noOp = true
                    }
                }
                MUS_SCOREEND -> {
                    midStatus = MIDI_META
                    mid1 = MIDI_META_EOT.toByte()
                    mid2 = 0
                }
                else -> {
                    System.err.println("Invalid state: ${event and 0x70}")
                    return false
                }
            }
            if (noOp) {
                midStatus = MIDI_META
                mid1 = MIDI_META_SSPEC.toByte()
                mid2 = 0
            }
            buffer.addAll(writeVarLen(deltaTime).toList())
            if (midStatus != status) {
                status = midStatus
                buffer.add(status.toByte())
            }
            buffer.add(mid1)
            if (midArgs == 0) {
                buffer.add(mid2)
            }
            if (event.toInt() and 128 != 0) {
                readVarLen(mus_p).let {
                    mus_p += it.first
                    deltaTime = it.second
                }
            } else {
                deltaTime = 0
            }
        }
        val trackLen = buffer.size - 22
        buffer[18] = ((trackLen shr 24) and 0xFF).toByte()
        buffer[18] = ((trackLen shr 16) and 0xFF).toByte()
        buffer[18] = ((trackLen shr 8) and 0xFF).toByte()
        buffer[18] = (trackLen and 0xFF).toByte()
        println("Read complete! Size: ${buffer.size} bytes")
        File(Environment.getExternalStorageDirectory(), "tmp.mid").apply {
            createNewFile()
            writeBytes(buffer.toByteArray())
        }
        return true
    }
}