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
