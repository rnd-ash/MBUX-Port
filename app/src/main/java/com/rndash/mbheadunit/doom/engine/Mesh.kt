package com.rndash.mbheadunit.doom.engine

import com.rndash.mbheadunit.doom.wad.Int16
import com.rndash.mbheadunit.doom.wad.Int32


data class Point3(val X: Int, val Y: Int16, val Z: Int16, val U: Float, val V: Float)

class Mesh(val texture: String, val vao: Int32, val vbo: Int32, val count: Int32, val lightLevel: Float)