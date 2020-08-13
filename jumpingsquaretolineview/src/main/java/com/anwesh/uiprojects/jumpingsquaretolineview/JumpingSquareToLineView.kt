package com.anwesh.uiprojects.jumpingsquaretolineview

/**
 * Created by anweshmishra on 14/08/20.
 */

import android.view.View
import android.view.MotionEvent
import android.graphics.*
import android.app.Activity
import android.content.Context

val parts : Int = 2
val scGap : Float = 0.02f / parts
val colors : Array<String> = arrayOf("#009688", "#F44336", "#4CAF50", "#00BCD4", "#3F51B5")
val strokeFactor : Float = 90f
val sizeFactor : Float = 5.6f
val delay : Long = 20
val rot : Float = 90f

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n
fun Float.sinify() : Float = Math.sin(this * Math.PI).toFloat()

fun Canvas.drawJumpingSquareToLine(scale : Float, w : Float, h : Float, paint : Paint) {
    val size : Float = Math.min(w, h) / sizeFactor
    val sf : Float = scale.sinify()
    val sf1 : Float = sf.divideScale(0, 2)
    val sf2 : Float = sf.divideScale(1, 2)
    save()
    translate(w / 2, h / 2)
    drawLine(-w * 0.5f * sf1, 0f, w * 0.5f * sf1, 0f, paint)
    save()
    translate(0f, -h * 0.5f * (1 - sf2))
    scale(sf2, sf2)
    rotate(rot * sf2)
    drawRect(RectF(-size, -size, size, size), paint)
    restore()
    restore()
}

fun Canvas.drawJSLNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    paint.color = Color.parseColor(colors[i])
    paint.strokeCap = Paint.Cap.ROUND
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    drawJumpingSquareToLine(scale, w, h, paint)
}

class JumpingSquareToLineView(ctx : Context) : View(ctx) {

    private val renderer : Renderer = Renderer(this)

    override fun onDraw(canvas : Canvas) {
        renderer.render(canvas)
    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
            }
        }
        return true
    }

    data class State(var scale : Float = 0f, var dir : Float = 0f, var prevScale : Float = 0f) {

        fun update(cb : (Float) -> Unit) {
            scale += scGap * dir
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                cb(prevScale)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            if (dir == 0f) {
                dir = 1f - 2 * prevScale
                cb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(delay)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class JSTLNode(var i : Int, val state : State = State()) {

        private var next : JSTLNode? = null
        private var prev : JSTLNode? = null

        init {

        }

        fun addNeighbor() {
            if (i < colors.size - 1) {
                next = JSTLNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawJSLNode(i, state.scale, paint)
        }

        fun update(cb : (Float) -> Unit) {
            state.update(cb)
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }
        fun getNext(dir : Int, cb : () -> Unit) : JSTLNode {
            var curr : JSTLNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }

    data class JumpingSquareToLine(var i : Int) {

        private var curr : JSTLNode = JSTLNode(0)
        private var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            curr.draw(canvas, paint)
        }

        fun update(cb : (Float) -> Unit) {
            curr.update {
                curr = curr.getNext(dir) {
                    dir *= -1
                }
                cb(it)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            curr.startUpdating(cb)
        }
    }

    data class Renderer(var view : JumpingSquareToLineView) {

        private val animator : Animator = Animator(view)
        private val jsl : JumpingSquareToLine = JumpingSquareToLine(0)
        private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

        fun render(canvas : Canvas) {
            canvas.drawColor(Color.parseColor("#bdbdbd"))
            jsl.draw(canvas, paint)
            animator.animate {
                jsl.update {
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            jsl.startUpdating {
                animator.start()
            }
        }
    }
}