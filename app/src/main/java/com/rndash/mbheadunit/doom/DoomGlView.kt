package com.rndash.mbheadunit.doom

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.os.Environment
import android.view.KeyEvent.*
import android.widget.Toast
import com.rndash.mbheadunit.BTMusic
import com.rndash.mbheadunit.CanFrame
import com.rndash.mbheadunit.R
import com.rndash.mbheadunit.car.PartyMode
import com.rndash.mbheadunit.doom.objects.HudElement
import com.rndash.mbheadunit.doom.objects.StatusBar
import com.rndash.mbheadunit.doom.renderer.ColourMap
import com.rndash.mbheadunit.doom.wad.WadFile
import com.rndash.mbheadunit.doom.wad.mapData.*
import com.rndash.mbheadunit.nativeCan.canB.MRM_A1
import com.rndash.mbheadunit.nativeCan.canC.MS_210h
import com.rndash.mbheadunit.nativeCan.canC.SBW_232h
import com.rndash.mbheadunit.nativeCan.canC.SID_SBW
import com.rndash.mbheadunit.doom.objects.items.weapons.*
import com.rndash.mbheadunit.doom.objects.items.Weapon
import java.lang.Math.abs
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.cos
import kotlin.math.sin
import com.rndash.mbheadunit.car.KeyManager

@ExperimentalUnsignedTypes
@ExperimentalStdlibApi
class DoomGlView(private val w: WadFile, lName: String, private val ctx: Context) : GLSurfaceView.Renderer {
    private var playerHealth = 100
    private var playerArmour = 0
    init {
        BTMusic.unfocusBT()
    }

    private var meshes = arrayListOf<Mesh>()

    private var level = w.getLevel(lName)

    private var posX: Float = 0.0f
    private var posY: Float = 0.0f
    private var posZ: Float = 30.0f

    private var eyeX: Float = 0.0f
    private var eyeY: Float = 0.0f
    private var eyeZ: Float = 0.0f

    private var dirX: Float = 0.0f
    private var dirY: Float = 0.0f
    private var dirZ: Float = 0.0f
    private var ang = 0.0 // Radians

    private var cMap : Array<ColourMap> = w.readPalette()
    private var weapons = ArrayList<Weapon>()
    private var weaponIdx = 0

    private val mProjectionMatrix = FloatArray(16)
    private var mViewMatrix = FloatArray(16)
    private fun onShoot() {
        weapons[weaponIdx].shoot()
    }

    companion object {
        var mColourHandle: Int = 0
        var mProgramHandle: Int = 0
        var mPositionHandle: Int = 0
        var mNormalHandle: Int = 0
        var mTextureCoordinateHandle: Int = 0
        var mMVPMatrixHandle: Int = 0
        var mVMatrixHandle: Int = 0
        var mTextureUniformHandle: Int = 0
        var mLightPosHandle: Int = 0
        var mSamplerHandle: Int = 0
    }

    init {
        level.things[0].let {
            ang = it.angle.toDouble()
            posX = it.xPos.toFloat()
            posY = it.yPos.toFloat()
            println("Player pos: ($posX,$posY) at $ang degrees")
        }

    }

    private var mrm_frame: CanFrame? = null
    private var lastShootMillis = System.currentTimeMillis()
    private var shoot_stage = 0
    val carinput = Thread() {
        var shooting = false
        while(true) {
            MS_210h.get_pw().toFloat().let { perc ->
                if (perc != 0f) {
                    posX += (-dirX) * perc / 10.0f
                    posY += (dirZ) * perc / 10.0f
                }
            }
            mrm_frame = MRM_A1.get_frame()
            mrm_frame?.let {
                val is_right = it.getBitRange(28, 1) == 1
                val ang = it.getBitRange(29, 11).toFloat() / 125.0f
                dRot = if (ang > 1.5) { // Allow a small dead zone
                    when (is_right) {
                        true -> -ang
                        false -> ang
                    }
                } else {
                    0f
                }
            }
            SBW_232h.get_sid_sbw().let {
                // Pressed any shit paddle
                if (it != SID_SBW.EWM) {
                    onShoot()
                }
            }
            updatePos()
            Thread.sleep(50L)
        }
    }

