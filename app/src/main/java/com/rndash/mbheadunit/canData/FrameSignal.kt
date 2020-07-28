package com.rndash.mbheadunit.canData

import java.lang.IndexOutOfBoundsException

@ExperimentalUnsignedTypes
class FrameSignal(private val name: String, val offset: Int, val len: Int) {
    private var storedValue : Int = 0

    fun setValue(raw: Int) {
        this.storedValue = raw
    }

    override fun toString(): String {
        return "Signal $name [Bits $offset - ${offset+len}] - Value: $storedValue"
    }

    fun getValue() : Int {
        return storedValue
    }

    fun toBitSet() : Array<Boolean> {
        val allBits = Array(len){false}
        (0 until len).forEach {
            allBits[len-1-it] = ((this.storedValue shr it) and 1) == 1
        }
        return allBits
    }

    fun processBits(bs: Array<Boolean>) {
        if (offset + len > bs.size) {
            throw IndexOutOfBoundsException("$offset + $len > ${bs.size}")
        }
        var value = 0
        (0 until len).forEach {
            value = (value shl 1) or (if (bs[offset+it]) 1 else 0)
        }
        storedValue = value
    }

    /**
     * Creates a copy of a FrameSignal, also copying its stored value
     */
    fun copy() : FrameSignal {
        return FrameSignal(name, offset, len).also { it.storedValue = this.storedValue }
    }
}