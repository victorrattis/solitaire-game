package com.vhra.game.solitaire.gl

import android.opengl.GLES20
import android.opengl.Matrix
import com.vhra.game.solitaire.CardId
import com.vhra.game.solitaire.R
import com.vhra.game.solitaire.gl.anim.Animation
import com.vhra.game.solitaire.gl.utils.BufferUtils
import com.vhra.game.solitaire.gl.utils.CardTextureMapper.getBackCardTextureCoordinate
import com.vhra.game.solitaire.gl.utils.CardTextureMapper.getTextureCoordinate
import com.vhra.game.solitaire.gl.utils.TextureLoader
import java.nio.FloatBuffer
import java.nio.ShortBuffer

class CardModel (
    val width: Float,
    val height: Float,
    val cardId: CardId
) {
    companion object {
        const val COORDINATE_FOR_VERTEX = 2
        private const val COORDINATE_BYTES = 4
        const val VERTEX_BYTES = COORDINATE_FOR_VERTEX * COORDINATE_BYTES
    }

    private var isLoaded: Boolean = false


    var position: Vec3 = Vec3()
    var rotate = Vec3()

    fun flip() {
        rotate = rotate.copy(x = 180f)
    }

    private lateinit var vertexBuffer: FloatBuffer
    private var vertices = floatArrayOf()

    private lateinit var textCoordBuffer: FloatBuffer
    private var textCoordinates = floatArrayOf()

    private lateinit var indexBuffer: ShortBuffer
    private val indices = shortArrayOf(
        0, 1, 2,
        0, 2, 3,
        4, 6, 5,
        4, 7, 6
    )

    var mvp: FloatArray = FloatArray(16)

    private val numberOfVertices: Int = vertices.size / COORDINATE_FOR_VERTEX

    private fun getVertexCapacity(): Int = vertices.size * COORDINATE_BYTES

    private var texture: Int = 0
    private var backCardCoordinateTexture = floatArrayOf()
    private var frontCardCoordinateTexture = floatArrayOf()

    private var mTextureDataHandle: Int = 0

    private var area = floatArrayOf()

    var animation: Animation? = null

    fun setCardArea(width: Float, height: Float) {
        val halfWidth = width / 2.0f
        val halfHeight = height / 2.0f
        val startX = -halfWidth
        val startY = -halfHeight

        area = floatArrayOf(startX, startY, startX + width, startY + height)

        vertices = floatArrayOf(
            startX,  startY + height,
            startX, startY,
            startX + width, startY,
            startX + width,  startY + height,

            startX,  startY + height,
            startX, startY,
            startX + width, startY,
            startX + width,  startY + height
        )
    }

    private fun setBackCoordinateTexture(value: FloatArray) {
        backCardCoordinateTexture = value
    }

    private fun setFrontCoordinateTexture(value: FloatArray) {
        frontCardCoordinateTexture = value
    }

    private fun loadCoordinateTexture() {
        textCoordinates = floatArrayOf(
            // front
            frontCardCoordinateTexture[2], frontCardCoordinateTexture[3],
            frontCardCoordinateTexture[2], frontCardCoordinateTexture[1],
            frontCardCoordinateTexture[0], frontCardCoordinateTexture[1],
            frontCardCoordinateTexture[0], frontCardCoordinateTexture[3],

            // back
            backCardCoordinateTexture[2], backCardCoordinateTexture[3],
            backCardCoordinateTexture[2], backCardCoordinateTexture[1],
            backCardCoordinateTexture[0], backCardCoordinateTexture[1],
            backCardCoordinateTexture[0], backCardCoordinateTexture[3]
        )
    }

    private fun loadGeometry() {
        setCardArea(width, height)
        texture = R.drawable.card_textures
        setFrontCoordinateTexture(getTextureCoordinate(cardId.rank, cardId.suit))
        setBackCoordinateTexture(getBackCardTextureCoordinate())
    }

    fun load(textureLoader: TextureLoader) {
        if (isLoaded) return

        loadGeometry()
        loadCoordinateTexture()

        vertexBuffer = BufferUtils.createFloatBuffer(vertices, getVertexCapacity())
        indexBuffer = BufferUtils.createShortBuffer(indices, indices.size * 2)
        textCoordBuffer = BufferUtils.createFloatBuffer(textCoordinates, textCoordinates.size * 4)

        mTextureDataHandle = textureLoader.loadTexture(texture)
        isLoaded = true
    }

    fun update(projection: FloatArray) {
        Matrix.setIdentityM(mvp, 0)
        Matrix.translateM(mvp, 0, position.x, position.y, position.z)
        Matrix.multiplyMM(mvp, 0, projection, 0, mvp, 0)
//        Matrix.rotateM(mvp, 0, rotate.x, rotate.y, 0.5f, 0f)
        animation?.update(area, mvp)
    }

    fun draw(shader: Shader) {
        if (!isLoaded) return

        GLES20.glUniformMatrix4fv(
            shader.getUniform("mvp"),
            1,
            false,
            mvp,
            0
        )

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureDataHandle);
        GLES20.glUniform1i(shader.getUniform("uTexture"), 0)

        GLES20.glVertexAttribPointer(
            shader.getAttribute("aPosition"),
            COORDINATE_FOR_VERTEX,
            GLES20.GL_FLOAT,
            false,
            VERTEX_BYTES,
            vertexBuffer
        )

        GLES20.glVertexAttribPointer(
            shader.getAttribute("aTexCoord"),
            2,
            GLES20.GL_FLOAT,
            false,
            2 * 4,
            textCoordBuffer
        )

        GLES20.glDrawElements(
            GLES20.GL_TRIANGLES,
            indices.size,
            GLES20.GL_UNSIGNED_SHORT,
            indexBuffer
        )
    }

    fun rectangle(): FloatArray {
        val start = floatArrayOf(area[0], area[1], 0f, 1f)
        Matrix.multiplyMV(start, 0, mvp, 0, start, 0)
        return floatArrayOf(
            start[0]/start[3],
            start[1]/start[3],
            width,
            height
        )
    }

    fun measure(): FloatArray {
        val start = floatArrayOf(area[0], area[1], 0f, 1f)
        val end = floatArrayOf(area[2], area[3], 0f, 1f)

        Matrix.multiplyMV(start, 0, mvp, 0, start, 0)
        Matrix.multiplyMV(end, 0, mvp, 0, end, 0)
        return floatArrayOf(
            start[0]/start[3],
            start[1]/start[3],
            end[0]/end[3],
            end[1]/end[3]
        )
    }
}