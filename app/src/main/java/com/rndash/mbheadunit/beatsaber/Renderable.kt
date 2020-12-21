package com.rndash.mbheadunit.beatsaber

interface Renderable {
    fun draw(viewMatrix: FloatArray, projectMatrix: FloatArray, camZ: Float)
}