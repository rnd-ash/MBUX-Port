package com.rndash.mbheadunit.doom.renderer

import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class Vector(var x: Double, var y: Double) {
    constructor() : this(0.0, 0.0)

    companion object {
        fun distance(left: Vector, right: Vector) = magnitude(right - left)

        fun magnitude(v: Vector) = sqrt(v.x * v.x + v.y * v.y)
    }

    fun rotate(radians: Double) {
        x = x * cos(radians) - y * sin(radians)
        y = x * sin(radians) + y * cos(radians)
    }

    operator fun plus(other: Vector) = Vector(x + other.x, y + other.y)
    operator fun plusAssign(other: Vector) {
        x += other.x
        y += other.y
    }

    operator fun minus(other: Vector) = Vector(x - other.x, y - other.y)
    operator fun minusAssign(other: Vector) {
        x -= other.x
        y -= other.y
    }

    operator fun plus(const: Double) = Vector(x + const, y + const)
    operator fun minus(const: Double) = Vector(x - const, y - const)

    operator fun div(const: Double) = Vector(x / const, y / const)
    operator fun divAssign(const: Double) {
        x /= const
        y /= const
    }

    operator fun times(other: Vector) = Vector(x * other.x, y * other.y)
    operator fun times(const: Double) = Vector(x * const, y * const)

    override fun equals(other: Any?) = if (other is Vector) other.x == x && other . y == y else false

    fun normalize(v: Vector) = Vector(v.x / magnitude(v), v.y / magnitude(v))
}