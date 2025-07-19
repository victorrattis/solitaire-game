package com.vhra.game.solitaire.gl

import android.annotation.SuppressLint
import android.content.Context
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.view.MotionEvent
import com.vhra.game.solitaire.gl.utils.Rank
import com.vhra.game.solitaire.gl.utils.Suit

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
                clickedPoint = Vec2(it.x, it.y)
                return true
            } else if (it.action == MotionEvent.ACTION_MOVE) {
                clickedPoint?.let { previous ->
                    val move = calculateTranslate(previous, Vec2(it.x, it.y))
                    clickedCard?.translate(move.x, move.y, 0.1f)
                }
                return false
            } else if (it.action == MotionEvent.ACTION_UP) {
//                if (clickedCard?.animation != null) {
//                    if (clickedCard?.animation?.isPlay() != false) clickedCard?.animation?.stop()
//                    else clickedCard?.animation?.play()
//                } else {
//                    clickedCard?.animation = CardFlipAnimation()
//                    clickedCard?.animation?.play()
//                }

                clickedCard?.translate(0f, 0f, 0f)
                clickedCard = null
                clickedPoint = null
                return true
            }
//            renderer.onTouchEvent(it.x, it.y)
        }
        return false //super.onTouchEvent(event)
    }

    private fun calculateTranslate(previousMouse: Vec2, currentMouse: Vec2): Vec3 {
        val ndcPreviousMouse = renderer.unProject(Vec3(previousMouse, 1f))
        val ndcCurrentMouse = renderer.unProject(Vec3(currentMouse, 1f))
        return Vec3(
            (ndcCurrentMouse.x - ndcPreviousMouse.x) / ndcPreviousMouse.w,
            (ndcPreviousMouse.y - ndcCurrentMouse.y) / ndcPreviousMouse.w
        )
    }

    private fun loadCardModels() {
        val cards: MutableList<CardModel> = mutableListOf()
        cards.add(CardModel(CARD_WIDTH, CARD_HEIGHT, Rank.KING, Suit.DIAMONDS).apply {
//            setOnUpdate { matrix -> Matrix.translateM(matrix, 0, -1.5920487f, -3.2761708f, 0f) }
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