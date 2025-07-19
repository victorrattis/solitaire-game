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
    var previousCardPosition: Vec3? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when(event?.action) {
            MotionEvent.ACTION_DOWN -> {
                clickedCard = renderer.getCollisionCard(event.x, event.y)
                clickedPoint = Vec2(event.x, event.y)
                previousCardPosition = clickedCard?.position
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                clickedPoint?.let { previous ->
                    val move = calculateTranslate(previous, Vec2(event.x, event.y))
                    clickedCard?.position = Vec3(
                        (previousCardPosition?.x ?: 0f) + move.x,
                        (previousCardPosition?.y ?: 0f) + move.y,
                        0.1f
                    )
                }
                return false
            }
            else -> {
                clickedCard?.position = clickedCard?.position?.copy(z = 0f) ?: Vec3()
                clickedCard = null
                clickedPoint = null
                return false
            }
        }
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