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
import com.rndash.mbheadunit.doom.objects.Scene
import com.rndash.mbheadunit.doom.renderer.ColourMap
import com.rndash.mbheadunit.doom.renderer.Vector3D
import com.rndash.mbheadunit.doom.wad.WadFile
import com.rndash.mbheadunit.doom.wad.mapData.*
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.cos
import kotlin.math.sin

@ExperimentalUnsignedTypes
@ExperimentalStdlibApi
class DoomGlView(private val w: WadFile, lName: String, private val ctx: Context) : GLSurfaceView.Renderer {
    private val mBytesPerFloat = 4
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

    private val mProjectionMatrix = FloatArray(16)
    private var mViewMatrix = FloatArray(16)


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

    private var cMap : Array<ColourMap>
    init {
        cMap = w.readPalette()
        level.things[0].let {
            ang = it.angle.toDouble()
            posX = it.xPos.toFloat()
            posY = it.yPos.toFloat()
            println("Player pos: ($posX,$posY) at $ang degrees")
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
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        Render.createProgram().let {
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
        //mTextureUniformHandle = glGetUniformLocation(mProgramHandle, "u_Texture")
        //mColourHandle = glGetAttribLocation(mProgramHandle, "a_Color")
        //mNormalHandle = glGetAttribLocation(mProgramHandle, "a_Normal")
        println("$mVMatrixHandle $mPositionHandle $mTextureCoordinateHandle $mSamplerHandle")

        traverse(level.nodes.size - 1, posX.toInt(), posY.toInt()) {
            genSubSector(it)
        }
        updatePos()
        startMusic()
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

    override fun onDrawFrame(gl: GL10?) {
        glClear(GL_DEPTH_BUFFER_BIT or GL_COLOR_BUFFER_BIT)
        glClearColor(0.3f, 0.3f, 0.3f, 0.5f)
        glUseProgram(mProgramHandle)
        glEnable(GL_DEPTH_TEST)
        glDepthFunc(GL_LEQUAL)
        glDepthMask(true)
        glActiveTexture(GL_TEXTURE0)
        meshes.forEach { m ->
            m.draw(
                mViewMatrix,
                mProjectionMatrix
            )
        }
        var err = glGetError()
        while(err != GL_NO_ERROR) {
            println("GL ERROR: $err")
            err = glGetError()
        }
    }

    private var eyeHeight = 32.0f
    fun updatePos() {
        println("Pos updated")
        eyeX = -posX
        eyeY = posZ
        eyeZ = posY

        val y = sin(ang * Math.PI / 180.0).toFloat()
        val x = cos(ang * Math.PI / 180.0).toFloat()

        dirX = x
        dirY = 0f
        dirZ = y
        Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, eyeX+dirX, eyeY+dirY, eyeZ+dirZ, 0f, 1f, 0f)
    }

    private var lastSector: Sector? = null
    fun onKeyDown(keyEvent: Int) {
        val angle = Math.toRadians(ang)
        when(keyEvent) {
            KEYCODE_W -> {
                posX += (-dirX) * 10.0f
                posY += (dirZ) * 10.0f
            }
            KEYCODE_S -> {
                posX -= (-dirX) * 10.0f
                posY -= (dirZ) * 10.0f
            }
            KEYCODE_Q -> { ang -= 10 }
            KEYCODE_E -> { ang += 10 }
            KEYCODE_Z -> {
                findSector(level.nodes.size-1)?.let {
                    if (it != lastSector) {
                        posZ = it.floorHeight.toFloat() + eyeHeight
                        lastSector = it
                    }
                }
                posZ += 10
            }
            KEYCODE_X -> {
                findSector(level.nodes.size-1)?.let {
                    if (it != lastSector) {
                        posZ = it.floorHeight.toFloat() + eyeHeight
                        lastSector = it
                    }
                }
                posZ -= 10
            }
        }
        updatePos()
    }

    private fun segSideDef(seg: Seg, line: LineDef) : SideDef? {
        if (seg.segSide.toInt() == 0) {
            return level.sideDefs[line.sideDefRight.toInt()]
        } else {
            if (line.sideDefLeft.toInt() == -1) {
                return null
            }
            return level.sideDefs[line.sideDefLeft.toInt()]
        }
    }

    private fun segOppositeSideDef(seg: Seg, line: LineDef) : SideDef? {
        if (seg.segSide.toInt() == 0) {
            if (line.sideDefLeft.toInt() == -1) {
                return null
            }
            return level.sideDefs[line.sideDefLeft.toInt()]
        } else {
            return level.sideDefs[line.sideDefLeft.toInt()]
        }
    }

    private fun intersects(x: Float, y: Float, box: Bbox): Boolean {
        return x.toInt() in (box.left .. box.right) && y.toInt() in (box.bottom .. box.top)
    }

    private fun findSector(id: Int) : Sector? {
        if (id and Scene.subsectorBit == Scene.subsectorBit) {
            val idx = (id.toUShort() and Scene.subsectorBit.toUShort().inv()).toInt()
            val ssector = level.subSectors[idx]
            for (segIdx in ssector.startSeg until ssector.startSeg + ssector.numSegs) {
                val seg = level.segs[segIdx]
                val linedef = level.lineDefs[seg.lineNum.toInt()]
                segSideDef(seg, linedef)?.let { return level.sectors[it.sectorRef.toInt()] }
                segOppositeSideDef(seg, linedef)?.let { return level.sectors[it.sectorRef.toInt()] }
            }

        }
        val node = level.nodes[id]
        if (intersects(posX, posY, node.bbox[0])) {
            return findSector(node.child[0].toInt())
        }
        if (intersects(posX, posY, node.bbox[1])) {
            return findSector(node.child[1].toInt())
        }
        return null
    }

    fun traverse(id: Int, x: Int, y: Int, action: (Int) -> Unit) {
        if (id and Scene.subsectorBit == Scene.subsectorBit) {
            return if (id == -1) {
                // First node in tree
                action(0)
            } else {
                // Which node are we rendering?
                action((id.toUShort() and Scene.subsectorBit.toUShort().inv()).toInt())
            }
        }
        val n = level.nodes[id]
        val side = pointOnSide(n, x, y)
        val sideIdx = n.child[side].toInt()
        traverse(sideIdx, x, y, action)
        val oppSide = side xor 1
        val oppSideIdx = n.child[oppSide].toInt()
        traverse(oppSideIdx, x, y, action)
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

    private fun genSubSector(ssectorID: Int) {
        val s = level.subSectors[ssectorID]
        var maxX = 0
        var minX = 99999
        var maxY = 0
        var minY = 99999
        (s.startSeg until s.startSeg + s.numSegs).forEach { i ->
            val sec = level.segs[i]
            genSeg(s, i)
            level.vertexes[sec.vertexStart.toInt()].let {
                if (it.x > maxX) { maxX = it.x.toInt() }
                if (it.x < minX) { minX = it.x.toInt() }
                if (it.y > maxY) { maxY = it.y.toInt() }
                if (it.y < minY) { minY = it.y.toInt() }
            }
            level.vertexes[sec.vertexEnd.toInt()].let {
                if (it.x > maxX) { maxX = it.x.toInt() }
                if (it.x < minX) { minX = it.x.toInt() }
                if (it.y > maxY) { maxY = it.y.toInt() }
                if (it.y < minY) { minY = it.y.toInt() }
            }
        }
    }

    private fun genSeg(subSector: SubSector, segID: Int) {
        val seg = level.segs[segID]
        val lineDef = level.lineDefs[seg.lineNum.toInt()]
        val sideDef = segSideDef(seg, lineDef) ?: return

        val sector = level.sectors[sideDef.sectorRef.toInt()]
        val oppSideDef = segOppositeSideDef(seg, lineDef)

        val ut = sideDef.uppperTexture
        val mt = sideDef.middleTexture
        val lt = sideDef.lowerTexture

        val start = level.vertexes[seg.vertexStart.toInt()]
        val end = level.vertexes[seg.vertexEnd.toInt()]

        if (ut != "-" && oppSideDef != null) {
            val oppSector = level.sectors[oppSideDef.sectorRef.toInt()]
            meshes.add(
                    Mesh(
                            Vector3D(-start.x, sector.ceilingHeight, start.y, 0.0f, 1.0f),
                            Vector3D(-start.x, oppSector.ceilingHeight, start.y, 0.0f, 0.0f),
                            Vector3D(-end.x, oppSector.ceilingHeight, end.y, 1.0f, 0.0f),

                            Vector3D(-end.x, oppSector.ceilingHeight, end.y, 1.0f, 0.0f),
                            Vector3D(-end.x, sector.ceilingHeight, end.y, 1.0f, 1.0f),
                            Vector3D(-start.x, sector.ceilingHeight, start.y, 0.0f, 1.0f)
                    ).apply {
                        cacheTexture(ut, w, cMap, sector.lightLevel.toInt())
                    }
            )
        }
        if (mt != "-") {
            meshes.add(
                    Mesh(
                            Vector3D(-start.x, sector.floorHeight, start.y, 0.0f, 1.0f),
                            Vector3D(-start.x, sector.ceilingHeight, start.y, 0.0f, 0.0f),
                            Vector3D(-end.x, sector.ceilingHeight, end.y, 1.0f, 0.0f),

                            Vector3D(-end.x, sector.ceilingHeight, end.y, 1.0f, 0.0f),
                            Vector3D(-end.x, sector.floorHeight, end.y, 1.0f, 1.0f),
                            Vector3D(-start.x, sector.floorHeight, start.y, 0.0f, 1.0f)
                    ).apply {
                        cacheTexture(mt, w, cMap, sector.lightLevel.toInt())
                    }
            )
        }
        if (lt != "-" && oppSideDef != null) {
            val oppSector = level.sectors[oppSideDef.sectorRef.toInt()]
            meshes.add(
                    Mesh(
                            Vector3D(-start.x, sector.floorHeight, start.y, 0.0f, 1.0f),
                            Vector3D(-start.x, oppSector.floorHeight, start.y, 0.0f, 0.0f),
                            Vector3D(-end.x, oppSector.floorHeight, end.y, 1.0f, 0.0f),

                            Vector3D(-end.x, oppSector.floorHeight, end.y, 1.0f, 0.0f),
                            Vector3D(-end.x, sector.floorHeight, end.y, 1.0f, 1.0f),
                            Vector3D(-start.x, sector.floorHeight, start.y, 0.0f, 1.0f)
                    ).apply {
                        cacheTexture(lt, w, cMap, sector.lightLevel.toInt())
                    }
            )
        }

    }
}