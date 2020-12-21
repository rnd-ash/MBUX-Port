package com.rndash.mbheadunit.beatsaber

interface Renderable {
    fun draw(viewMatrix: FloatArray, projectMatrix: FloatArray, camZ: Float): RenderPosition

    enum class RenderPosition {
        REDERED,
        BEHIND_CAMERA,
        DISTANT
    }
}