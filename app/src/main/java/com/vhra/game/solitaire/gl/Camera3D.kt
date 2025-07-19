package com.vhra.game.solitaire.gl

import android.opengl.GLES20
import android.opengl.GLU
import android.opengl.Matrix

class Camera3D (
   private val eye: FloatArray = FloatArray(3),
   private val center: FloatArray = FloatArray(3),
   private val up: FloatArray
) {
    val matrix: FloatArray by lazy {
        val viewMatrix = FloatArray(16)
        Matrix.setLookAtM(
            viewMatrix,
            0,
            eye[0], eye[1], eye[2],
            center[0], center[1], center[2],
            up[0], up[1], up[2]
        )
        viewMatrix
    }
}