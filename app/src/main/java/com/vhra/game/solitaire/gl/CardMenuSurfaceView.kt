package com.vhra.game.solitaire.gl

import android.annotation.SuppressLint
import android.content.Context
import android.opengl.GLSurfaceView
import android.view.MotionEvent
import com.vhra.game.solitaire.SolitaireGame
import com.vhra.game.solitaire.gl.anim.Flipped
import com.vhra.game.solitaire.gl.utils.Logger.logd

class CardMenuSurfaceView(context: Context) : GLSurfaceView(context)  {
    companion object {
        const val CARD_WIDTH = 0.64f
        const val CARD_HEIGHT = 1f
    }

    private val renderer: GlRenderer
    private val game: SolitaireGame

    init {
        setEGLContextClientVersion(2)
        renderer = GlRenderer(context)
        setRenderer(renderer)

        game = SolitaireGame()
        game.startGame()

        loadCardModels()
    }

    var clickedCard: CardModel? = null
    var clickedPoint: Vec2? = null
    var previousCardPosition: Vec3? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when(event?.action) {
            MotionEvent.ACTION_DOWN -> {
                val temp = renderer.getCollisionCard(event.x, event.y)
                if (temp != null && game.isCardDraggable(temp.cardId)) {
                    clickedCard = temp
                    clickedPoint = Vec2(event.x, event.y)
                    previousCardPosition = clickedCard?.position
                }
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
            MotionEvent.ACTION_UP -> {
                if (clickedCard != null) {
                    val card = renderer.getCollisionCard(
                        clickedCard!!.cardId,
                        clickedCard!!.measure(),
                        game.getLastCardEachColumn()
                    )
                    if (card != null) {
                        clickedCard?.let {
                            it.position = card.position.copy(y = (card.position.y - 0.25f))
                            val nextColumn = game.getColumn(card.cardId)
                            val previousColumn = game.getColumn(clickedCard!!.cardId)
                            nextColumn?.let {
                                game.switchColumn(clickedCard!!.cardId, nextColumn)
                            }
                            previousColumn?.let {
                                var previousColumnCard = game.getCardColumn(previousColumn)
                                previousColumnCard?.let {
                                    previousColumnCard.isFaceUp = true
                                    renderer.getCardModel(previousColumnCard.id)?.let {
                                        it.animation = null
                                    }
                                }
                            }
                            renderer.orderToTop(clickedCard!!.cardId)
                        }
                    } else {
                        clickedCard?.let {
                            if (previousCardPosition != null) {
                                it.position = previousCardPosition!!
                            }
                        }
                    }
                }
                clickedCard = null
                clickedPoint = null
                previousCardPosition = null
                return false
            }
            else -> {
                clickedCard?.let {
                    if (previousCardPosition != null) {
                        it.position = previousCardPosition!!
                    }
                }
                clickedCard = null
                clickedPoint = null
                previousCardPosition = null
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
        val deck = mutableListOf<CardModel>()
        val y = 2.5f
        val xMargin = 0.07f
        val yMargin = 0.25f
        game.columns.forEachIndexed { column, cards ->
            val startX = (CARD_WIDTH + xMargin) * (-3 + column)
            for(row in (game.columns[column].size -1)downTo  0) {
                val card = game.columns[column][row]
                deck.add(CardModel(CARD_WIDTH, CARD_HEIGHT, card.id).apply {
                    position = Vec3(startX, y - (yMargin * row))
                    if (!card.isFaceUp) animation = Flipped()
                })
            }
        }

        val lastCard = game.pile.first()
        deck.add(CardModel(CARD_WIDTH, CARD_HEIGHT, lastCard).apply {
            val startX = (CARD_WIDTH + xMargin) * (-3 + 6)
            position = Vec3(startX, y + 1.5f)
            animation = Flipped()
        })

        renderer.addModels(deck)
    }
}