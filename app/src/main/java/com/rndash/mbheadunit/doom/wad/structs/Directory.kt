package com.rndash.mbheadunit.doom.wad.structs

import com.rndash.mbheadunit.doom.wad.Int32

data class Directory(val filePos: Int32, val size: Int32, val name: String) : Struct