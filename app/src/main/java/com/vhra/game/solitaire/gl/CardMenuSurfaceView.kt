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
import com.vhra.game.solitaire.gl.utils.Rank
import com.vhra.game.solitaire.gl.utils.Suit
import javax.microedition.khronos.opengles.GL10
import javax.microedition.khronos.opengles.GL11

class CardMenuSurfaceView(context: Context) : GLSurfaceView(context)  {
    companion object {
        const val CARD_WIDTH = 0.64f
        const val CARD_HEIGHT = 1f
    }

    private val renderer: GlRenderer

    init {
        setEGLContextClientVersion(2)
        renderer = GlRenderer(context)
        setRenderer(renderer)
        loadCardModels()
    }

    var clickedCard: CardModel? = null
    var clickedPoint: Vec2? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            if (it.action == MotionEvent.ACTION_DOWN) {
                clickedCard = renderer.getCollisionCard(it.x, it.y)
                clickedCard?.translate(0f, 0f, -1f)
                return true
            } else if (it.action == MotionEvent.ACTION_MOVE) {

                return false
            } else if (it.action == MotionEvent.ACTION_UP) {
                clickedCard?.translate(0f, 0f, 0f)
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

    private fun loadCardModels() {
        val cards: MutableList<CardModel> = mutableListOf()
        cards.add(CardModel(CARD_WIDTH, CARD_HEIGHT, Rank.KING, Suit.DIAMONDS).apply {
//            setOnUpdate { }
        })
        cards.add(CardModel(CARD_WIDTH, CARD_HEIGHT, Rank.ACE, Suit.DIAMONDS).apply {
            setOnUpdate { matrix -> Matrix.translateM(matrix, 0, 0f, 1f + 0.02f, 0f) }
        })
        cards.add(CardModel(CARD_WIDTH, CARD_HEIGHT, Rank.TWO, Suit.DIAMONDS).apply {
            setOnUpdate { matrix -> Matrix.translateM(matrix, 0, CARD_WIDTH + 0.02f, 1f + 0.02f, 0f) }
        })
        cards.add(CardModel(CARD_WIDTH, CARD_HEIGHT, Rank.THREE, Suit.DIAMONDS).apply {
            setOnUpdate { matrix -> Matrix.translateM(matrix, 0, -(CARD_WIDTH + 0.02f), 1f + 0.02f, 0f) }
        })
        cards.add(CardModel(CARD_WIDTH, CARD_HEIGHT, Rank.FOUR, Suit.DIAMONDS).apply {
            setOnUpdate { matrix -> Matrix.translateM(matrix, 0, -(CARD_WIDTH + 0.02f), -(1f + 0.02f), 0f) }
        })
        cards.add(CardModel(CARD_WIDTH, CARD_HEIGHT, Rank.FIVE, Suit.DIAMONDS).apply {
            setOnUpdate { matrix -> Matrix.translateM(matrix, 0, -(CARD_WIDTH + 0.02f), 0f, 0f) }
        })
        cards.add(CardModel(CARD_WIDTH, CARD_HEIGHT, Rank.SIX, Suit.DIAMONDS).apply {
            setOnUpdate { matrix -> Matrix.translateM(matrix, 0, (CARD_WIDTH + 0.02f), 0f, 0f) }
        })
        cards.add(CardModel(CARD_WIDTH, CARD_HEIGHT, Rank.SEVEN, Suit.DIAMONDS).apply {
            setOnUpdate { matrix -> Matrix.translateM(matrix, 0, (CARD_WIDTH + 0.02f), -(1f + 0.02f), 0f) }
        })
        cards.add(CardModel(CARD_WIDTH, CARD_HEIGHT, Rank.EIGHT, Suit.DIAMONDS).apply {
            setOnUpdate { matrix -> Matrix.translateM(matrix, 0, 0f, -(1f + 0.02f), 0f) }
        })
        renderer.addModels(cards)
    }
}