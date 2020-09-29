package com.rndash.mbheadunit.doom.renderer

import kotlin.math.max
import kotlin.math.min

/**
 * Do 2 number ranges overlap?
 */
fun intersectsBox(x0: Int, y0: Int, x1: Int, y1: Int, x2: Int, y2: Int, x3: Int, y3: Int) =
        overlaps(x0, x1, x2, x3) && overlaps(y0, y1, y2, y3)

/**
 * Point of intersection of 2 lines
 * coords 'a' Line 1
 * coords 'b' Line 2
 */
fun intersects(xa1: Int, ya1: Int, xa2: Int, ya2: Int, xb1: Int, yb1: Int, xb2: Int, yb2: Int): Pair<Int, Int> =
        Pair(
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

fun crossProduct(x1: Int, y1: Int, x2: Int, y2: Int): Int = x1*y2 - x2*y1


fun overlaps(a0: Int, a1: Int, b1: Int, b2: Int): Boolean =
        min(a1, a1) <= max(b1, b2) && min(b1, b2) <= max(a0, a1)


fun pointSide(
        px: Int,
        py: Int,
        x1: Int,
        y1: Int,
        x2: Int,
        y2: Int
): Int = crossProduct(x1-x2, y1-y2, px-x1, py-y1)
