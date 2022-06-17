package com.avelycure.stars.engine

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.avelycure.stars.engine.StarConstants.INITIAL_Z_COORD
import kotlin.math.abs
import kotlin.math.roundToInt

class Render {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = 3f
    }

    fun draw(canvas: Canvas, model: Model) {

        //drawing black empty space
        paint.color = Color.BLACK
        paint.style = Paint.Style.FILL
        canvas.drawRect(0f, 0f, canvas.width.toFloat(), canvas.height.toFloat(), paint)

        //drawing stars
        paint.style = Paint.Style.STROKE
        for ((x, y, z, color) in model.getPoints()) {
            //3d projections on 2d surface(from https://habr.com/ru/post/342708/)
            val sx = canvas.width / 2f + canvas.width / 2f * x / z
            val sy = canvas.height / 2f + canvas.height / 2f * y / z
            val isx = sx.roundToInt()
            val isy = sy.roundToInt()
            if (isx < canvas.width && isx >= 0 && isy < canvas.height && isy >= 0) {
                //the closer the stars the brighter they are
                val colorGain =
                    (255f + z * (255f / abs(INITIAL_Z_COORD))) / 255f
                val colorR = color and 0xff0000 shr 16
                val colorG = color and 0xff00 shr 8
                val colorB = color and 0xff
                paint.color = (-0x1000000
                        or ((colorR * colorGain).toInt() shl 16
                        ) or ((colorG * colorGain).toInt() shl 8
                        ) or (colorB * colorGain).toInt())
                canvas.drawPoint(isx.toFloat(), isy.toFloat(), paint)
            }
        }
    }
}