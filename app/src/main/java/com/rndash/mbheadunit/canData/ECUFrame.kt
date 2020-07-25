package com.rndash.mbheadunit.canData

import android.util.Log
import com.rndash.mbheadunit.CarCanFrame

import java.lang.IndexOutOfBoundsException



@ExperimentalUnsignedTypes
abstract class ECUFrame {
    abstract val name: String // CAN Frame name (ECU ID)
    abstract val id: Int // Can ID
    abstract val dlc: Int // Can DLC of ECU Frame
    abstract val signals: List<FrameSignal>

    open fun parseFrame(frame: CarCanFrame) {
        // check if frame is our frame, if it isn't, ignore it
        if (frame.canID != this.id) {
            return
        }
        if (frame.dlc != this.dlc) {
            return
        }
        val bs = frame.toBitArray()
        signals.forEach {
            it.processBits(bs)
        }
    }

    fun createCanFrame() : CarCanFrame {
        val bs = Array(this.dlc*8){false}
        signals.forEach {
            val b = it.toBitSet()
            (0 until it.len).forEach { i -> bs[it.offset+i] = b[i] }
        }
        return CarCanFrame(id, bs)
    }

    override fun toString(): String {
        return this.toRawString()
    }

    /**
     * Returns a raw string containing or the CAN Signals within the frame in their raw state
     */
    fun toRawString(): String {
        return """
            |Frame $name (ID: $id - DLC: $dlc)
            |Data:
            |${signals.joinToString("\n")}
        """.trimMargin("|")
    }
}