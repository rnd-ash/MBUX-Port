package com.rndash.mbheadunit.doom

import android.opengl.GLES11.glTexCoordPointer
import android.opengl.GLES20.*
import android.opengl.Matrix
import com.rndash.mbheadunit.doom.renderer.ColourMap
import com.rndash.mbheadunit.doom.renderer.Vector3D
import com.rndash.mbheadunit.doom.wad.WadFile
import com.rndash.mbheadunit.doom.wad.mapData.Thing
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import kotlin.random.Random

@ExperimentalUnsignedTypes
class Thing(t: Thing, vararg vecs: Vector3D) : Mesh(*vecs) {
    // TODO Things
}