package com.rndash.mbheadunit.doom.objects

@ExperimentalUnsignedTypes
class Mob {
    var x: Int = 0
    var y: Int = 0
    var z: Int = 0

    lateinit var sNext: Mob
    lateinit var sPrev: Mob


    // 0 - 360 degrees
    var angle: UInt = 0U


}