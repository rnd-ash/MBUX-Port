package com.rndash.mbheadunit.doom

import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

import android.opengl.GLES20
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import com.rndash.mbheadunit.doom.wad.*
import java.nio.Buffer
import java.nio.ByteBuffer
import java.nio.FloatBuffer
import java.nio.IntBuffer

@ExperimentalUnsignedTypes
@ExperimentalStdlibApi
class Renderer : GLSurfaceView.Renderer {
    private val subsectorBit = 0x8000

    private val vertex = """
        #version 330
        in vec3 vertex;
        in vec2 vertTexCoord;
        uniform mat4 MVP;
        out vec2 fragTexCoord;
        void main()
        {
            fragTexCoord = vertTexCoord;
            gl_Position = MVP * vec4(vertex, 1.0);
        }
    """.trimIndent()

    private val fragment = """
        #version 330
        uniform float LightLevel;
        uniform sampler2D tex;
        in vec2 fragTexCoord;
        out vec4 outColor;
        void main()
        {
            float alpha = texture(tex, fragTexCoord).a;
            if (alpha == 1.0) {
                outColor = texture(tex, fragTexCoord) * LightLevel;
            } else {
                discard;
            }
        }
    """.trimIndent()

    class Point3() {
        var X: Int16 = 0
        var Y: Int16 = 0
        var Z: Int16 = 0
        var U: Float = 0.0F
        var V: Float = 0.0F

        fun toFArray() : Array<Float> {
            return arrayOf(X.toFloat(), Y.toFloat(), Z.toFloat(), U, V)
        }
    }

    class Point(){
        var X: Int16 = 0
        var Y: Int16 = 0
    }
    class Mesh constructor(val texture: String, val vao: Int32, val vbo: Int32, val count: Int, val lightLevel: Float)
    class Scene() {
        lateinit var meshes: Map<Int, Array<Mesh>>
        lateinit var textures: Map<String, UInt32>
    }

    fun newMesh(texture: String, ll: Int16, vertices: Array<Point3>): Mesh {
        val vao : IntBuffer = IntBuffer.allocate(1)
        GLES30.glGenVertexArrays(1, vao)
        GLES30.glBindVertexArray(vao.get())

        val vbo : IntBuffer = IntBuffer.allocate(1)
        GLES30.glGenVertexArrays(1, vbo)
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vbo.get())

        val vbo_data_temp = ArrayList<Float>()
        vertices.forEach {
            vbo_data_temp.addAll(it.toFArray())
        }
        val vbo_data = FloatBuffer.wrap(vbo_data_temp.toTypedArray().toFloatArray())
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, vbo_data_temp.size*4, vbo_data, GLES30.GL_STATIC_DRAW)
        val vertexAttrib = IntBuffer.allocate(1)
        GLES30.glVertexAttribPointer(vertexAttrib.get(), 3, GLES30.GL_FLOAT, false, 5*4, ByteBuffer.allocate(0))
        GLES30.glEnableVertexAttribArray(vertexAttrib.get())

        val texCoordAttrib = IntBuffer.allocate(1)
        texCoordAttrib.put(0, 1)
        GLES30.glVertexAttribPointer(texCoordAttrib.get(), 2, GLES30.GL_FLOAT, false, 5*4, ByteBuffer.allocate(3*4))
        GLES30.glEnableVertexAttribArray(texCoordAttrib.get())

        return Mesh(texture, vao.get(), vbo.get(), vbo_data_temp.size, (ll.toFloat() / 255.0).toFloat())
    }

    fun genSubSector(w: WAD, l: Level, ssectorID: Int, scene: Scene) {
        val ssector = l.ssectors[ssectorID]
        (ssector.startSeg until ssector.startSeg+ssector.numSegs).forEach {
            genSeg(w, l, ssectorID, it, scene)
        }
    }

    fun genSeg(w: WAD, l: Level, ssectorID: Int, segID: Int, scene: Scene) {
        val seg = l.segs[segID]
        val lineDefId = seg.lineNum
        val meshes = scene.meshes[ssectorID]
        val lineDef = l.lineDefscd [lineDefId.toInt()]
        val sideDef = segSideDef(l, seg, lineDef)
    }

    fun segSideDef(l: Level, seg: Seg, linedef: LineDef) : SideDef {
        TODO("TEST")
    }


    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        GLES30.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
    }

    override fun onDrawFrame(unused: GL10) {
        GLES30.glClear(GLES20.GL_COLOR_BUFFER_BIT)
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        GLES30.glViewport(0, 0, width, height)
    }
}