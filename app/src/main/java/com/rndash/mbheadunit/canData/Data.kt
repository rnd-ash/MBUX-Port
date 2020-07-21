package com.rndash.mbheadunit.canData

import com.rndash.mbheadunit.CarCanFrame

import java.lang.IndexOutOfBoundsException


@ExperimentalUnsignedTypes
class DataSignal(initialValue: Int, private val name: String, val offset: Int, val len: Int) {
    private var storedValue : Int = initialValue

    fun setValue(raw: Int) {
        this.storedValue = raw
    }

    override fun toString(): String {
        return "Signal $name - Value: $storedValue"
    }

    fun getValue() : Int {
        return storedValue
    }

    fun toBitSet() : Array<Boolean> {
        val allBits = Array(len){false}
        (0 until len).forEach {
            allBits[it] = ((this.storedValue shr it) and 1) == 1
        }
        return allBits

    }

    fun processBits(bs: Array<Boolean>) {
        if (offset + len > bs.size) {
            throw IndexOutOfBoundsException("$offset + $len > ${bs.size}")
        }
        var value: Int = 0
        (0 until len).forEach {
            if (bs[offset+it]) {
                value = value or (1 shl it)
            }
        }
        storedValue = value
    }
}


@ExperimentalUnsignedTypes
abstract class DataFrame {
    abstract val name: String // CAN Frame name (ECU ID)
    abstract val id: Int // Can ID
    abstract val dlc: Int // Can DLC of ECU Frame
    abstract val signals: List<DataSignal>

    fun parseFrame(frame: CarCanFrame) {
        if (frame.canID != this.id) {
            throw Exception("Invalid CAN ID for $name")
        }
        if (frame.dlc != this.dlc) {
            throw Exception("Invalid DLC for $name")
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
        return """
            Frame $name (ID: $id - DLC: $dlc)
            Data:
            ${signals.joinToString("\n")}
        """.trimIndent()
    }
}