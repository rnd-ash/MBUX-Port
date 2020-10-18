package com.rndash.mbheadunit.doom.objects.items.weapons

import android.content.Context
import android.media.MediaPlayer
import com.rndash.mbheadunit.R
import com.rndash.mbheadunit.car.PartyMode
import com.rndash.mbheadunit.doom.objects.items.Weapon
import com.rndash.mbheadunit.doom.renderer.ColourMap
import com.rndash.mbheadunit.doom.wad.WadFile

@ExperimentalUnsignedTypes
@ExperimentalStdlibApi
class Pistol(w: WadFile, c: ColourMap, ctx: Context): Weapon(w, c,
        160,32,
        // Muzzle flash
        // Hand in front
        // ^^ - further to the right and up
        // Between 2 and 3
        // Firing?
        // Lift gun up (Post fire)
        listOf("PISFA0", "PISGA0", "PISGB0", "PISGC0", "PISGD0", "PISGE0")) {

    override val displayName: String = "PISTOL"
    override var ammoCount: Int = 100
    private var shootSound = MediaPlayer.create(ctx, R.raw.pistol)
    private var lastFireTime = System.currentTimeMillis() - 1000
    private var isShooting = false
    override fun onFire(): Boolean {
        return if (!isShooting) {
            shootSound.start()
            isShooting = true
            PartyMode.activateHazards(300)
            lastFireTime = System.currentTimeMillis()
            ammoCount--
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
        sprites[0].setPosition(160.toFloat() - sprites[0].w / 2 + 6, 76f)
    }

    override fun update() {
        if (isShooting && System.currentTimeMillis() - lastFireTime > 300) {
            PartyMode.activateHazards(0)
            shootSound.stop()
            shootSound.prepare()
            isShooting = false
        }
    }

    override fun draw() {
        super.sprites[1].draw()
        if (isShooting) {
            super.sprites[0].draw()
        }
    }
}