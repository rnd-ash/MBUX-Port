package com.rndash.mbheadunit.doom.objects.items.weapons

import android.content.Context
import android.media.MediaPlayer
import com.rndash.mbheadunit.R
import com.rndash.mbheadunit.car.PartyMode
import com.rndash.mbheadunit.doom.DisplayManager
import com.rndash.mbheadunit.doom.objects.items.Weapon
import com.rndash.mbheadunit.doom.renderer.ColourMap
import com.rndash.mbheadunit.doom.wad.WadFile

@ExperimentalUnsignedTypes
@ExperimentalStdlibApi
class Plasma(w: WadFile, c: ColourMap, ctx: Context): Weapon(w, c,
        160,0,
        // Firing (Start)
        // Firing (End)
        // Idle
        // Reload
        listOf("PLSFA0", "PLSFB0", "PLSGA0")) {

    init {
        currSpriteIdx = 2
    }

    override val displayName: String = "PLASMA"
    override var ammoCount: Int = 100
    private var shootSound = MediaPlayer.create(ctx, R.raw.plasmafire)
    private var lastFireTime = System.currentTimeMillis() - 1000
    private var isShooting = false
    override fun onFire(): Boolean {
        return if (!isShooting) {
            shootSound.start()
            isShooting = true
            lastFireTime = System.currentTimeMillis()
            ammoCount--
            DisplayManager.setAmmo(ammoCount)
            true
        } else {
            false
        }
    }

    override fun update() {
        if (isShooting) {
            if (isShooting && System.currentTimeMillis() - lastFireTime < 200) {
                currSpriteIdx = 0
                PartyMode.activateFog(200)
            } else if (isShooting && System.currentTimeMillis() - lastFireTime < 400) {
                currSpriteIdx = 1
                PartyMode.activateDipped(200)
            } else {
                shootSound.stop()
                shootSound.prepare()
                isShooting = false
                currSpriteIdx = 2
            }
        }
    }

    override fun draw() {
        super.sprites[currSpriteIdx].draw()
    }
}