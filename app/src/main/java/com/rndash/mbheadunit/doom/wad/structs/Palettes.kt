package com.rndash.mbheadunit.doom.wad.structs

@ExperimentalUnsignedTypes
class Palettes {
    val data = Array(14){Palette()}
}

@ExperimentalUnsignedTypes
data class RGB(val r: UByte, val g: UByte, val b: UByte)

@ExperimentalUnsignedTypes
class Palette {
    val red = Array<UByte>(256){0x00U}
    val green = Array<UByte>(256){0x00U}
    val blue = Array<UByte>(256){0x00U}

    fun getColour(index: Int) = RGB(red[index], green[index], blue[index])
}