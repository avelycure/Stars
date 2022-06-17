package com.avelycure.stars.engine

import android.graphics.Canvas
import android.view.SurfaceHolder
import android.view.SurfaceView
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class Engine(surfaceView: SurfaceView) {
    private var model: Model = Model()
    private var render: Render = Render()

    private lateinit var callback: SurfaceHolder.Callback
    private var surfaceHolder: SurfaceHolder? = null

    private val lock = ReentrantLock()
    private val condition = lock.newCondition()

    @Volatile
    private var stopped = false

    var time = System.nanoTime()

    private var threadRunnable = Runnable {
        while (!stopped) {
            val canvas = surfaceHolder?.lockCanvas()
            if (surfaceHolder == null || canvas == null) {
                lock.withLock {
                    try {
                        condition.await()
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }
            } else {
                val timeElapsed = System.nanoTime() - time
                time = System.nanoTime()
                model.update(timeElapsed)
                render.draw(canvas, model)
                surfaceHolder?.unlockCanvasAndPost(canvas)
            }
        }
    }

    init {
        val engineThread = Thread(threadRunnable, "EngineThread")
        engineThread.start()
        callback = object : SurfaceHolder.Callback {
            override fun surfaceCreated(surfaceHolder: SurfaceHolder) {
                this@Engine.surfaceHolder = surfaceHolder
                lock.withLock {
                    condition.signal()
                }
            }

            override fun surfaceChanged(surfaceHolder: SurfaceHolder, i: Int, i1: Int, i2: Int) {
                this@Engine.surfaceHolder = surfaceHolder
            }

            override fun surfaceDestroyed(surfaceHolder: SurfaceHolder) {
                this@Engine.surfaceHolder = null
            }
        }
        surfaceView.holder.addCallback(callback)
    }

    fun stop() {
        stopped = true
    }
}