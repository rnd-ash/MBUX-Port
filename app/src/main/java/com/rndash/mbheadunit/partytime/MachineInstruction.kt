package com.rndash.mbheadunit.partytime

import android.graphics.Color

class Instruction(val opcode: OpCode, val operand: Long, val locx: Int, val locy: Int, val c: Int) {
    override fun toString(): String {
        return "Op: $opcode - V: $operand"
    }
}

enum class OpCode {
    DISPLAY_NOTE,
    BG_COLOUR_CHANGE,
    END_OF_SONG
}

val OpCodes = OpCode.values()