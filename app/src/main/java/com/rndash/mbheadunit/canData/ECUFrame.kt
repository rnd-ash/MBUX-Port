package com.rndash.mbheadunit.canData

import com.rndash.mbheadunit.CarCanFrame

@ExperimentalUnsignedTypes
abstract class ECUFrame {
    abstract val name: String // CAN Frame name (ECU ID)
    abstract val id: Int // Can ID
    abstract val dlc: Int // Can DLC of ECU Frame
    abstract val signals: List<FrameSignal> // List of [FrameSignals] describing what data is in the frame

    open fun parseFrame(frame: CarCanFrame) {
        // check if frame is our frame, if it isn't, ignore it
        if (frame.canID != this.id) {
            return
        }
        // Also check DLCs! - Sometimes Arduino can corrupt certain data!
        if (frame.dlc != this.dlc) {
            return
        }
        val bs = frame.toBitArray()
        // Iterate over each signal, and let each signal extract its data
        signals.forEach {
            it.processBits(bs)
        }
    }

    /**
     * Creates a CAN Frame containing the data from signals
     */
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

    fun copy() = object : ECUFrame() {
        override val id: Int = this@ECUFrame.id
        override val dlc: Int = this@ECUFrame.dlc
        override val name: String = this@ECUFrame.name
        override val signals: List<FrameSignal> = this@ECUFrame.signals.map { it.copy() }
    }
}