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
class Chaingun(w: WadFile, c: ColourMap, ctx: Context): Weapon(w, c,
        160,32,

        listOf("CHGGA0", "CHGGB0", "CHGFA0", "CHGFB0")) {

    override val displayName: String = "CHAINGUN"
    override var ammoCount: Int = 999
    private var shootSound = MediaPlayer.create(ctx, R.raw.pistol)
    private var lastFireTime = System.currentTimeMillis() - 1000
    private var isShooting = false
    private var useFirst = false
    private var isFire = false
    override fun onFire(): Boolean {
        return if (!isShooting) {
            useFirst = !useFirst
            shootSound.start()
            isShooting = true
            isFire = true
            PartyMode.activateHazards(50)
            lastFireTime = System.currentTimeMillis()
            ammoCount--
            DisplayManager.setAmmo(ammoCount)
            currSpriteIdx++
            if (currSpriteIdx >= sprites.size) {
                currSpriteIdx = 0
            }
            true
        } else {
            false
        }
    }

    init {
        sprites[0].setPosition(160.5f - sprites[0].w / 2, 0f)
        sprites[1].setPosition(160.5f - sprites[1].w / 2, 0f)
        sprites[2].setPosition(160.5f - sprites[2].w / 2, 55f)
        sprites[3].setPosition(160.5f - sprites[3].w / 2, 55f)
    }

    override fun update() {
        if (isShooting && System.currentTimeMillis() - lastFireTime > 75) {
            isFire = false
        }
        if (isShooting && System.currentTimeMillis() - lastFireTime > 100) {
            PartyMode.activateHazards(0)
            shootSound.stop()
            shootSound.prepare()
            isShooting = false
            isFire = false
        }
    }

    override fun draw() {
        if (useFirst) {
            super.sprites[0].draw()
        } else {
            super.sprites[1].draw()
        }
        if (isFire) {
            if (useFirst) {
                super.sprites[2].draw()
            } else {
                super.sprites[3].draw()
            }
        }
    }
}