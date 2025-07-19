package com.vhra.game.solitaire.gl

import android.annotation.SuppressLint
import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.GLU
import android.opengl.Matrix
import android.util.Log
import android.view.MotionEvent
import com.vhra.game.solitaire.gl.anim.CardFlipAnimation
import javax.microedition.khronos.opengles.GL10
import javax.microedition.khronos.opengles.GL11

data class Vec2(val x: Float, val y: Float) {
    operator fun minus(a: Vec2): Vec2 = Vec2(x - a.x, y - a.y)
}

class CardMenuSurfaceView(context: Context) : GLSurfaceView(context)  {
    private val renderer: GlRenderer

    init {
        setEGLContextClientVersion(2)
        renderer = GlRenderer(context)
        setRenderer(renderer)
//        renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
    }

    var clickedCard: CardModel? = null
    var clickedPoint: Vec2? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            if (it.action == MotionEvent.ACTION_DOWN) {
                clickedCard = renderer.getCollisionCard(it.x, it.y)
                clickedPoint = Vec2(it.x, it.y)
                val m = FloatArray(16)
                Matrix.setIdentityM(m, 0)
                val r = renderer.convertGlCoordinateSystem(it.x, it.y)
                val current = floatArrayOf(r[0], r[1], 0f, 0f)
                Matrix.multiplyMV(current, 0, renderer.projectionMatrix, 0, current, 0)
                Log.d("devlog", "current: ${current[0]}, ${current[1]}")

//                clickedCard?.let {
//                    it.translate(1f, 0f)
//                }

                return true
            } else if (it.action == MotionEvent.ACTION_MOVE) {

                return false
            } else if (it.action == MotionEvent.ACTION_UP) {
                clickedCard?.translate(0f, 0f)
                clickedCard = null
                clickedPoint = null
//                if (clickedCard?.animation != null) {
//                    if (clickedCard?.animation?.isPlay() != false) clickedCard?.animation?.stop()
//                    else clickedCard?.animation?.play()
//                } else {
//                    clickedCard?.animation = CardFlipAnimation()
//                    clickedCard?.animation?.play()
//                }
                return true
            }
//            renderer.onTouchEvent(it.x, it.y)
        }
        return false //super.onTouchEvent(event)
    }
}