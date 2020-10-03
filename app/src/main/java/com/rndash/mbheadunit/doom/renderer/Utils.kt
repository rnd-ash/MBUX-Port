package com.rndash.mbheadunit.doom.renderer

import kotlin.math.max
import kotlin.math.min

/**
 * Do 2 number ranges overlap?
 */
fun intersectsBox(x0: Double, y0: Double, x1: Double, y1: Double, x2: Double, y2: Double, x3: Double, y3: Double) =
        overlaps(x0, x1, x2, x3) && overlaps(y0, y1, y2, y3)

/**
 * Point of intersection of 2 lines
 * coords 'a' Line 1
 * coords 'b' Line 2
 */
fun intersects(xa1: Double, ya1: Double, xa2: Double, ya2: Double, xb1: Double, yb1: Double, xb2: Double, yb2: Double): Vector =
        Vector(
            // X
            crossProduct(
                    crossProduct(xa1, ya1, xa2, ya2), (xa1 - xa2),
                    crossProduct(xb1, yb1, xb2, yb2), xb1 - xb2)
            / crossProduct(xa1 - xb1, ya1 - yb1, xb1 - xb2, yb1 - yb2)
            ,
            // Y
            crossProduct(
                    crossProduct(xa1, ya1, xa2, ya2), (ya1 - ya2),
                    crossProduct(xb1, yb1, xb2, yb2), yb1 - yb2)
            / crossProduct(xa1 - xb1, ya1 - yb1, xb1 - xb2, yb1 - yb2)
        )

fun crossProduct(x1: Double, y1: Double, x2: Double, y2: Double): Double = x1*y2 - x2*y1


fun overlaps(a0: Double, a1: Double, b1: Double, b2: Double): Boolean =
        min(a1, a1) <= max(b1, b2) && min(b1, b2) <= max(a0, a1)


fun pointSide(
        px: Double,
        py: Double,
        x1: Double,
        y1: Double,
        x2: Double,
        y2: Double
): Double = crossProduct(x1-x2, y1-y2, px-x1, py-y1)
