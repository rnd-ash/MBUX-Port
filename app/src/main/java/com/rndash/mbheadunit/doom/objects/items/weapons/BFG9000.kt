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
class BFG9000(w: WadFile, c: ColourMap, ctx: Context): Weapon(w, c,
        160,0,
        // Ball small = [0]
        // ball large = [1]
        // BFG IDLE = [2]
        // BFG GLOWING = [3]
        // BFG FIRE! = [4]
        listOf("BFGFA0", "BFGFB0", "BFGGA0", "BFGGB0", "BFGGC0")) {

    override val displayName: String = "BFG9000"
    override var ammoCount: Int = 100
    private var shootSound = MediaPlayer.create(ctx, R.raw.bfg_fire)
    private var lastFireTime = System.currentTimeMillis() - 1000
    private var isShooting = false
    private var flash1 = false
    override fun onFire(): Boolean {
        return if (!isShooting) {
            shootSound.start()
            isShooting = true
            lastFireTime = System.currentTimeMillis()
            ammoCount--
            true
        } else {
            false
        }
    }

    init {
        sprites[0].setPosition(160.toFloat() - sprites[0].w/2, 62f)
        sprites[1].setPosition(160.toFloat() - sprites[1].w/2, 56f)
    }

    var drawwBall = 0
    override fun update() {
        if (!isShooting) {
            currSpriteIdx = 2
            drawwBall = 0
            return
        }

        if (isShooting && System.currentTimeMillis() - lastFireTime < 500) { // Pre first bang (warm up)
            currSpriteIdx = 2
            PartyMode.activateFog(500)
            flash1 = false
        } else if (isShooting && System.currentTimeMillis() - lastFireTime < 750) { // First bang to second bang
            PartyMode.activateFog(0)
            if (!flash1) {
                PartyMode.activateHazards(100)
                flash1 = true
            }
            currSpriteIdx = 2
        } else if (isShooting && System.currentTimeMillis() - lastFireTime < 1000) { // Second bang to last woosh
            PartyMode.activateFog(500)
            currSpriteIdx = 3
            drawwBall = 1
        } else if (isShooting && System.currentTimeMillis() - lastFireTime < 1600) { // Last woosh to end
            PartyMode.activateDipped(500)
            currSpriteIdx = 3
            drawwBall = 2
        } else {
            PartyMode.activateHazards(0)
            PartyMode.activateDipped(0)
            PartyMode.activateFog(0)
            isShooting = false
            currSpriteIdx = 2
            drawwBall = 0
        }
    }

    override fun draw() {
        super.sprites[currSpriteIdx].draw()
        if (drawwBall == 1) {
            sprites[0].draw()
        } else if (drawwBall == 2) {
            sprites[1].draw()
        }
    }
}