    private fun selectNextWeapon() {
        var idx = weaponIdx + 1
        if (idx >= weapons.size) {
            idx = 0
        }
        weaponIdx = idx
        weapons[weaponIdx].onSelect()
    }

    private fun selectPrevWeapon() {
        var idx = weaponIdx - 1
        if (idx < 0) {
            idx = weapons.size - 1
        }
        weaponIdx = idx
        weapons[weaponIdx].onSelect()
    }

    private var currSector: Sector? = null
    private var currSectorRef: Short? = null
    val physThread = Thread() {
        while(true) {
            map.findSector(posX.toInt(), posY.toInt(), level.nodes.size-1)?.let {
                currSector = it.first
                currSectorRef = it.second
            }
            ang += dRot
            if (dFwd != 0.0f) {
                posX += (-dirX) * dFwd
                posY += (dirZ) * dFwd
            }
            if (dFwd > 0) {
                dFwd -= 0.25f
            } else if (dFwd < 0) {
                dFwd += 0.25f
            }
            updatePos()
            weapons[weaponIdx].update()
            Thread.sleep(100/6) // 60 updates per second
        }
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        // Set the OpenGL viewport to the same size as the surface.
        glViewport(0, 0, width, height)

        // Create a new perspective projection matrix. The height will stay the same
        // while the width will vary as per aspect ratio.
        val ratio = (width / height).toFloat()
        val left = -ratio
        val right = ratio
        val bottom = -1.0f
        val top = 1.0f
        val near = 1.0f
        val far = 10000.0f
        Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far)
        HudElement.setScreenDimensions(width, height)
    }

    private val map = GLMap(level, w, cMap)
    private val sBar = StatusBar(w)
    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        Renderer.createProgramDOOM().let {
            if (it == 0) {
                throw Exception("Error creating GL Program")
            } else {
                println("GL program created!")
                mProgramHandle = it
            }
        }
        glUseProgram(mProgramHandle)
        mPositionHandle = glGetAttribLocation(mProgramHandle, "a_Position")
        mTextureCoordinateHandle = glGetAttribLocation(mProgramHandle, "a_texcoords")
        //mSamplerHandle = glGetUniformLocation(mProgramHandle, "u_sampler")
        mMVPMatrixHandle = glGetUniformLocation(mProgramHandle, "u_MVPMatrix")
        //mVMatrixHandle = glGetUniformLocation(mProgramHandle, "u_MVMatrix")
        //mLightPosHandle = glGetUniformLocation(mProgramHandle, "u_LightPos")
        //mTextureUniformHandle = glGetUniformLocation(mProgramHandl
        // e, "u_Texture")
        //mColourHandle = glGetAttribLocation(mProgramHandle, "a_Color")
        //mNormalHandle = glGetAttribLocation(mProgramHandle, "a_Normal")
        println("$mVMatrixHandle $mPositionHandle $mTextureCoordinateHandle $mSamplerHandle")
        // Register weapons
        weapons.add(Pistol(w, cMap[0], ctx))
        weapons.add(Chaingun(w, cMap[0], ctx))
        weapons.add(Plasma(w, cMap[0], ctx))
        weapons.add(BFG9000(w, cMap[0], ctx))
        weapons[weaponIdx].onSelect()
        // Add click handler for steering wheel to seek next / prev weapons
        KeyManager.registerPageUpListener(KeyManager.KEY.PAGE_UP, object : KeyManager.KeyListener {
            override fun onLongPress(pg: KeyManager.PAGE) {
                println("Page up long press. Page: $pg")
            }

            override fun onShortPress(pg: KeyManager.PAGE) {
                selectNextWeapon()
            }
        })

        KeyManager.registerPageUpListener(KeyManager.KEY.PAGE_DOWN, object : KeyManager.KeyListener {
            override fun onLongPress(pg: KeyManager.PAGE) {
                println("Page up long press. Page: $pg")
            }

            override fun onShortPress(pg: KeyManager.PAGE) {
                selectPrevWeapon()
            }
        })

        KeyManager.registerPageUpListener(KeyManager.KEY.TEL_ANSWER, object : KeyManager.KeyListener {
            override fun onLongPress(pg: KeyManager.PAGE) {
                println("Page up long press. Page: $pg")
            }

            override fun onShortPress(pg: KeyManager.PAGE) {
                DisplayManager.changeMode(true)
            }
        })

        KeyManager.registerPageUpListener(KeyManager.KEY.TEL_DECLINE, object : KeyManager.KeyListener {
            override fun onLongPress(pg: KeyManager.PAGE) {
                println("Page up long press. Page: $pg")
            }

            override fun onShortPress(pg: KeyManager.PAGE) {
                DisplayManager.changeMode(false)
            }
        })
        DisplayManager.init()

        map.genMap()
        sBar.setup()
        updatePos()
        startMusic()
        physThread.start()
        carinput.start()
        PartyMode.startThread()
        glUseProgram(mProgramHandle)
        glEnable(GL_BLEND)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
    }

    private lateinit var mp: MediaPlayer
    private fun startMusic() {
        try {
            val music = w.getMidi(getMusicName(level.name))
            if (music) {
                mp = MediaPlayer.create(
                        ctx,
                        Uri.parse(
                                Environment.getExternalStorageDirectory().path + "/tmp.mid"
                        )
                )
                mp.isLooping = true
                mp.start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(ctx, "Unable to find music for ${level.name}", Toast.LENGTH_LONG).show()
        }
    }

    private var dFwd = 0.0f
    private var dRot = 0.0f
    override fun onDrawFrame(gl: GL10?) {
        glClear(GL_DEPTH_BUFFER_BIT or GL_COLOR_BUFFER_BIT)
        glEnable(GL_DEPTH_TEST)
        glActiveTexture(GL_TEXTURE0)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT)
        map.render(mViewMatrix, mProjectionMatrix, posX, posY)

        // Do 2D Rendering
        glDisable(GL_DEPTH_TEST)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)
        weapons[weaponIdx].draw()
        sBar.render(weapons[weaponIdx].getAmmo(), playerHealth, playerArmour)
    }

    private var eyeHeight = 32.0f
    fun updatePos() {
        eyeX = -posX
        val sec_height: Float? = currSector?.let { it.floorHeight + eyeHeight }
        sec_height?.let {
            if (abs(it - posZ) < 50) {
                posZ = it
            }
        }
        eyeY = posZ
        eyeZ = posY

        val y = sin(ang * Math.PI / 180.0).toFloat()
        val x = cos(ang * Math.PI / 180.0).toFloat()

        dirX = x
        dirY = 0f
        dirZ = y
        Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, eyeX+dirX, eyeY+dirY, eyeZ+dirZ, 0f, 1f, 0f)
    }

    fun onKeyDown(keyEvent: Int) {
        val angle = Math.toRadians(ang)
        when(keyEvent) {
            KEYCODE_W -> { dFwd += 5f }
            KEYCODE_S -> { dFwd -= 5f }
            KEYCODE_Q -> { dRot -= 2f }
            KEYCODE_E -> { dRot += 2f }
            KEYCODE_SPACE -> { onShoot() }
            KEYCODE_Z -> { selectPrevWeapon() }
            KEYCODE_X -> { selectNextWeapon() }
        }
        updatePos()
    }

    private fun intersects(x: Float, y: Float, box: Bbox): Boolean {
        return x.toInt() in (box.left .. box.right) && y.toInt() in (box.bottom .. box.top)
    }

    // Calculates which side the player is on
    // 0 - Front
    // 1 - Back
    private fun pointOnSide(node: Node, x: Int, y: Int): Int {
        val dx = x - node.x
        val dy = y - node.y
        val l = (node.dy.toInt() shr 16) * dx
        val r = (node.dx.toInt() shr 16) * dy
        return if (r < l) 0 else 1
    }
}