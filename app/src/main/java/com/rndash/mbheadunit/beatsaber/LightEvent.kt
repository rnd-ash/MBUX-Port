package com.rndash.mbheadunit.beatsaber

import com.rndash.mbheadunit.car.PartyMode

@ExperimentalUnsignedTypes
@ExperimentalStdlibApi
class LightEvent(
    private val light: Light,    private val durationMs: Int,
    val timestampMs: Int
) {
    enum class Light {
        FOG,
        DIPPED
    }

    fun animate() {
        when(light) {
            Light.FOG -> PartyMode.activateFog(durationMs)
            Light.DIPPED -> PartyMode.activateDipped(durationMs)
        }
    }
}