package com.rndash.mbheadunit.doom.things

import com.rndash.mbheadunit.doom.GLMap
import com.rndash.mbheadunit.doom.renderer.ColourMap
import com.rndash.mbheadunit.doom.wad.Patch
import com.rndash.mbheadunit.doom.wad.WadFile
import com.rndash.mbheadunit.doom.wad.mapData.Thing

@ExperimentalUnsignedTypes
@ExperimentalStdlibApi
class Enemy(t: Thing, l: GLMap) : AbstractThing(t, l) {
    override var frames = ArrayList<Pair<Patch, Int>>()

    override fun genSprites(w: WadFile, name: String, map: ColourMap) {

    }

    override fun physUpdate() {

    }
}