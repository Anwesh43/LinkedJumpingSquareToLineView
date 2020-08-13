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
