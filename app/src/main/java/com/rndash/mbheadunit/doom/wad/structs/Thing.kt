package com.rndash.mbheadunit.doom.wad.structs

import com.rndash.mbheadunit.doom.wad.Int16

@ExperimentalUnsignedTypes
class Thing(val x_pos: Int16, val y_pos: Int16, val angle: Int16, val type: Int16, val flags: Int16) : Struct {
    companion object {
        const val FLAG_L_1_2 = 0x0001 // Thing is on skill level 1-2
        const val FLAG_L_3 = 0x0002   // Thing is on skill level 3
        const val FLAG_L_4_5 = 0x0004 // Thing is on skill level 4-5
        const val FLAG_DEAF = 0x0008  // Thing is deaf
        const val FLAG_MULTI_PLAYER = 0x0010 // Thing is NOT in single player mode
    }
}