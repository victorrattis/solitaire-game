package com.vhra.game.solitaire.gl

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.GLU
import android.opengl.Matrix
import com.vhra.game.solitaire.CardId
import com.vhra.game.solitaire.R
import com.vhra.game.solitaire.gl.utils.Logger.logd
import com.vhra.game.solitaire.gl.utils.ShaderLoader
import com.vhra.game.solitaire.gl.utils.TextureLoader
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.max
import kotlin.math.min

class GlRenderer(private val context: Context) : GLSurfaceView.Renderer {
    private lateinit var textureLoader: TextureLoader

    private var models: MutableList<CardModel> = mutableListOf()

    var projectionMatrix = FloatArray(16)
    var projection = FloatArray(16)
    var viewModel = FloatArray(16)
    var viewport = IntArray(4)

    var screenSize: IntArray = intArrayOf()

    fun addModels(models: MutableList<CardModel>) {
        this.models = models
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

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        logd("Viewport: 0, 0, $width, $height")
        GLES20.glViewport(0, 0, width, height)
        viewport = intArrayOf(0, 0, width, height)
        screenSize = intArrayOf(width, height)
        Matrix.setIdentityM(projection, 0)

        val farPoint = 16f
        val ratio: Float = width.toFloat() / height.toFloat()
        Matrix.frustumM(projection, 0, -ratio, ratio, -1f, 1f, 3f, farPoint + 0.001f)

        val camera = Camera3D(
            eye = floatArrayOf(0f, 0f, farPoint),
            center = floatArrayOf(0f, 0f, 0f),
            up = floatArrayOf(0f, 1f, 0f)
        )
        viewModel = camera.matrix
        Matrix.multiplyMM(projectionMatrix, 0, projection, 0, viewModel, 0)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        synchronized(models) {
            models.forEach { it.load(textureLoader) }

            models.forEach { it.update(projectionMatrix) }

            models.forEach { it.draw(shader) }
        }
    }

    fun getCardModel(cardId: CardId): CardModel? {
        models.forEach {
            if (it.cardId == cardId) {
                return it
            }
        }
        return null
    }

    fun getCollisionCard(x: Float, y: Float): CardModel? {
        val glPosition = convertGlCoordinateSystem(x, y)
        models.forEach {
            if (collisionRectangle(it.measure(), glPosition)) {
                return it
            }
        }
        return null
    }

    fun getCollisionCard(cardId: CardId, card: FloatArray, cards: Set<CardId>): CardModel? {
        models.forEach {
            if (it.cardId != cardId && cards.contains(it.cardId)
                && checkCollision(it.measure(), card)) {
                return it
            }
        }
        return null
    }

    fun unProject(point: Vec3): Vec4 {
        val result = Vec4.toArray()
        GLU.gluUnProject(
            point.x, point.y, point.z,
            viewModel, 0,
            projectionMatrix, 0,
            viewport, 0,
            result, 0
        )
        return result.toVec4()
    }

    fun checkCollision(rect1: FloatArray, rect2: FloatArray): Boolean {
        val x1_min = rect1[0]
        val y1_min = rect1[1]
        val x1_max = rect1[2]
        val y1_max = rect1[3]

        val x2_min = rect2[0]
        val y2_min = rect2[1]
        val x2_max = rect2[2]
        val y2_max = rect2[3]

        return x1_min < x2_max && // Check for horizontal overlap
                x1_max > x2_min && // Check for horizontal overlap
                y1_min < y2_max && // Check for vertical overlap
                y1_max > y2_min    // Check for vertical overlap
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

    fun orderToTop(cardId: CardId) {
        synchronized(models) {
            val oldIndex =
                models.indexOfFirst { it.cardId == cardId }// Find the current index of the item
            if (oldIndex != -1) { // Ensure the item exists in the list
                val itemToMove = models.removeAt(oldIndex) // Remove it from its old position
                models.add(0, itemToMove) // Add it to the end of the list
            }
        }
    }


}