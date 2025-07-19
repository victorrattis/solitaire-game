package com.vhra.game.solitaire.gl.anim

interface Animation {
    fun update(area: FloatArray, matrix: FloatArray)
    fun stop()
    fun play()
    fun isPlay(): Boolean
}