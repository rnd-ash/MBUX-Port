package com.rndash.mbheadunit.doom.renderer

import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class Vector3D(var x: Float, var y: Float, var z: Float, var u: Float, var v: Float) {
    constructor(x: Short, y: Short, z: Short, u: Float, v: Float) : this(x.toFloat(), y.toFloat(), z.toFloat(), u, v)
    constructor(x: Int, y: Int, z: Int, u: Float, v: Float) : this(x.toFloat(), y.toFloat(), z.toFloat(), u, v)
    constructor(x: Int, y: Short, z: Int, u: Float, v: Float) : this(x.toFloat(), y.toFloat(), z.toFloat(), u, v)
    constructor(x: Int, y: Short, z: Short, u: Float, v: Float) : this(x.toFloat(), y.toFloat(), z.toFloat(), u, v)

    fun toFloatArray() = floatArrayOf(x, y, z)
    fun toTexFA() = floatArrayOf(u, v) // Texture coordinates
}