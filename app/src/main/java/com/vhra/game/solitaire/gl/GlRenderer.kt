package com.vhra.game.solitaire.gl

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.util.Log
import com.vhra.game.solitaire.R
import com.vhra.game.solitaire.gl.anim.CardFlipAnimation
import com.vhra.game.solitaire.gl.utils.Logger.logd
import com.vhra.game.solitaire.gl.utils.ShaderLoader
import com.vhra.game.solitaire.gl.utils.TextureLoader
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.max
import kotlin.math.min

data class Vec2(val x: Float, val y: Float) {
    operator fun minus(a: Vec2): Vec2 = Vec2(x - a.x, y - a.y)
    fun toArray(): FloatArray = floatArrayOf(x, y)
}

data class Vec3(val x: Float = 0f, val y: Float = 0f, val z: Float = 0f) {
    fun toArray(): FloatArray = floatArrayOf(x, y, z)
    operator fun minus(a: Vec3): Vec3 = Vec3(x - a.x, y - a.y, z - a.z)
}

class GlRenderer(private val context: Context) : GLSurfaceView.Renderer {
    private lateinit var textureLoader: TextureLoader

    private val models: MutableList<CardModel> = mutableListOf()

    var projectionMatrix = FloatArray(16)

    var screenSize: IntArray = intArrayOf()

    fun addModels(models: List<CardModel>) {
        this.models.addAll(models)
    }

    private val shader = Shader(
        vertex = R.raw.vertex,
        fragment = R.raw.fragment
    )

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0.86f, 0.86f, 0.86f, 1.0f)

        textureLoader = TextureLoader(context)

        GLES20.glEnable(GLES20.GL_CULL_FACE)
        GLES20.glCullFace(GLES20.GL_BACK)

        // Enable z depth
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        GLES20.glDepthFunc(GLES20.GL_LESS)
        GLES20.glDepthMask(true)

        val shaderLoader = ShaderLoader(context)
        shaderLoader.load(shader)
        shader.dump()

        shader.useProgram()
    }

    var projection = FloatArray(16)
    var viewModel = FloatArray(16)

    fun getViewport(): IntArray = intArrayOf(
        0, 0, screenSize[0], screenSize[1])

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        logd("Viewport: 0, 0, $width, $height")
        GLES20.glViewport(0, 0, width, height)
        screenSize = intArrayOf(width, height)

        val ratio: Float = width.toFloat() / height.toFloat()
        Matrix.frustumM(projection, 0, -ratio, ratio, -1f, 1f, 3f, 100f)

        val camera = Camera3D(
            eye = floatArrayOf(0f, 0f, -10f),
            center = floatArrayOf(0f, 0f, 0f),
            up = floatArrayOf(0f, 1f, 0f)
        )
        viewModel = camera.matrix
        Matrix.multiplyMM(projectionMatrix, 0, projection, 0, camera.matrix, 0)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        models.forEach { it.load(textureLoader) }

        models.forEach { it.update(projectionMatrix) }

        models.forEach { it.draw(shader) }
    }

    fun getCollisionCard(x: Float, y: Float): CardModel? {
        val glPosition = convertGlCoordinateSystem(x, y)
        models.forEach {
            if (collisionRectangle(it.measure(), glPosition)) {
                it.measure().let{
                    Log.d("devlog", "collissed: ${it[0]}, ${it[1]}, ${it[2]}, ${it[3]} --> ${glPosition[0]}, ${glPosition[1]}")
                }

                return it
            }
        }
        return null
    }

    fun onTouchEvent(x: Float, y: Float) {
        // convert touch x and y to OpenGL coordinate system
        val glPosition = convertGlCoordinateSystem(x, y)

        models.forEach {
            val rectangle = it.measure()
            if (collisionRectangle(rectangle, glPosition)) {
                if (it.animation != null) {
                    if (it.animation?.isPlay() != false) it.animation?.stop()
                    else it.animation?.play()
                } else {
                    it.animation = CardFlipAnimation()
                    it.animation?.play()
                }
            }
        }
    }

    fun normalize(x: Float, y: Float): FloatArray {
        return floatArrayOf(x / screenSize[0], y / screenSize[1])
    }

    fun convertGlCoordinateSystem(x: Float, y: Float): FloatArray {
        return floatArrayOf(
            ((x / screenSize[0]) * 2f) - 1f,
            1f - ((y / screenSize[1]) * 2f)
        )
    }

    private fun collisionRectangle(rec: FloatArray, position: FloatArray): Boolean {
        return contain(min(rec[0], rec[2]), max(rec[0], rec[2]), position[0]) &&
                contain(min(rec[1], rec[3]), max(rec[1], rec[3]), position[1])
    }

    private fun contain(start: Float, end: Float, value: Float): Boolean = value in start..end
}