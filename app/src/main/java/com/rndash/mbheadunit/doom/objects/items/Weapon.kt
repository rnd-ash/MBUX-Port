package com.rndash.mbheadunit.doom.objects.items

import com.rndash.mbheadunit.doom.objects.HudElement
import com.rndash.mbheadunit.doom.renderer.ColourMap
import com.rndash.mbheadunit.doom.wad.WadFile
import com.rndash.mbheadunit.nativeCan.KombiDisplay

@ExperimentalUnsignedTypes
@ExperimentalStdlibApi
abstract class Weapon(w: WadFile, c: ColourMap, hudX: Int, hudy: Int, sprs: List<String>) {
    protected var sprites = sprs.map {
        val p = w.readPatch(it)
        HudElement(p.width.toFloat(), p.height.toFloat(), 0f, 0f).apply {
            cachePatch(p, c, 0x00)
            setPosition(hudX.toFloat() - p.width/2, hudy.toFloat())
        }

    }

    protected var currSpriteIdx = 0
    fun getAmmo(): Int = ammoCount
    abstract var ammoCount: Int

    abstract fun draw()

    abstract fun update()
    abstract val displayName: String

    fun onSelect() {
        KombiDisplay.setAudioSymbol(KombiDisplay.AUDIO_SYMBOL.UP_ARROW, KombiDisplay.AUDIO_SYMBOL.DOWN_ARROW)
        KombiDisplay.setAudioHeaderText("WEAPON", arrayOf(KombiDisplay.TEXT_FMT.CENTER_JUSTIFIED))
        KombiDisplay.setAudioBodyText(displayName, arrayOf(KombiDisplay.TEXT_FMT.CENTER_JUSTIFIED))
    }

    fun shoot() : Boolean {
        return if (ammoCount > 0) {
            onFire()
        } else {
            false
        }
    }

    protected abstract fun onFire(): Boolean
}