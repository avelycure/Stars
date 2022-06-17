package com.avelycure.stars.engine

import com.avelycure.stars.engine.StarConstants.INITIAL_Z_COORD
import com.avelycure.stars.engine.StarConstants.MOTION_SPEED

class Model {
    private val points = mutableListOf<Point>()

    fun update(elapsedTime: Long) {
        //cycle is needed to increase number of stars
        for (i in 0..2) {
            points.add(
                Point(
                    random(-1f, 1f), random(-1f, 1f), INITIAL_Z_COORD,
                    -0x1000000 or (random(180f, 255f).toInt() shl 16) or (random(
                        180f,
                        255f
                    ).toInt() shl 8) or random(180f, 255f).toInt()
                )
            )
        }
        for (point in points) {
            point.z += elapsedTime * MOTION_SPEED
        }
        val iterator = points.iterator()
        while (iterator.hasNext()) {
            val point = iterator.next()
            if (point.z >= 0) {
                iterator.remove()
            }
        }
    }

    private fun random(from: Float, to: Float): Float {
        return (from + (to - from) * Math.random()).toFloat()
    }

    fun getPoints(): List<Point>{
        return points
    }
}